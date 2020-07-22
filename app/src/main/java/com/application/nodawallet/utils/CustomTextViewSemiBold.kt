package com.application.nodawallet.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

class CustomTextViewSemiBold : androidx.appcompat.widget.AppCompatTextView{

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
        val font = Typeface.createFromAsset(context.assets, "fontName/sfprosemibold.ttf")
     //   val font = Typeface.createFromAsset(context.assets, "fontName/dinprosemibold.otf")
      //  val font = Typeface.createFromAsset(context.assets, "fontName/robotosemibold.ttf")
        //val font = Typeface.createFromAsset(context.assets, "fontName/poppinsemibold.ttf")
        setTypeface(font, Typeface.BOLD)
    }
}