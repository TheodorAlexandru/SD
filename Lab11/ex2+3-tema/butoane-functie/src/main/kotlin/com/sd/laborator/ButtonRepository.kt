package com.sd.laborator

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "nr_apasari_butoane")
data class ButtonRepository (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "nume_buton", unique = true, false)
    var numeButon: String,

    @Column(name = "numar_apasari")
    var numarApasari: Int = 0
)