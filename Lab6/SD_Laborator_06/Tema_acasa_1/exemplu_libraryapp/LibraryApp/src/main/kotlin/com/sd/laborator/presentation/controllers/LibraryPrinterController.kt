package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.interfaces.ILibraryPrinterService
import com.sd.laborator.business.models.Book
import com.sd.laborator.business.models.Content
import com.sd.laborator.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class LibraryPrinterController {
    @Autowired
    private lateinit var _libraryDAOService: ILibraryDAOService

    @Autowired
    private lateinit var _libraryPrinterService: ILibraryPrinterService

    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent

    private lateinit var _amqpTemplate: AmqpTemplate

    @Volatile
    private var _lastResponse: String? = null

    @Autowired
    fun initTemplate(){
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    @RequestMapping("/print", method = [RequestMethod.GET])
    @ResponseBody
    fun customPrint(@RequestParam(required = true, name = "format", defaultValue = "") format: String): String {
        return when (format) {
            "html" -> _libraryPrinterService.printHTML(_libraryDAOService.getBooks())
            "json" -> _libraryPrinterService.printJSON(_libraryDAOService.getBooks())
            "raw" -> _libraryPrinterService.printRaw(_libraryDAOService.getBooks())
            else -> "Not implemented"
        }
    }


    @RequestMapping("/find", method = [RequestMethod.GET])
    @ResponseBody
    fun customFind(
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String
    ): String {
        if (author != "")
            return this._libraryPrinterService.printJSON(_libraryDAOService.findAllByAuthor(author))
        if (title != "")
            return this._libraryPrinterService.printJSON(_libraryDAOService.findAllByTitle(title))
        if (publisher != "")
            return this._libraryPrinterService.printJSON(_libraryDAOService.findAllByPublisher(publisher))
        return this._libraryPrinterService.printJSON(_libraryDAOService.getBooks())
    }


    @RequestMapping("/add", method = [RequestMethod.GET])
    @ResponseBody
    fun customAdd(
        @RequestParam(required = true, name = "author", defaultValue = "") author: String,
        @RequestParam(required = true, name = "title", defaultValue = "") title: String,
        @RequestParam(required = true, name = "publisher", defaultValue = "") publisher: String,
        @RequestParam(required = true, name = "text", defaultValue = "") text: String
    ): String {
        val newContent = Content(author, title, publisher, text)
        val newBook = Book(newContent)
        _libraryDAOService.addBook(newBook)
        return "Cartea '$title' scrisă de '$author' a fost adăugată cu succes în bibliotecă!"
    }

    @RequestMapping("/find-and-print", method = [RequestMethod.GET])
    @ResponseBody
    fun customPrintFormat(
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String,
        @RequestParam(required = true, name = "format", defaultValue = "") format: String
    ): String {
        var msg: String
        if(author != ""){
            msg = "request~author=$author&format=$format"
        }
        else if(title != ""){
            msg = "request~title=$title&format=$format"
        }
        else if(publisher != ""){
            msg = "request~publisher=$publisher&format=$format"
        }
        else{
            return "Parametru lipsa"
        }

        _lastResponse = null

        sendRequest(msg)

        repeat(50){
            if(_lastResponse == null) Thread.sleep(200)
        }

        return _lastResponse ?: "Timeout - nu a venit raspuns"
    }

    @RabbitListener(queues = ["\${libraryapp.rabbitmq.responsequeue}"])
    fun handleLibraryResponse(msg: String)
    {
        val pieces1 = msg.split("~")
        if(pieces1[0] == "MISS") {
            val piecesParam = pieces1[1].split("&")
            val piecesType = piecesParam[0].split("=")
            var books: Set<Book> = emptySet()
            if (piecesType[0] == "author") {
                books = _libraryDAOService.findAllByAuthor(piecesType[1])
            } else if (piecesType[0] == "title") {
                books = _libraryDAOService.findAllByTitle(piecesType[1])
            } else if (piecesType[0] == "publisher") {
                books = _libraryDAOService.findAllByPublisher(piecesType[1])
            }

            val piecesFormat = piecesParam[1].split("=")
            val result = when (piecesFormat[1]) {
                "html" -> _libraryPrinterService.printHTML(books)
                "json" -> _libraryPrinterService.printJSON(books)
                "raw" -> _libraryPrinterService.printRaw(books)
                else -> "nu exista acest format"
            }
            val query = pieces1[1]
            sendUpdate("update~$query~$result")
            _lastResponse = result
        }
        else if(pieces1[0] == "HIT"){
            val result = pieces1[2]
            _lastResponse = result
        }

    }

    fun sendRequest(msg: String)
    {
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyRequests(), msg)
    }

    fun sendUpdate(msg: String)
    {
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyUpdates(), msg)
    }
}