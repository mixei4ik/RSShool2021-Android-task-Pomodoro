package com.example.rsshool2021_android_task_pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.addNewStopwatchButton.setOnClickListener {
            stopwatches.add(Stopwatch(nextId++, 0, false))
            stopwatchAdapter.submitList(stopwatches.toList())
        }
    }

    override fun start(id: Int) {
        changeStopwatch(id, null, true)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id, currentMs, false)
    }

    override fun reset(id: Int) {
        changeStopwatch(id, 0L, false)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id})
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean) {
        for (i in stopwatches.indices) {
            if (stopwatches[i].id == id) {
                stopwatches[i] =
                    Stopwatch(stopwatches[i].id, currentMs ?: stopwatches[i].currentMs, isStarted)

            }
        }

        stopwatchAdapter.submitList(stopwatches.toList())
    }
}