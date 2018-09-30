package com.example.yoshi.viewpagertodo1

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("android:checked", "android:text", requireAll = true)
fun CompoundButton.setSpannableText(doneChecked: Boolean, _text: String) {
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

fun OnEditorDone(txtView: TextView, actionId: Int, event: KeyEvent): Boolean {
    if (actionId != EditorInfo.IME_ACTION_DONE) return false
    Log.i("test", "Done Event Called")
    return true
}