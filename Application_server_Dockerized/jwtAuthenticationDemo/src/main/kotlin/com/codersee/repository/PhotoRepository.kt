package com.codersee.repository

import com.codersee.model.Photo
import com.codersee.database.DatabaseFactory
import java.sql.ResultSet
import java.util.*

class PhotosRepository {

    fun save(photo: Photo) {
        DatabaseFactory.getConnection().use { conn ->
            val stmt = conn.prepareStatement(
                "INSERT INTO photos (id, filename, owner_id, created_at) VALUES (?, ?, ?, ?)"
            )
            stmt.setString(1, photo.id.toString())
            stmt.setString(2, photo.filename)
            stmt.setString(3, photo.owner)
            stmt.setString(4, photo.createdAt)
            stmt.executeUpdate()
        }
    }

    fun findByOwner(ownerId: String): List<Photo> {
        DatabaseFactory.getConnection().use { conn ->
            val stmt = conn.prepareStatement("SELECT * FROM photos WHERE owner_id = ?")
            stmt.setString(1, ownerId)
            val rs = stmt.executeQuery()
            val photos = mutableListOf<Photo>()
            while (rs.next()) {
                photos.add(toPhoto(rs))
            }
            return photos
        }
    }

    private fun toPhoto(rs: ResultSet): Photo =
        Photo(
            id = UUID.fromString(rs.getString("id")),
            filename = rs.getString("filename"),
            owner = rs.getString("owner_id"),
            createdAt = rs.getString("created_at")
        )
}