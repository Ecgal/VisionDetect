package com.codersee.database

import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

object DatabaseFactory {
    private const val JDBC_URL = "jdbc:sqlite:/app/data/data.db"

    fun init() {
        Class.forName("org.sqlite.JDBC")
        DriverManager.getConnection(JDBC_URL).use { conn ->
            val ddl = this::class.java.classLoader
                .getResource("tables.sql")
                ?.readText()
                ?: throw IllegalStateException("Could not find database/tables.sql in resources")

            conn.createStatement().use { stmt ->
                stmt.executeUpdate(ddl)
            }
        }
    }

    fun getConnection(): Connection =
        DriverManager.getConnection(JDBC_URL)
}