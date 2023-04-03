package ru.kozlovss.workingcontacts.domain.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kozlovss.workingcontacts.data.userdata.dto.Token
import javax.inject.Inject

class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authStateFlow: MutableStateFlow<Token?>
    private val tokenKey = "TOKEN_KEY"
    private val idKey = "ID_KEY"

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0L)

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
        get() = prefs.getString(tokenKey, null)

    @Synchronized
    fun setAuth(token: Token) {
        _authStateFlow.value = token
        with(prefs.edit()) {
            putString(tokenKey, token.token)
            putLong(idKey, token.id)
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
}