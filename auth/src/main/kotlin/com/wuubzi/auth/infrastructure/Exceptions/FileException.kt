package com.wuubzi.auth.infrastructure.Exceptions

sealed class FileException(message: String) : RuntimeException(message)

class InvalidFileException(message: String) : FileException(message)
class MaliciousFileException(message: String) : FileException(message)
class VirusScanException(message: String) : FileException(message)
