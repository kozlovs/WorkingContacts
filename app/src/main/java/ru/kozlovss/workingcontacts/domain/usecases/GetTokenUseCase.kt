package ru.kozlovss.workingcontacts.domain.usecases

import kotlinx.coroutines.flow.StateFlow
import ru.kozlovss.workingcontacts.data.userdata.dto.Token
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.error.catchExceptions
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(
    private val appAuth: AppAuth
) {
    fun execute(): StateFlow<Token?> = catchExceptions {
        return appAuth.authStateFlow
    }
}