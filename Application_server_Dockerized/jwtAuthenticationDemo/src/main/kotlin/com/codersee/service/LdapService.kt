package com.codersee.service

import javax.naming.Context
import javax.naming.NamingException
import javax.naming.directory.BasicAttribute
import javax.naming.directory.BasicAttributes
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls
import java.util.Hashtable

class LdapService(
    private val ldapUrl: String = System.getenv("LDAP_URL") ?: "ldap://ldap:389",
    private val baseDn: String = System.getenv("LDAP_BASE_DN") ?: "dc=ldap,dc=asd,dc=msd,dc=localhost",
    private val usersRdn: String = System.getenv("LDAP_USERS_RDN") ?: "ou=users",
    private val bindDn: String = System.getenv("LDAP_BIND_DN") ?: "cn=admin,dc=ldap,dc=asd,dc=msd,dc=localhost",
    private val bindPassword: String = System.getenv("LDAP_BIND_PASSWORD") ?: "shouldntMatter"
) {

    /**
     * Authenticates a user against LDAP by binding with their credentials.
     */
    fun authenticate(username: String, password: String): Boolean {
        var adminCtx: InitialDirContext? = null
        var userCtx: InitialDirContext? = null

        return try {
            adminCtx = InitialDirContext(adminEnv())

            val searchBase = "$usersRdn,$baseDn"
            val controls = SearchControls().apply {
                searchScope = SearchControls.SUBTREE_SCOPE
            }

            val results = adminCtx.search(searchBase, "(uid=$username)", controls)
            if (!results.hasMore()) {
                println("LDAP: user $username not found under $searchBase")
                return false
            }

            val userDn = results.next().nameInNamespace

            val userEnv = Hashtable<String, String>().apply {
                put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
                put(Context.PROVIDER_URL, ldapUrl)
                put(Context.SECURITY_AUTHENTICATION, "simple")
                put(Context.SECURITY_PRINCIPAL, userDn)
                put(Context.SECURITY_CREDENTIALS, password)
            }

            userCtx = InitialDirContext(userEnv)
            println("LDAP auth success for $username ($userDn)")
            true
        } catch (e: NamingException) {
            println("LDAP auth failed for $username: ${e.message}")
            false
        } finally {
            try { userCtx?.close() } catch (_: Exception) {}
            try { adminCtx?.close() } catch (_: Exception) {}
        }
    }

    /**
     * Creates a new user in LDAP under ou=users.
     */
    fun createUser(username: String, password: String): Boolean {
        var ctx: InitialDirContext? = null
        return try {
            ctx = InitialDirContext(adminEnv())

            val attrs = BasicAttributes(true)
            val objClass = BasicAttribute("objectClass")
            objClass.add("inetOrgPerson")
            attrs.put(objClass)
            attrs.put("sn", username)
            attrs.put("cn", username)
            attrs.put("uid", username)
            attrs.put("userPassword", password)

            val dn = "uid=$username,$usersRdn,$baseDn"
            ctx.createSubcontext(dn, attrs)
            println("LDAP: created user $username ($dn)")
            true
        } catch (e: Exception) {
            println("LDAP: failed to create user $username -> ${e.message}")
            false
        } finally {
            try { ctx?.close() } catch (_: Exception) {}
        }
    }

    /**
     * Searches LDAP to check if a user exists.
     */
    fun findByUsername(username: String): Boolean {
        var ctx: InitialDirContext? = null
        return try {
            ctx = InitialDirContext(adminEnv())
            val searchBase = "$usersRdn,$baseDn"
            val controls = SearchControls().apply {
                searchScope = SearchControls.SUBTREE_SCOPE
            }
            val results = ctx.search(searchBase, "(uid=$username)", controls)
            results.hasMore()
        } catch (e: Exception) {
            println("LDAP: error searching for user $username -> ${e.message}")
            false
        } finally {
            try { ctx?.close() } catch (_: Exception) {}
        }
    }

    private fun adminEnv(): Hashtable<String, String> =
        Hashtable<String, String>().apply {
            put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
            put(Context.PROVIDER_URL, ldapUrl)
            put(Context.SECURITY_AUTHENTICATION, "simple")
            put(Context.SECURITY_PRINCIPAL, bindDn)
            put(Context.SECURITY_CREDENTIALS, bindPassword)
        }
}
