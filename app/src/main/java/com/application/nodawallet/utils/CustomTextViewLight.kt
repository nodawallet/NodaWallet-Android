package com.application.nodawallet.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

class CustomTextViewLight : androidx.appcompat.widget.AppCompatTextView{

    constructor(context: Context) : super(context){
        setFont()
    }
    constructor(context: Context, attrs : AttributeSet) : super(context,attrs){
        setFont()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr){
        setFont()
    }

    private fun setFont() {
        val font = Typeface.createFromAsset(context.assets, "fontName/sfprolight.ttf")
        //val font = Typeface.createFromAsset(context.assets, "fontName/dinprolight.ttf")
       // val font = Typeface.createFromAsset(context.assets, "fontName/robotolight.ttf")
       // val font = Typeface.createFromAsset(context.assets, "fontName/poppinslight.ttf")
        setTypeface(font, Typeface.BOLD)
    }
}