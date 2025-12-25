package com.codersee.repository

import com.codersee.model.User
import com.codersee.database.DatabaseFactory
import java.sql.ResultSet
import java.util.*

class UserRepository {

  fun createUser(user: User) {
    DatabaseFactory.getConnection().use { conn ->
      val stmt = conn.prepareStatement(
        "INSERT INTO users (id, username, hashed_password) VALUES (?, ?, ?)"
      )
      stmt.setString(1, user.id.toString())
      stmt.setString(2, user.username)
      stmt.setString(3, user.hashedPassword)
      stmt.executeUpdate()
    }
  }

  fun findByUsername(username: String): User? {
    DatabaseFactory.getConnection().use { conn ->
      val stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")
      stmt.setString(1, username)
      val rs = stmt.executeQuery()
      return if (rs.next()) toUser(rs) else null
    }
  }

  private fun toUser(rs: ResultSet): User =
    User(
      id = UUID.fromString(rs.getString("id")),
      username = rs.getString("username"),
      hashedPassword = rs.getString("hashed_password")
    )

  fun findAll(): List<User> {
    DatabaseFactory.getConnection().use { conn ->
      val stmt = conn.prepareStatement("SELECT * FROM users")
      val rs = stmt.executeQuery()

      val users = mutableListOf<User>()
      while (rs.next()) {
        users.add(toUser(rs))
      }
      return users
    }
  }

  fun findById(id: UUID): User? {
    DatabaseFactory.getConnection().use { conn ->
      val stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")
      stmt.setString(1, id.toString())
      val rs = stmt.executeQuery()
      return if (rs.next()) toUser(rs) else null
    }
  }
}