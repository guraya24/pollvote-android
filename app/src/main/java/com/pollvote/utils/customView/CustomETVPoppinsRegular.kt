package com.pollvote.utils.customView

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomETVPoppinsRegular(context: Context?, attrs: AttributeSet?) : AppCompatEditText(context!!, attrs) {
    init {
        val typeface = Typeface.createFromAsset(getContext().assets, "font/poppins_regular.ttf")
            setTypeface(typeface)

    }
}