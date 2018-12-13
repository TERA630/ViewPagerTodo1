package com.example.yoshi.viewpagertodo1

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter

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
