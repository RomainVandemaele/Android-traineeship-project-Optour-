package com.example.optitrip.utils.forms

import com.example.optitrip.entities.Client
import com.example.optitrip.utils.annotations.Mail
import com.example.optitrip.utils.annotations.MinLength
import com.example.optitrip.utils.annotations.Required

/**
 * Validation of a registration form inherit from [FormModel]
 *
 * @property firstName
 * @property lastName
 * @property mail
 * @property username
 * @property password
 */
class RegisterForm(
    @Required
    val firstName: String,
    @Required
    val lastName: String,
    @Required
    @Mail
    val mail: String,
    @Required
    @MinLength(size = 5)
    val username: String,
    @Required
    @MinLength(size = 6)
    val password: String,

) : FormModel() {

    /**
     * Transform the dat in form as a client
     *
     * @return an object [Client]
     */
    fun toClient() : Client {
        return Client(null,username, firstName, lastName, mail, password,true)
    }
}