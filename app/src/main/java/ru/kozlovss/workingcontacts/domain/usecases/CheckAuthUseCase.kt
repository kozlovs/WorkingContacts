package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.error.catchExceptions
import javax.inject.Inject

class CheckAuthUseCase @Inject constructor(
    private val appAuth: AppAuth
) {
    fun execute(): Boolean = catchExceptions {
        return appAuth.isAuthenticated()
    }
}