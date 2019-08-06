package com.allever.security.photo.browser.function.password

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.allever.security.photo.browser.R
import com.allever.lib.common.app.App


class KeyboardView : LinearLayout, View.OnClickListener {

    private val VIBRATOR_DURATION = 100L

    private val defaultTextSize = 72f

    private var key1: TextView? = null
    private var key2: TextView? = null
    private var key3: TextView? = null
    private var key4: TextView? = null
    private var key5: TextView? = null
    private var key6: TextView? = null
    private var key7: TextView? = null
    private var key8: TextView? = null
    private var key9: TextView? = null
    private var key0: TextView? = null
    private var keySpace: TextView? = null
    private var keyDel: ImageView? = null

    var onKeyboardClick: OnKeyboardClick? = null

    private var vibrator: Vibrator? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
    }


    override fun onFinishInflate() {
        super.onFinishInflate()

        key1 = findViewById(R.id.keyboard_key_1)
        key2 = findViewById(R.id.keyboard_key_2)
        key3 = findViewById(R.id.keyboard_key_3)
        key4 = findViewById(R.id.keyboard_key_4)
        key5 = findViewById(R.id.keyboard_key_5)
        key6 = findViewById(R.id.keyboard_key_6)
        key7 = findViewById(R.id.keyboard_key_7)
        key8 = findViewById(R.id.keyboard_key_8)
        key9 = findViewById(R.id.keyboard_key_9)
        key0 = findViewById(R.id.keyboard_key_0)
        keySpace = findViewById(R.id.keyboard_key_space)
        keyDel = findViewById(R.id.keyboard_key_del)

        key1?.setOnClickListener(this)
        key2?.setOnClickListener(this)
        key3?.setOnClickListener(this)
        key4?.setOnClickListener(this)
        key5?.setOnClickListener(this)
        key6?.setOnClickListener(this)
        key7?.setOnClickListener(this)
        key8?.setOnClickListener(this)
        key9?.setOnClickListener(this)
        key0?.setOnClickListener(this)
        keyDel?.setOnClickListener(this)

        val childCount = this.childCount
        for (i in 0 until childCount) {
            val childView = this.getChildAt(i)
            if (childView is TextView) {
                childView.textSize = defaultTextSize
            }
        }

        vibrator = App.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.keyboard_key_1,
            R.id.keyboard_key_2,
            R.id.keyboard_key_3,
            R.id.keyboard_key_4,
            R.id.keyboard_key_5,
            R.id.keyboard_key_6,
            R.id.keyboard_key_7,
            R.id.keyboard_key_8,
            R.id.keyboard_key_9,
            R.id.keyboard_key_0 -> {
                val text = (v as? TextView)?.text
                if (text != null) {
                    val num = text.toString().toIntOrNull()
                    if (num != null) {
                        onKeyboardClick?.onClick(num)
                    }
                }
            }
            R.id.keyboard_key_del -> {
                onKeyboardClick?.onDelete()
            }
        }

        vibrator?.vibrate(VIBRATOR_DURATION)

    }

    interface OnKeyboardClick {
        fun onClick(num: Int)
        fun onDelete()
    }
}
