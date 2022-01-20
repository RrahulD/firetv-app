package tv.accedo.dishonstream2.extensions

import android.view.KeyEvent
import android.view.View

private val leftKeyEventConsumer = View.OnKeyListener { _, keyEvent, _ -> keyEvent == KeyEvent.KEYCODE_DPAD_LEFT }
private val rightKeyEventConsumer = View.OnKeyListener { _, keyEvent, _ -> keyEvent == KeyEvent.KEYCODE_DPAD_RIGHT }

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.addToFocusHierarchy() {
    this.isFocusable = true
    this.isFocusableInTouchMode = true
}

fun View.removeFromFocusHierarchy() {
    this.isFocusable = false
    this.isFocusableInTouchMode = false
}

fun View.consumeLeftKeyEvent() {
    this.setOnKeyListener(leftKeyEventConsumer)
}

fun View.consumeRightKeyEvent() {
    this.setOnKeyListener(rightKeyEventConsumer)
}


