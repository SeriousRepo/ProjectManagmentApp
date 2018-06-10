package com.romk.projectmanagmentapp.Models

data class TaskModel (
    val id: Int,
    val content: String,
    val isFinished: Boolean,
    val assignedTo: UserModel
)