package com.example.rsshool2021_android_task_pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), TimerListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    private val timerAdapter = TimersAdapter(this)
    private val timers = mutableListOf<Timer>()
    private var nextId = 0
    private var startTime = 0L

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
            var currentMin = 0L
            currentMin = if (binding.timeEdit.text.toString() == "") {
                0
            } else {
                binding.timeEdit.text.toString().toLong()
            }
            if (currentMin > 0) {
                timers.add(Timer(nextId++, currentMin * 60 * 1000, currentMin * 60 * 1000, false))
                timerAdapter.submitList(timers.toList())
            }
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
        changeTimer(id, currentMs, true)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeTimer(id, currentMs, false)
    }

    override fun reset(id: Int) {
        changeTimer(id, 0L, false)
    }

    override fun delete(id: Int) {
        timers.remove(timers.find { it.id == id })
        timerAdapter.submitList(timers.toList())
    }

    private fun changeTimer(id: Int, currentMs: Long, isStarted: Boolean) {
        for (i in timers.indices) {
            timers[i] = Timer(timers[i].id, timers[i].initMs, timers[i].currentMs, false)
            if (timers[i].id == id) {
                timers[i] =
                    Timer(timers[i].id, timers[i].initMs, currentMs, isStarted)

            }
        }

        timerAdapter.submitList(timers.toList())
    }
}