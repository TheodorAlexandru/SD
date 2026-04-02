package com.sd.laborator.persistence.interfaces

import com.sd.laborator.business.models.Book

interface ILibraryRepository {

    fun createTable()

    fun getBooks(): Set<Book>
    fun addBook(book: Book)
    fun findByAuthor(author: String): Set<Book>
    fun findAllByTitle(title: String): Set<Book>
    fun findAllByPublisher(publisher: String): Set<Book>
}