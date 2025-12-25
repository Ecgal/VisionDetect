package com.codersee.routing


import com.codersee.repository.PhotosRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import java.io.File
import java.util.*

fun Route.analyzeRoute(photoRepository: PhotosRepository) {

    val client = HttpClient(CIO)
    val modelServerUrl = System.getenv("MODEL_SERVER_URL") ?: "http://model-server:8001"

    authenticate("auth-jwt") {
        /**
         * Analyze an uploaded image by sending it to the model-server.
         */
        post {
            val principal = call.principal<JWTPrincipal>()
            val username = principal?.getClaim("username", String::class)

            if (username == null) {
                call.respond(HttpStatusCode.Unauthorized, "Missing or invalid JWT")
                return@post
            }

            val multipart = call.receiveMultipart()
            var uploadedFile: File? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val filename = "analysis_${UUID.randomUUID()}_${part.originalFileName}"
                    val file = File("uploads/$username/$filename")
                    file.parentFile.mkdirs()
                    part.streamProvider().use { its ->
                        file.outputStream().buffered().use { its.copyTo(it) }
                    }
                    uploadedFile = file
                }
                part.dispose()
            }

            val file = uploadedFile ?: run {
                call.respond(HttpStatusCode.BadRequest, "No file uploaded")
                return@post
            }

            // Send the uploaded image to the model-server
            val response: HttpResponse = client.submitFormWithBinaryData(
                url = "$modelServerUrl/analyze",
                formData = formData {
                    append("file", file.readBytes(), Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    })
                }
            )

            if (response.status != HttpStatusCode.OK) {
                call.respond(HttpStatusCode.InternalServerError, "Model server error: ${response.status}")
                return@post
            }

            // Save AI-generated image to local uploads folder
            val analyzedPath = "uploads/$username/ai_${file.name}"
            val analyzedFile = File(analyzedPath)
            analyzedFile.writeBytes(response.bodyAsChannel().toByteArray())

            call.respondFile(analyzedFile)
        }
    }
}
