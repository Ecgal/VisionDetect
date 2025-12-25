package com.codersee.routing


import com.codersee.repository.PhotosRepository
import com.codersee.repository.UserRepository
import com.codersee.service.JwtService
import com.codersee.service.LdapService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting(
  jwtService: JwtService,
  userRepository: UserRepository,
  photoRepository:PhotosRepository,
) {
  val ldapService = LdapService()

  routing {
    route("/api/auth") {
      authRoute(jwtService, ldapService)
    }

    route("/api/user") {
      userRoute(userRepository, ldapService)
    }

    route("/api/upload") {
      uploadRoute(photoRepository)
    }
    // user looks through saved photos and sends their photo to get analyzed
    route("/api/analyze") {
      analyzeRoute(photoRepository)
    }

    staticFiles("/uploads", File("uploads"))
  }
}


fun extractPrincipalUsername(call: ApplicationCall): String? =
  call.principal<JWTPrincipal>()
    ?.payload
    ?.getClaim("username")
    ?.asString()



