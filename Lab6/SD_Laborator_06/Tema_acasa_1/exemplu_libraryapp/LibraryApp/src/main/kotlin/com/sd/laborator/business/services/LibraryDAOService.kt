package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.models.Book
import com.sd.laborator.business.models.Content
import com.sd.laborator.persistence.repositories.LibraryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class LibraryDAOService : ILibraryDAOService {
    @Autowired
    private lateinit var _libraryRepo: LibraryRepository

    @PostConstruct
    override fun createLibraryTable() {
        _libraryRepo.createTable()
        if(_libraryRepo.getBooks().isEmpty()) {
            _libraryRepo.addBook(Book(
                Content(
                    "Jules Verne",
                    "Casa cu aburi",
                    "Albatros",
                    "Capitolul I. S-a pus un premiu pe capul unui om. Se ofera premiu de 2000 de lire ..."
                ))
            )
            _libraryRepo.addBook(Book(
                Content(
                    "Jules Verne",
                    "Insula Misterioasa",
                    "Teora",
                    "Partea intai. Naufragiatii vazduhului. Capitolul 1. Uraganul din 1865. ..."
                ))
            )
            _libraryRepo.addBook(Book(
                Content(
                    "Jules Verne",
                    "O calatorie spre centrul pamantului",
                    "Polirom",
                    "Cuvant Inainte. Imaginatia copiilor - zicea un mare poet romantic spaniol - este asemenea unui cal nazdravan, iar curiozitatea lor e pintenul ce-l fugareste prin lumea celor mai indraznete proiecte."
                ))
            )
            _libraryRepo.addBook(Book(
                Content(
                    "Jules Verne",
                    "Steaua Sudului",
                    "Corint",
                    "Nemaipomeniti sunt francezii astia! - Vorbiti, domnule, va ascult! ...."
                ))
            )
            _libraryRepo.addBook(Book(
                Content(
                    "Roberto Ierusalimschy",
                    "Programming in LUA",
                    "Teora",
                    "Preface. When Waldemar, Luiz, and I started the development of Lua, back in 1993, we could hardly imagine that it would spread as it did. ..."
                ))
            )
        }
    }

    override fun getBooks(): Set<Book> {
        return _libraryRepo.getBooks()
    }

    override fun addBook(book: Book) {
        _libraryRepo.addBook(book)
    }

    override fun findAllByAuthor(author: String): Set<Book> {
        return _libraryRepo.findByAuthor(author)
    }

    override fun findAllByTitle(title: String): Set<Book> {
        return _libraryRepo.findAllByTitle(title)
    }

    override fun findAllByPublisher(publisher: String): Set<Book> {
        return _libraryRepo.findAllByPublisher(publisher)
    }
}