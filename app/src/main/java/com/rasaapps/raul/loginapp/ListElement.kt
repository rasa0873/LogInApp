package com.rasaapps.raul.loginapp

class ListElement {

    private var title: String? = null
    private var description: String? = null
    private var price: String? = null

    constructor(title: String?, description: String?, price: String?){
        this.title = title
        this.description = description
        this.price = price
    }

    fun getTitle() : String? {
        return title
    }

    fun getDescription() : String? {
        return description
    }

    fun getPrice() : String? {
        return price
    }

}