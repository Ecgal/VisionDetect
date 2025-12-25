package com.codersee.service

import com.codersee.model.User
import com.codersee.repository.UserRepository

class UserService(
  private val userRepository: UserRepository
) {
  fun register(user: User): Boolean {
    val exists = userRepository.findByUsername(user.username)
    return if (exists == null) {
      userRepository.createUser(user)
      true
    } else {
      false
    }
  }

  fun findByUsername(username: String): User? =
    userRepository.findByUsername(username)
}