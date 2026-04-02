package com.sd.laborator.authentification.pojo

import jakarta.persistence.*

@Entity
@Table(name = "date_autentificare")
data class AuthData(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false)
    val username: String = "",

    @Column(nullable = false)
    val passwordHash: String = "",

    @Column(nullable = false)
    val passwordHash: String = "",

    @Column(nullable = false)
    val passwordHash: String = ""

)