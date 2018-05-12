package com.romk.projectmanagmentapp.Models

class SessionModel private constructor() {
    private object Holder {
        val instance = SessionModel()
    }

    companion object {
        val instance: SessionModel by lazy { Holder.instance }
    }

    lateinit var email : String
    lateinit var token : String
}