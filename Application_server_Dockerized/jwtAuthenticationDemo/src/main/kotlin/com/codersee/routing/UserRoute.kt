package com.codersee.routing

import com.codersee.model.User
import com.codersee.repository.UserRepository
import com.codersee.routing.request.UserRequest
import com.codersee.routing.response.UserResponse
import com.codersee.service.LdapService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.userRoute(userRepository: UserRepository, ldapService: LdapService) {

  post {
    val userRequest = call.receive<UserRequest>()

    if (ldapService.findByUsername(userRequest.username)) {
      return@post call.respond(HttpStatusCode.Conflict, "User already exists in LDAP")
    }

    val created = ldapService.createUser(userRequest.username, userRequest.password)
    if (!created) {
      return@post call.respond(HttpStatusCode.InternalServerError, "Failed to create user in LDAP")
    }

    val newUser = User(
      id = UUID.randomUUID(),
      username = userRequest.username,
      hashedPassword = ""
    )
    userRepository.createUser(newUser)

    call.respond(HttpStatusCode.Created, "User ${newUser.username} created successfully")
  }

  authenticate("auth-jwt") {
    get {
      val users = userRepository.findAll()
      call.respond(users.map { UserResponse(it.id, it.username) })
    }
  }
}







