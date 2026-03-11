package com.sd.laborator.interfaces

interface BlackzoneInterface {
    fun getServerLocation(): String
    fun isServerLocationBlacklisted(): Boolean
}