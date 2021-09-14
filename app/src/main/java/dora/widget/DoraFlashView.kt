package dora.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlin.math.abs
import kotlin.math.tan

class DoraFlashView : FrameLayout {

    private var transX = 0f
    private var deltaX = 0f
    private lateinit var gradient: LinearGradient
    private lateinit var gradientMatrix: Matrix
    private val defaultColor = 0x00ffffff
    private var highlightColor = 0x00ffffff
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var gradientSize = 0f
    private var outSize = 0f
    private var type = TYPE_NORMAL
    // 取值区间（-90°, 90°）
    private var rotateAngle: Float = 0f

    private fun init() {
        setBackgroundColor(Color.BLACK)
        if (type == TYPE_BLUR) {
            gradient = LinearGradient(
                    -gradientSize,
                    0f,
                    0f,
                    0f,
                    intArrayOf(defaultColor, highlightColor, defaultColor),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
            )
        } else {
            gradient = LinearGradient(
                    -gradientSize,
                    0f,
                    0f,
                    0f,
                    intArrayOf(defaultColor, highlightColor, highlightColor, defaultColor),
                    floatArrayOf(0f, 0.1f, 0.9f, 1f),
                    Shader.TileMode.CLAMP
            )
        }
        gradientMatrix = Matrix()
        paint.shader = gradient
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (abs(rotateAngle) >= 90f) {
            throw IllegalArgumentException("angle not supported")
        }
        outSize = height * tan(Math.PI / 180f * abs(rotateAngle.toDouble())).toFloat()
        canvas.drawRect(Rect(0, 0, canvas.width, canvas.height), paint)
        transX += deltaX
        if (transX > width+gradientSize+outSize) {
            transX = 0f
        }
        gradientMatrix.setRotate(rotateAngle)
        gradientMatrix.postTranslate(transX, 0f)
        gradient.setLocalMatrix(gradientMatrix)
        postInvalidateDelayed(40)
    }

    companion object {
        // 清晰
        const val TYPE_NORMAL = 0
        // 模糊
        const val TYPE_BLUR = 1
    }

    init {
        init()
    }

    @JvmOverloads constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyle: Int = 0
    ) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DoraFlashView, defStyle, 0)
        type = a.getInt(R.styleable.DoraFlashView_dora_type, TYPE_NORMAL)
        rotateAngle = a.getFloat(R.styleable.DoraFlashView_dora_rotateAngle, 0f)
        deltaX = a.getDimension(R.styleable.DoraFlashView_dora_deltaX, 20f)
        gradientSize = a.getDimension(R.styleable.DoraFlashView_dora_gradientSize, 40f)
        highlightColor = a.getColor(R.styleable.DoraFlashView_dora_highlightColor, 0x3fffffff)
        a.recycle()
    }
}