package com.example.rsshool2021_android_task_pomodoro

import android.content.res.Resources
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
        binding.timerView.setBackgroundColor(resources.getColor(R.color.white))
        binding.customView.setCurrent(timer.initMs - timer.currentMs)

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
                listener.stop(timer.id, timer.currentMs)
            } else {
                listener.start(timer.id, timer.currentMs)
            }
        }

        binding.deleteButton.setOnClickListener { listener.delete(timer.id) }
    }

    private fun startTimer(timer: Timer) {
        binding.startPauseButton.text = "stop"
        binding.timerView.setBackgroundColor(resources.getColor(R.color.white))

        jobView?.cancel()
        paintView(timer)

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()

    }

    private fun stopTimer(timer: Timer) {
        binding.startPauseButton.text = "start"
        binding.timerView.setBackgroundColor(resources.getColor(R.color.white))

        jobView?.cancel()
        binding.customView.setCurrent(timer.initMs - timer.currentMs)

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
                delay(100L)
            }
        }
    }

    private fun onFinish(timer: Timer) {
        listener.stop(timer.id, timer.initMs)
        binding.timerView.setBackgroundColor(resources.getColor(R.color.red_700))
        binding.timer.text = timer.currentMs.displayTime()
    }
}