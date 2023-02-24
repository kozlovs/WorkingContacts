package ru.kozlovss.workingcontacts.domain.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
){
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authStateFlow: MutableStateFlow<AuthState>
    private val tokenKey = "TOKEN_KEY"
    private val idKey = "ID_KEY"

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0L)

        if (token == null || id == 0L) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putString(tokenKey, token)
            putLong(idKey, id)
            apply()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            apply()
        }
    }
}