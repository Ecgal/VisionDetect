package com.codersee.routing

import com.codersee.routing.request.LoginRequest
import com.codersee.service.JwtService
import com.codersee.service.LdapService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import javax.naming.Context
import javax.naming.directory.InitialDirContext
import java.util.Hashtable
import javax.naming.directory.BasicAttribute
import javax.naming.directory.BasicAttributes

// just use the open ldap authentication dont build this my self.
// add the sql lite to docker file  but also nto nessary to use the sql
// use the ldap url
// call the ldap authentication in the token creation then once confirmed give a token
//treat the ldap as another end point
//find by user name function
// import ldap. user password credientials

fun Route.authRoute(jwtService: JwtService, ldapService: LdapService) {

  post {
    val request = call.receive<LoginRequest>()

    if (ldapService.authenticate(request.username, request.password)) {
      val token = jwtService.createJwtToken(request)
      call.respond(HttpStatusCode.OK, mapOf("token" to token))
    } else {
      call.respond(HttpStatusCode.Unauthorized, "Invalid LDAP credentials")
    }
  }
}




