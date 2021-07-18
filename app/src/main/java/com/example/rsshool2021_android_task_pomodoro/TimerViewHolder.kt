package com.example.rsshool2021_android_task_pomodoro

import android.annotation.SuppressLint
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
        binding.timerView.setBackgroundColor(resources.getColor(R.color.white))

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
        binding.timerView.setBackgroundColor(resources.getColor(R.color.white))

        this.timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(timer: Timer): CountDownTimer {
        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                timer.currentMs -= interval
                if (timer.currentMs <= 0) onFinish()
                binding.timer.text = timer.currentMs.displayTime()
            }

            override fun onFinish() {
                listener.stop(timer.id, timer.initMs)
                binding.timerView.setBackgroundColor(resources.getColor(R.color.red_700))
                binding.timer.text = timer.currentMs.displayTime()
            }
        }
    }

    private fun Long.displayTime(): String {
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {

        private const val STOP_TIME = "00:00:00"
        private const val UNIT_TEN_MS = 100L
        private const val PERIOD = 1000L * 60L * 60L * 24L

        private const val INTERVAL = 100L
        private const val PERIOD_CUSTOM = 1000L * 30 // 30 sec
        private const val REPEAT = 10 // 10 times
    }
}