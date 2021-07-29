package com.android.farmmate.model

class Dish {
    var description: String? = null
    var imageUrl: String? = null
    var name: String? = null
    var price: String? = null

    constructor() {}
    constructor(description: String?, imageUrl: String?, name: String?, price: String?) {
        this.description = description
        this.imageUrl = imageUrl
        this.name = name
        this.price = price
    }
}