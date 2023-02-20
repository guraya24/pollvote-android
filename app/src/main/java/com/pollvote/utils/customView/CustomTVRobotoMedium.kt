package com.pollvote.utils.customView

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CustomTVRobotoMedium(context: Context?, attrs: AttributeSet?) :
    AppCompatTextView(context!!, attrs) {
    init {
        val typeface = Typeface.createFromAsset(getContext().assets, "font/roboto_medium.ttf")
        setTypeface(typeface)

    }
}