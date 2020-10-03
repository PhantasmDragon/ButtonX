package io.github.phantasmdragon.buttonx.presentation.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.os.Parcel
import android.os.Parcelable
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import io.github.phantasmdragon.buttonx.R
import java.util.*

class TimeCountView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet
) : View(context, attrs) {

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private var textAppearanceSpan: TextAppearanceSpan? = null
    private var textLayout: Layout? = null
    private val spannableString = SpannableStringBuilder()
    private var timer: CountDownTimer? = null
    private var startDuration: Long = 0
    private var currentDuration: Long = 0
    private var timerRunning = false

    private var listener: CountDownListener? = null

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        textPaint.color = Color.BLACK
        val textSize: Int
        val startDuration: Int
        val textAppearanceRef: Int
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TimeCountView)
        startDuration = ta.getInt(R.styleable.TimeCountView_startDuration, 0)
        textSize = ta.getDimensionPixelSize(
            R.styleable.TimeCountView_android_textSize,
            dpToPx(12, resources).toInt()
        )
        textAppearanceRef = ta.getResourceId(R.styleable.TimeCountView_android_textAppearance, 0)
        ta.recycle()
        textPaint.textSize = textSize.toFloat()
        if (textAppearanceRef != 0) {
            textAppearanceSpan = TextAppearanceSpan(context, textAppearanceRef)
            textPaint.textSize = textAppearanceSpan!!.textSize.toFloat()
        }
        setStartDuration(startDuration.toLong())
    }

    fun setStartDuration(duration: Long) {
        if (timerRunning) {
            return
        }
        currentDuration = duration
        startDuration = currentDuration
        updateText(duration)
    }

    fun start() {
        if (timerRunning) {
            return
        }
        timerRunning = true
        timer = object : CountDownTimer(currentDuration, 100) {
            override fun onTick(millis: Long) {
                currentDuration = millis
                updateText(millis)
                invalidate()
            }

            override fun onFinish() {
                stop()
                listener?.onFinishCountDown()
            }
        }
        timer?.start()
    }

    fun reset() {
        stop()
        setStartDuration(startDuration)
        invalidate()
    }

    fun stop() {
        if (!timerRunning) {
            return
        }
        timerRunning = false
        timer!!.cancel()
    }

    fun setListener(listener: CountDownListener?) {
        this.listener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (textLayout == null) {
            updateText(currentDuration)
        }
        setMeasuredDimension(textLayout!!.width, textLayout!!.height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textLayout!!.draw(canvas)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val viewState = CountDownViewState(superState)
        viewState.startDuration = startDuration
        viewState.currentDuration = currentDuration
        viewState.timerRunning = timerRunning
        return viewState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val viewState: CountDownViewState = state as CountDownViewState
        super.onRestoreInstanceState(viewState.getSuperState())
        setStartDuration(viewState.startDuration)
        currentDuration = viewState.currentDuration
        if (viewState.timerRunning) {
            start()
        }
    }

    fun updateText(duration: Long) {
        val text = generateCountdownText(duration)
        textLayout = createTextLayout(text)
    }

    fun createTextLayout(text: String): Layout {
        val textWidth = textPaint.measureText(text).toInt()
        val unitTextSize = (textPaint.textSize / 2).toInt()
        spannableString.clear()
        spannableString.clearSpans()
        spannableString.append(text)
        if (textAppearanceSpan != null) {
            spannableString.setSpan(
                textAppearanceSpan,
                0,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        val hrIndex = text.indexOf("h")
        val minIndex = text.indexOf("m")
        val secIndex = text.indexOf("s")
        spannableString.setSpan(
            AbsoluteSizeSpan(unitTextSize),
            hrIndex,
            hrIndex + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(unitTextSize),
            minIndex,
            minIndex + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(unitTextSize),
            secIndex,
            secIndex + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return StaticLayout(
            spannableString,
            textPaint,
            textWidth,
            Layout.Alignment.ALIGN_CENTER,
            0f,
            0f,
            true
        )
    }

    companion object {
        private const val HOUR = 3600000
        private const val MIN = 60000
        private const val SEC = 1000
        fun generateCountdownText(duration: Long): String {
            val hr = (duration / HOUR).toInt()
            val min =
                ((duration - hr * HOUR) / MIN).toInt()
            val sec =
                ((duration - hr * HOUR - min * MIN) / SEC).toInt()
            val locale = Locale.getDefault()
            val format = "%02d"
            val formattedHr = String.format(locale, format, hr)
            val formattedMin = String.format(locale, format, min)
            val formattedSec = String.format(locale, format, sec)
            return String.format(
                locale,
                "%sh %sm %ss",
                formattedHr,
                formattedMin,
                formattedSec
            )
        }

        private fun dpToPx(dp: Int, resources: Resources): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                resources.displayMetrics
            )
        }
    }

    internal class CountDownViewState : BaseSavedState {
        var startDuration: Long = 0
        var currentDuration: Long = 0
        var timerRunning = false

        constructor(superState: Parcelable?) : super(superState) {}
        constructor(source: Parcel) : super(source) {
            startDuration = source.readLong()
            currentDuration = source.readLong()
            timerRunning = source.readInt() == 1
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeLong(startDuration)
            out.writeLong(currentDuration)
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

}