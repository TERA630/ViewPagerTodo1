package com.example.yoshi.viewpagertodo1

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class KeyboardUtils {
    fun hide(_context: Context, view: View) {
        val imm = (_context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
    fun hide(_activity: Activity) {
        val focus = _activity.currentFocus
        focus?.let {
            this.hide(_activity, focus)
        }
    }
    fun initHidden(_activity: Activity) {
        _activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun show(_context: Context, text: EditText) {
        show(_context, text)
    }
    fun show(_context: Context?, edit: EditText, delayTime: Int) {
        val showKeyboardDelay = Runnable {
            _context?.let {
                val imm = _context
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        Handler().postDelayed(showKeyboardDelay, delayTime.toLong())
    }
}
