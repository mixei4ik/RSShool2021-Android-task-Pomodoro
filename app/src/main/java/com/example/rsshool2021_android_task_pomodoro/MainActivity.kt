package com.example.rsshool2021_android_task_pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), TimerListener {

    private lateinit var binding: ActivityMainBinding

    private val timerAdapter = TimersAdapter(this)
    private val timers = mutableListOf<Timer>()
    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerAdapter
        }
        2
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