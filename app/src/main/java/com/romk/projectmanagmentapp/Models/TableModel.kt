package com.romk.projectmanagmentapp.Models

data class TableModel (
    val id : Int,
    val name : String,
    val isPrivate : Boolean,
    var groupId : Int
)