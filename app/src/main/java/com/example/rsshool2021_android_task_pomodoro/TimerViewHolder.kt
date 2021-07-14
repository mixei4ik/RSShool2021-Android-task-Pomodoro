package com.example.rsshool2021_android_task_pomodoro

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rsshool2021_android_task_pomodoro.databinding.TimerItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewHolder(
    private val binding: TimerItemBinding,
    private val listener: TimerListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null
    private var current = 0L

    fun bind(timer: Timer) {
        binding.timer.text = timer.currentMs.displayTime()

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
                listener.start(timer.id)
            }
        }

        binding.deleteButton.setOnClickListener { listener.delete(timer.id) }
    }

    private fun startTimer(timer: Timer) {
        binding.startPauseButton.text = "stop"

        this.timer?.cancel()
        this.timer = getCountDownTimer(timer)
        this.timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()



/*        binding.customView.setPeriod(PERIOD_CUSTOM)

        GlobalScope.launch {
            while (current < PERIOD_CUSTOM * REPEAT) {
                current += INTERVAL
                binding.customView.setCurrent(current)
                delay(INTERVAL)
            }
        }*/
    }

    private fun stopTimer(timer: Timer) {
        binding.startPauseButton.text = "start"

        this.timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(timer: Timer): CountDownTimer {
        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                timer.currentMs += interval
                binding.timer.text = timer.currentMs.displayTime()
            }

            override fun onFinish() {
                binding.timer.text = timer.currentMs.displayTime()
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