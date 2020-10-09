package io.github.phantasmdragon.buttonx.presentation.view

import android.content.Context
import android.os.CountDownTimer
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateUtils
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.res.getIntOrThrow
import androidx.core.content.res.use
import io.github.phantasmdragon.buttonx.R
import io.github.phantasmdragon.buttonx.presentation.data.Constant
import io.github.phantasmdragon.buttonx.utils.extension.inflate
import kotlinx.android.synthetic.main.view_time_block_container.view.*
import java.util.concurrent.TimeUnit

class TimeCountView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var startTime: Long = System.currentTimeMillis()
        set(value) {
            if (!isRunning) {
                field = value
            }
        }
    var timeUnit: TimeUnit = TimeUnit.MILLISECONDS

    private lateinit var countType: CountType

    private var currentTime: Long = startTime
    private var isRunning: Boolean = false

    private val stopwatchRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                currentTime += DateUtils.SECOND_IN_MILLIS
                updateStopwatchTimeChunks()
                updateText()

                postDelayed(this, DateUtils.SECOND_IN_MILLIS)
            }
        }
    }
    private val countDownTimer by lazy(LazyThreadSafetyMode.NONE) {
        object : CountDownTimer(currentTime, DateUtils.SECOND_IN_MILLIS) {
            override fun onTick(millis: Long) {
                currentTime = millis
                updateCountDownTimeChunks()
                updateText()
            }

            override fun onFinish() {
                stop()
                countDownListener?.onFinishCountDown()
            }
        }
    }


    /**
     * represents time as [ss, mm, hh, dd, yy, ...]
     * where ss - seconds, mm - minutes, etc.
     *
     * thus, we can update values separately and identify which value has changed,
     * so don't update other views
     */
    private lateinit var humanReadableTimeChunks: LongArray

    private var countDownListener: CountDownListener? = null

    init {
        inflate(R.layout.view_time_block_container)
        init(attrs)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val viewState = CountDownViewState(superState)

        return viewState.also {
            it.startTime = startTime
            it.currentTime = currentTime
            it.timerRunning = isRunning
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val viewState: CountDownViewState = state as CountDownViewState

        super.onRestoreInstanceState(viewState.superState)

        startTime = viewState.startTime
        currentTime = viewState.currentTime

        if (viewState.timerRunning) {
            start()
        }
    }

    fun start() {
        if (isRunning) return

        isRunning = true

        when (countType) {
            CountType.STOPWATCH -> startStopwatch()
            CountType.COUNTDOWN -> startCountDown()
        }
    }

    fun reset() {
        stop()
        currentTime = startTime
    }

    fun stop() {
        if (!isRunning) return

        isRunning = false

        when (countType) {
            CountType.STOPWATCH -> stopStopwatch()
            CountType.COUNTDOWN -> stopCountDown()
        }
    }

    fun setListener(countDownListener: CountDownListener?) {
        this.countDownListener = countDownListener
    }

    private fun init(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.TimeCountView).use { typedArray ->
            val startTime = typedArray.getInt(R.styleable.TimeCountView_start_time, Constant.NO_ID)
                .takeUnless { it == Constant.NO_ID }
                ?.toLong()

            countType = typedArray.getIntOrThrow(R.styleable.TimeCountView_count_type).let { id ->
                CountType.findById(id)
            }

            typedArray.getInt(R.styleable.TimeCountView_time_unit, Constant.NO_ID)
                .takeUnless { it == Constant.NO_ID }
                ?.let { value ->
                    timeUnit = when (value) {
                        0 -> TimeUnit.MILLISECONDS
                        1 -> TimeUnit.SECONDS
                        2 -> TimeUnit.MINUTES
                        3 -> TimeUnit.HOURS
                        else -> TimeUnit.DAYS
                    }
                }
            typedArray.getBoolean(R.styleable.TimeCountView_start_immediately, false)
                .let { startImmediately ->
                    if (startImmediately && !isRunning) {
                        if (countType == CountType.COUNTDOWN) {
                            this.startTime = startTime ?: throw IllegalStateException(
                                "start_time is required to start time immediately"
                            )
                            currentTime = timeUnit.toMillis(startTime)
                        }

                        start()
                    }
                }
        }
    }

    private fun startStopwatch() {
        postDelayed(stopwatchRunnable, DateUtils.SECOND_IN_MILLIS)
    }

    private fun stopStopwatch() {
        removeCallbacks(stopwatchRunnable)
    }

    private fun startCountDown() {
        countDownTimer.start()
    }

    private fun stopCountDown() {
        countDownTimer.cancel()
    }

    private fun updateStopwatchTimeChunks() {
        var millisPassed = currentTime - startTime

        val days = TimeUnit.MILLISECONDS.toDays(millisPassed)
        millisPassed -= TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(millisPassed)
        millisPassed -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisPassed)
        millisPassed -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisPassed)

        // the order matters
        humanReadableTimeChunks = longArrayOf(
            seconds, minutes, hours, days
        )
    }

    private fun updateCountDownTimeChunks() {
        var currentTimeInMillis = currentTime

        val days = TimeUnit.MILLISECONDS.toDays(currentTimeInMillis)
        currentTimeInMillis -= TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(currentTimeInMillis)
        currentTimeInMillis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(currentTimeInMillis)
        currentTimeInMillis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(currentTimeInMillis)

        // the order matters
        humanReadableTimeChunks = longArrayOf(
            seconds, minutes, hours, days
        )
    }

    private fun updateText() {
        view_time_block_seconds.text = humanReadableTimeChunks[0].withLeadingZero()
        view_time_block_minutes.text = humanReadableTimeChunks[1].withLeadingZero()
        view_time_block_hours.text = humanReadableTimeChunks[2].withLeadingZero()
        view_time_block_days.text = humanReadableTimeChunks[3].withLeadingZero()
    }

    private fun Long.withLeadingZero(): String = if (this < 10) "0$this" else toString()

    internal class CountDownViewState : BaseSavedState {
        var startTime: Long = 0
        var currentTime: Long = 0
        var timerRunning = false

        constructor(superState: Parcelable?) : super(superState)
        constructor(source: Parcel) : super(source) {
            startTime = source.readLong()
            currentTime = source.readLong()
            timerRunning = source.readInt() == 1
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeLong(startTime)
            out.writeLong(currentTime)
            out.writeInt(if (timerRunning) 1 else 0)
        }

        companion object CREATOR : Parcelable.Creator<CountDownViewState> {
            override fun createFromParcel(parcel: Parcel): CountDownViewState {
                return CountDownViewState(parcel)
            }

            override fun newArray(size: Int): Array<CountDownViewState?> {
                return arrayOfNulls(size)
            }
        }

    }

    interface CountDownListener {
        fun onFinishCountDown()
    }

    enum class CountType(val id: Int) {
        STOPWATCH(0),
        COUNTDOWN(1);

        companion object {
            fun findById(id: Int) = values().find { it.id == id } ?:
                throw NoSuchElementException("CountType has not been found by id: $id")
        }
    }

}
