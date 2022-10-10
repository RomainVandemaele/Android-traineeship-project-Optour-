package com.example.optitrip.utils.forms

import com.example.optitrip.utils.annotations.MinLength
import com.example.optitrip.utils.annotations.Required

/**
 * Validation of a login form with username, password inherit from [FormModel]
 *
 * @property username a username
 * @property password a password
 */
class LogInForm(
    @Required
    @MinLength(size=5)
    val username: String,
    @Required
    @MinLength(size=6)
    val password: String)  : FormModel() {

}