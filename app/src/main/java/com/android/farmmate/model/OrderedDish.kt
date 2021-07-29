package com.android.farmmate.model

class OrderedDish {
    var dishName: String? = null
    var price: String? = null
    var date: String? = null
    var customeName: String? = null
    var email: String? = null
    var phoneNumber: String? = null
    var imageUrl: String? = null
    var status: String? = null

    constructor() {}
    constructor(dishName: String?, price: String?, date: String?, customeName: String?, email: String?, phoneNumber: String?, imageUrl: String?, status: String?) {
        this.dishName = dishName
        this.price = price
        this.date = date
        this.customeName = customeName
        this.email = email
        this.phoneNumber = phoneNumber
        this.imageUrl = imageUrl
        this.status = status
    }
}