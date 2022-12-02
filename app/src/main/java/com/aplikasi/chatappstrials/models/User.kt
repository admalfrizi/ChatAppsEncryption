package com.aplikasi.chatappstrials.models

class User {
    var name : String? = null
    var email: String? = null
    var img_profile : String? = null
    var uid: String? = null

    constructor(){}

    constructor(name: String?, email: String?, img_profile: String?, uid: String?){
        this.name = name
        this.email = email
        this.img_profile = img_profile
        this.uid = uid
    }
}