package com.example.rsshool2021_android_task_pomodoro

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rsshool2021_android_task_pomodoro.databinding.TimerItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewHolder(
    private val binding: TimerItemBinding,
    private val listener: TimerListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {

    private var jobView: Job? = null

    fun bind(timer: Timer) {
        binding.timer.text = timer.currentMs.displayTime()

        if (timer.isFinished) {
            binding.timerView.setBackgroundColor(resources.getColor(R.color.red_700))
        } else {
            binding.timerView.setBackgroundColor(Color.WHITE)
            binding.customView.setCurrent(timer.initMs - timer.currentMs)
        }

        if (timer.isStarted) {
            startTimer(timer)
        } else {
            stopTimer(timer)
        }

        initButtonsListeners(timer)
    }

    private fun initButtonsListeners(timer: Timer) {
        binding.startPauseButton.setOnClickListener {
            if (timer.isStarted) {
                listener.stop(timer.id, timer.currentMs, timer.isFinished)
            } else {
                listener.start(timer.id, timer.currentMs)
            }
        }

        binding.deleteButton.setOnClickListener { listener.delete(timer.id) }
    }

    private fun startTimer(timer: Timer) {
        binding.startPauseButton.text = "stop"

        jobView?.cancel()
        paintView(timer)

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()

    }

    private fun stopTimer(timer: Timer) {
        binding.startPauseButton.text = "start"

        jobView?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun paintView(timer: Timer) {
        binding.customView.setPeriod(timer.initMs)

        jobView = GlobalScope.launch {
            while (true) {
                if (timer.currentMs <= 0) {
                    onFinish(timer)
                    jobView?.cancel()
                }

                binding.customView.setCurrent(timer.initMs - timer.currentMs)
                binding.timer.text = timer.currentMs.displayTime()
                delay(1000L)
            }
        }
    }

    private fun onFinish(timer: Timer) {
        timer.isFinished = true
        listener.stop(timer.id, timer.initMs, timer.isFinished)
        binding.timer.text = timer.currentMs.displayTime()
    }
}