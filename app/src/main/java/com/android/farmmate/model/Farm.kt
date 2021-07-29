package com.android.farmmate.model

class Farm {
    var name: String? = null
    var description: String? = null
    var price: String? = null
    var imageUrl: String? = null
    var locationLink: String? = null
    var mobile: String? = null
    var priceOvernight: String? = null

    constructor() {}
    constructor(name: String?, description: String?, price: String?, imageurl: String?, mobile: String?, locationLink: String?, priceOvernight: String?) {
        this.name = name
        this.description = description
        this.price = price
        this.imageUrl = imageurl
        this.locationLink = imageurl
        this.mobile = imageurl
        this.priceOvernight = imageurl
    }

}