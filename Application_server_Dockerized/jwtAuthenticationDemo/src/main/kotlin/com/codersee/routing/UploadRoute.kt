package com.codersee.routing

import com.codersee.model.Photo
import com.codersee.repository.PhotosRepository
import com.codersee.routing.response.PhotoResponse
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.div
import java.nio.file.Files.createDirectories
import java.util.*

val uploadDir = Path("uploads/")

fun Route.uploadRoute(photoRepository: PhotosRepository) {
    authenticate("auth-jwt") {
        get {
            val username = extractPrincipalUsername(call)
            if (username == null) {
                call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT")
                return@get
            }


            val userDir = uploadDir / username
            val files = userDir.toFile().listFiles()?.map {
                PhotoResponse(
                    filename = it.name,
                    path = "/api/upload//$username/${it.name}",
                    size = it.length()
                )
            } ?: emptyList()

            call.respond(HttpStatusCode.OK, files)
        }

        get("/{username}/{filename}") {
            val pathUsername = call.parameters["username"]!!
            val principal = call.principal<JWTPrincipal>()
            val tokenUsername = principal?.getClaim("username", String::class)

            if (tokenUsername != pathUsername) {
                call.respond(HttpStatusCode.Forbidden, "You can only access your own files")
                return@get
            }

            val filename = call.parameters["filename"]!!
            val file = (uploadDir / pathUsername / filename).toFile()

            if (!file.exists()) {
                call.respond(HttpStatusCode.NotFound, "File not found")
                return@get
            }

            call.respondFile(file)
        }
        val baseDir = File("uploads")
        if (!baseDir.exists()) baseDir.mkdirs()
        post("/{username}") {
            val username = call.parameters["username"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing username")
            val userDir = File(baseDir, username)
            if (!userDir.exists()) userDir.mkdirs()

            val multipart = call.receiveMultipart()
            var filename: String? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val ext = File(part.originalFileName ?: "unknown").extension
                        filename = "upload_${UUID.randomUUID()}.$ext"
                        val file = File(userDir, filename!!)
                        part.streamProvider().use { its ->
                            file.outputStream().buffered().use { its.copyTo(it) }
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (filename == null) {
                call.respond(HttpStatusCode.BadRequest, "No file uploaded")
                return@post
            }


            val photo = Photo(
                id = UUID.randomUUID(),
                filename = filename!!,
                owner = username,
                createdAt = System.currentTimeMillis().toString()
            )
            photoRepository.save(photo)
            println("Saved photo: ${photo.filename} for user $username")

            call.respond(
                mapOf(
                    "filename" to filename,
                    "path" to "/uploads/$username/$filename"
                )
            )
        }
    }
}