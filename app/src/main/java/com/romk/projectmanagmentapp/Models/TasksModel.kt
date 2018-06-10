package com.romk.projectmanagmentapp.Models

data class TasksModel (
    val id: Int,
    val name: String,
    val tasks: List<TaskModel>
)