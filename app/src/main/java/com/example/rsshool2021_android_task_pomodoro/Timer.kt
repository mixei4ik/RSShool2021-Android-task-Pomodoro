package com.example.rsshool2021_android_task_pomodoro

data class Timer(
    val id: Int,
    val initMs: Long,
    var currentMs: Long,
    var isStarted: Boolean
)