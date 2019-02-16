package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.BindingAdapter
import kotlinx.android.synthetic.main.recyclerview_menu.view.*

@BindingAdapter("android:checked", "android:text", requireAll = true)
fun CheckBox.setSpannableText(doneChecked: Boolean, _text: String) {
    if (doneChecked) {
        val spannableSir = SpannableString(_text)
        spannableSir.setSpan(StrikethroughSpan(), 0, spannableSir.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        this.text = spannableSir
        this.isChecked = true
    } else {
        this.text = _text
        this.isChecked = false
    }
}

fun onEditorDone(txtView: TextView, actionId: Int, event: KeyEvent): Boolean {
    if (actionId != EditorInfo.IME_ACTION_DONE) return false
    Log.i("test", "onEditor done was called by  $event of $txtView ")
    return true
}

fun onEditorActionDone(edit: TextView, actionId: Int, event: KeyEvent?): Boolean {
    Log.i("test", "onEditorActionDone was Called by $event")
    return when (actionId) {
        EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_NULL -> {
            val keyboardUtils = KeyboardUtils()
            keyboardUtils.hide(edit.context, edit)
            true
        }
        else -> {
            false
        }
    }
}

class SubContextWindow(private val _view: View) {
    var numberToCall:Int = 0
    var menuPositon:Int =0
    fun create(_context: Context): PopupWindow {

        val window = PopupWindow(_context)
        val content = LayoutInflater.from(_context).inflate(R.layout.recyclerview_menu, null)
        window.contentView = content

        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, _context.resources.displayMetrics)
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, _context.resources.displayMetrics)
        window.width = width.toInt()
        window.height = height.toInt()
        window.isOutsideTouchable = true
        window.isFocusable = true
        window.isTouchable = true

        window.showAsDropDown(_view,0,0,Gravity.NO_GRAVITY)
        val menu1 = content.findViewById(R.id.view_contextMenu1)
        (menu1 as View).setOnClickListener{


        }
        val menu2 = content.findViewById(R.Id.view_contextMenu2)
        (menu2 as View).setOnClickListener{}
        return window
    }
}