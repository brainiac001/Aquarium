package com.idootech.aquarium.Model

class Products {
    var pname: String? = null
    var pdescription: String? = null
    var image: String? = null
    var category: String? = null
    var pid: String? = null
    var date: String? = null
    var time: String? = null
    var pno = 0

    constructor()
    constructor(
        pname: String?, image: String?, category: String?,
        pid: String?, date: String?, time: String?,
        pdescription: String?, pno: Int
    ) {
        this.pname = pname
        this.image = image
        this.category = category
        this.pid = pid
        this.date = date
        this.time = time
        this.pdescription = pdescription
        this.pno = pno
    }
}