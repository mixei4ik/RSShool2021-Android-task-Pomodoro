package com.example.rsshool2021_android_task_pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), TimerListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    private val timerAdapter = TimersAdapter(this)
    private val timers = mutableListOf<Timer>()
    private var nextId = 0
    private var startTime = 0L
    private var jobTimer: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerAdapter
        }

        binding.addNewTimerButton.setOnClickListener {
            val currentMin: Long = if (binding.timeEdit.text.toString() == "") {
                0
            } else {
                binding.timeEdit.text.toString().toLong()
            }
            if (currentMin in 1..5999) {
                timers.add(
                    Timer(
                        nextId++,
                        currentMin * 60 * 1000,
                        currentMin * 60 * 1000,
                        false,
                        isFinished = false
                    )
                )
                timerAdapter.submitList(timers.toList())
            } else Toast.makeText(applicationContext, "range 1..5999", Toast.LENGTH_SHORT).show()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        var currentMsFS = 0L
        for (i in timers.indices) {
            if (timers[i].isStarted) {
                currentMsFS = timers[i].currentMs
            }
        }

        if (currentMsFS == 0L) return

        startTime = System.currentTimeMillis()
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
        startIntent.putExtra(CURRENT_MS, currentMsFS)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    override fun start(id: Int, currentMs: Long) {
        jobTimer?.cancel()
        getCountTimer(id, currentMs)
        changeTimer(id, currentMs, true, isFinished = false)
    }

    override fun stop(id: Int, currentMs: Long, isFinished: Boolean) {
        jobTimer?.cancel()
        changeTimer(id, currentMs, false, isFinished)
    }

    override fun delete(id: Int) {
        timers.remove(timers.find { it.id == id })
        timerAdapter.submitList(timers.toList())
    }

    private fun changeTimer(id: Int, currentMs: Long, isStarted: Boolean, isFinished: Boolean) {
        for (i in timers.indices) {
            timers[i] =
                Timer(
                    timers[i].id,
                    timers[i].initMs,
                    timers[i].currentMs,
                    false,
                    timers[i].isFinished
                )
            if (timers[i].id == id) {
                timers[i] =
                    Timer(timers[i].id, timers[i].initMs, currentMs, isStarted, isFinished)

            }
        }

        timerAdapter.submitList(timers.toList())
    }

    private fun getCountTimer(id: Int, currentMs: Long) {
        jobTimer = lifecycleScope.launch(Dispatchers.Main) {
            val startTimeTimer = System.currentTimeMillis()
            while (true) {
                for (i in timers.indices) {
                    if (timers[i].id == id) {
                        timers[i].currentMs =
                            currentMs - (System.currentTimeMillis() - startTimeTimer)
                    }
                }

                delay(10L)
            }
        }
    }
}