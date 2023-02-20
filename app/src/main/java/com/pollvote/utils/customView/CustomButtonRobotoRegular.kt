package com.pollvote.utils.customView

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton

class CustomButtonRobotoRegular(context: Context?, attrs: AttributeSet?) : AppCompatButton(context!!, attrs) {
    init {
        val typeface = Typeface.createFromAsset(getContext().assets, "font/roboto_regular.ttf")
        setTypeface(typeface)

    }
}