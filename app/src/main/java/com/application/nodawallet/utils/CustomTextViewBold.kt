package com.application.nodawallet.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

class CustomTextViewBold : androidx.appcompat.widget.AppCompatTextView{

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
        val font = Typeface.createFromAsset(context.assets, "fontName/sfprobold.ttf")
     //   val font = Typeface.createFromAsset(context.assets, "fontName/dinprobold.ttf")
      //  val font = Typeface.createFromAsset(context.assets, "fontName/robotobold.ttf")
       // val font = Typeface.createFromAsset(context.assets, "fontName/poppinsbold.ttf")

        setTypeface(font, Typeface.BOLD)
    }
}