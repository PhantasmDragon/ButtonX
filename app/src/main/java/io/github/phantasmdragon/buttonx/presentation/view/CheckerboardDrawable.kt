package io.github.phantasmdragon.buttonx.presentation.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import io.github.phantasmdragon.buttonx.R
import io.github.phantasmdragon.buttonx.utils.extension.getColorCompat

class CheckerboardDrawable(context: Context) : Drawable() {

    private val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val size = 40
    private val colorOdd = context.getColorCompat(R.color.pink)
    private val colorEven = context.getColorCompat(R.color.black)

    init {
        configurePaint()
    }

    private fun configurePaint() {
        val bitmap = Bitmap.createBitmap(
            size * 2,
            size * 2,
            Bitmap.Config.ARGB_8888
        )
        val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = colorOdd
        }
        val canvas = Canvas(bitmap)
        val rect = Rect(0, 0, size, size)

        canvas.drawRect(rect, bitmapPaint)
        rect.offset(size, size)
        canvas.drawRect(rect, bitmapPaint)
        bitmapPaint.color = colorEven
        rect.offset(-size, 0)
        canvas.drawRect(rect, bitmapPaint)
        rect.offset(size, -size)
        canvas.drawRect(rect, bitmapPaint)

        shaderPaint.shader = BitmapShader(
            bitmap,
            Shader.TileMode.REPEAT,
            Shader.TileMode.REPEAT
        )
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPaint(shaderPaint)
    }

    override fun setAlpha(alpha: Int) {
        shaderPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        shaderPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

}
