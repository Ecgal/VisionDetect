package com.codersee

import com.codersee.database.DatabaseFactory
import com.codersee.plugins.configureSecurity
import com.codersee.plugins.configureSerialization
import com.codersee.repository.PhotosRepository

import com.codersee.repository.UserRepository
import com.codersee.routing.configureRouting
import com.codersee.service.JwtService
import com.codersee.service.UserService
import io.ktor.server.application.*

fun main(args: Array<String>) {
  io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
  DatabaseFactory.init()
  val userRepository = UserRepository()
  val photoRepository = PhotosRepository()
  val jwtService = JwtService(this, userRepository)


  configureSerialization()
  configureSecurity()
  configureRouting(jwtService, userRepository, photoRepository )
}
