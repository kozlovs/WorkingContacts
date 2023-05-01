package ru.kozlovss.workingcontacts.domain.auth

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kozlovss.workingcontacts.entity.Token

class AppAuth(
    context: Context
) {
    private val prefs = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE)
    private val _authStateFlow: MutableStateFlow<Token?>


    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)

        if (token == null || id == 0L) {
            _authStateFlow = MutableStateFlow(null)
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(Token(id, token))
        }
    }

    val authStateFlow: StateFlow<Token?> = _authStateFlow.asStateFlow()

    val token: String?
        get() = prefs.getString(TOKEN_KEY, null)

    @Synchronized
    fun setAuth(token: Token) {
        _authStateFlow.value = token
        with(prefs.edit()) {
            putString(TOKEN_KEY, token.token)
            putLong(ID_KEY, token.id)
            apply()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = null
        with(prefs.edit()) {
            clear()
            apply()
        }
    }

    fun isAuthenticated() = authStateFlow.value != null

    companion object {
        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val ID_KEY = "ID_KEY"
        private const val PREF_AUTH = "auth"
    }
}