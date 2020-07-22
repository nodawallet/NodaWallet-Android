package com.application.nodawallet.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

class CustomTextViewMedium  : androidx.appcompat.widget.AppCompatTextView{

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
        val font = Typeface.createFromAsset(context.assets, "fontName/sfpromedium.ttf")
      //  val font = Typeface.createFromAsset(context.assets, "fontName/dinpromedium.ttf")
        //val font = Typeface.createFromAsset(context.assets, "fontName/robotomedium.ttf")
      //  val font = Typeface.createFromAsset(context.assets, "fontName/poppinsmedium.ttf")
        setTypeface(font, Typeface.BOLD)
    }
}