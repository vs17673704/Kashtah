package com.android.farmmate.model

class BookedFarm {
    var farmName: String? = null
    var price: String? = null
    var date: String? = null
    var customeName: String? = null
    var email: String? = null
    var phoneNumber: String? = null
    var paymentMode: String? = null
    var imageUrl: String? = null
    var status: String? = null
    var stay: String? = null

    constructor() {}
    constructor(farmName: String?, price: String?, date: String?, customeName: String?, email: String?, phoneNumber: String?, paymentMode: String?, imageUrl: String?, status: String?, stay: String? ) {
        this.farmName = farmName
        this.price = price
        this.date = date
        this.customeName = customeName
        this.email = email
        this.phoneNumber = phoneNumber
        this.paymentMode = paymentMode
        this.imageUrl = imageUrl
        this.status = status
        this.stay = stay
    }
}