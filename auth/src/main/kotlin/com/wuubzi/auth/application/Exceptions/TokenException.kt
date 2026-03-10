package com.wuubzi.auth.application.Exceptions

sealed class TokenException(message: String) : RuntimeException(message)

class TokenNotFoundException(message: String) : TokenException(message)
class TokenExpiredException(message: String) : TokenException(message)
class TokenRevokedException(message: String) : TokenException(message)