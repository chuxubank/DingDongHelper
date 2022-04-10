package com.jiayou.shanghai

import android.view.accessibility.AccessibilityEvent

abstract class AbstractHelper {
    abstract fun handleEvent(event: AccessibilityEvent)
}