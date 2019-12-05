package com.example.selfbuy.data.entity.local

import com.example.selfbuy.data.entity.remote.TokenDto
import com.example.selfbuy.data.entity.remote.UserDto

object CurrentUser{
    var tokenDto: TokenDto? = null
    var userDto: UserDto? = null
}