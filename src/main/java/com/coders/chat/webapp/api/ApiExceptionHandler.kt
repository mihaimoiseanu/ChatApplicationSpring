package com.coders.chat.webapp.api

import com.coders.chat.model.exceptions.ApiValidationException
import com.coders.chat.model.exceptions.base.ApplicationException
import com.coders.chat.model.exceptions.base.ExceptionInfo
import com.coders.chat.model.exceptions.base.ExceptionTag
import org.hibernate.validator.internal.engine.path.PathImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.persistence.RollbackException
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class ApiExceptionHandler {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java)
        val knownConstrainViolations = mapOf(
                Pair("UC_USEREMAIL_COL", "Email already in use!")/*todo check this constraint*/
        )
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleAppException(exception: ApplicationException): ResponseEntity<Any> =
            logAndBuildExceptionResponse(exception)

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(exception: AccessDeniedException): ResponseEntity<Any> =
            logAndBuildExceptionResponse(
                    apiException = ApplicationException(
                            message = "Access denied!",
                            tag = ExceptionTag.FORBIDDEN,
                            throwable = exception
                    ),
                    printStackTrace = false)


    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException::class)
    private fun handleHibernateConstraintViolation(exception: org.hibernate.exception.ConstraintViolationException): ResponseEntity<Any> = logAndBuildExceptionResponse(
            apiException = ApplicationException(
                    message = knownConstrainViolations[exception.constraintName] ?: exception.localizedMessage,
                    tag = ExceptionTag.CONFLICT
            ))

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(exception: ConstraintViolationException): ResponseEntity<Any> {
        val toCollection = mutableListOf<ApiValidationException>()
        exception.constraintViolations.map {
            ApiValidationException(
                    obj = it.rootBeanClass.simpleName,
                    field = (it.propertyPath as PathImpl).leafNode.asString(),
                    message = it.message,
                    rejectedValue = it.invalidValue
            )
        }.toCollection(toCollection)
        return logAndBuildExceptionResponse(
                ApplicationException(
                        tag = ExceptionTag.INVALID_PARAMETER,
                        message = "Validation error! ",
                        subExceptions = toCollection
                )
        )
    }

    @ExceptionHandler(JpaSystemException::class)
    fun handleJpaSystemException(exception: RollbackException): ResponseEntity<Any> {
        var cause = exception.cause
        while (cause != null) {
            when (cause) {
                is ConstraintViolationException -> return handleConstraintViolationException(cause)
                is org.hibernate.exception.ConstraintViolationException -> return handleHibernateConstraintViolation(cause)
                else -> cause = cause.cause
            }
        }
        return logAndBuildExceptionResponse(ApplicationException(
                tag = ExceptionTag.UNSPECIFIED,
                throwable = exception
        ))
    }

    @ExceptionHandler(JpaObjectRetrievalFailureException::class)
    fun handleNotFoundException(e: JpaObjectRetrievalFailureException): ResponseEntity<Any> {
        return logAndBuildExceptionResponse(
                ApplicationException(
                        tag = ExceptionTag.NOT_FOUND,
                        throwable = e,
                        message = e.localizedMessage
                )
        )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleUniquesConstraintExceptions(e: DataIntegrityViolationException): ResponseEntity<Any> {
        var cause = e.cause
        while (cause != null) {
            when (cause) {
                is ConstraintViolationException -> return handleConstraintViolationException(cause)
                is org.hibernate.exception.ConstraintViolationException -> return handleHibernateConstraintViolation(cause)
                else -> cause = cause.cause
            }
        }

        return logAndBuildExceptionResponse(ApplicationException(
                tag = ExceptionTag.CONFLICT,
                throwable = e,
                message = "Unique constraint!"
        ))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodValidationException(exception: MethodArgumentNotValidException): ResponseEntity<Any> {
        val validationErrors = exception.bindingResult.allErrors.map {
            ApiValidationException(
                    obj = it.objectName,
                    field = (it as? FieldError)?.field ?: "",
                    message = it.defaultMessage ?: "Validation error",
                    rejectedValue = (it as? FieldError)?.rejectedValue
            )
        }

        LOGGER.warn("A validation exception was returned: {}", exception.message)
        return logAndBuildExceptionResponse(
                ApplicationException(
                        tag = ExceptionTag.INVALID_PARAMETER,
                        message = "Validation error!",
                        subExceptions = validationErrors
                )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(exception: Exception): ResponseEntity<Any> = logAndBuildExceptionResponse(ApplicationException(
            tag = ExceptionTag.UNSPECIFIED,
            throwable = exception
    ))

    private fun logAndBuildExceptionResponse(apiException: ApplicationException, printStackTrace: Boolean = true): ResponseEntity<Any> {
        LOGGER.debug("A {} HTTP was throws: {}", apiException.status, apiException.message)
        if (printStackTrace) {
            apiException.printStackTrace()
        }
        val httpStatus: HttpStatus = HttpStatus.valueOf(apiException.status?.value()!!)
        return ResponseEntity(ExceptionInfo(apiException), httpStatus)
    }
}