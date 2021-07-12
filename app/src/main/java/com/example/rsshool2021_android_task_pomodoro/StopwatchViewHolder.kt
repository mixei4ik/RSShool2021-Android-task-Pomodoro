package com.example.rsshool2021_android_task_pomodoro

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null
    private var current = 0L

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()

        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        } else {
            stopTimer(stopwatch)
        }

        initButtonsListeners(stopwatch)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id)
            }
        }

        binding.restartButton.setOnClickListener { listener.reset(stopwatch.id) }

        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        val drawable = resources.getDrawable(R.drawable.ic_baseline_pause_24)
        binding.startPauseButton.setImageDrawable(drawable)

        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()



        binding.customView.setPeriod(PERIOD_CUSTOM)

        GlobalScope.launch {
            while (current < PERIOD_CUSTOM * REPEAT) {
                current += INTERVAL
                binding.customView.setCurrent(current)
                delay(INTERVAL)
            }
        }
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        val drawable = resources.getDrawable(R.drawable.ic_baseline_play_arrow_24)
        binding.startPauseButton.setImageDrawable(drawable)

        timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs += interval
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }
        }
    }

    private fun Long.displayTime(): String {
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60
        val ms = this % 1000 / 10

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}:${displaySlot(ms)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {

        private const val START_TIME = "00:00:00:00"
        private const val UNIT_TEN_MS = 10L
        private const val PERIOD = 1000L * 60L * 60L * 24L

        private const val INTERVAL = 100L
        private const val PERIOD_CUSTOM = 1000L * 30 // 30 sec
        private const val REPEAT = 10 // 10 times
    }
}