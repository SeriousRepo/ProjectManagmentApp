package com.romk.projectmanagmentapp.Models

data class ExtendedGroupModel (
    val groupId: Int,
    val groupName: String,
    val leader: UserModel,
    val members: List<UserModel>
)