package com.android.farmmate.model

class User {
    var name: String? = null
    var address: String? = null
    var phoneNumber: String? = null
    var email: String? = null
    var role: String? = null
    var password: String? = null

    constructor() {}
    constructor(name: String?, address: String?, phoneNumber: String?, email: String?, password: String?, role: String?) {
        this.name = name
        this.address = address
        this.phoneNumber = phoneNumber
        this.email = email
        this.password = password
        this.role = role
    }
}