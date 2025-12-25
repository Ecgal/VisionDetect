package com.codersee.routing.response


import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import java.io.File
import java.util.*

fun Route.uploadRoute() {

    // Make sure the uploads directory exists
    val uploadDir = File("uploads")
    if (!uploadDir.exists()) uploadDir.mkdirs()

    post {
        val multipart = call.receiveMultipart()
        var filename: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val ext = File(part.originalFileName ?: "unknown").extension
                    filename = "upload_${UUID.randomUUID()}.$ext"
                    val file = File(uploadDir, filename!!)
                    part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyTo(it) } }
                }
                else -> {}
            }
            part.dispose()
        }

        if (filename == null) {
            call.respond(HttpStatusCode.BadRequest, "No file uploaded")
            return@post
        }

        call.respond(
            mapOf(
                "filename" to filename,
                "path" to "/uploads/$filename"
            )
        )
    }
}
