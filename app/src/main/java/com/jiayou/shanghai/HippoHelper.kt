package com.jiayou.shanghai

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CLICK

class HippoHelper(
    val performGlobalAction: (Int) -> Unit
) : AbstractHelper() {
    var currentClassName: String = ""

    override fun handleEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        currentClassName = event.className.toString()
        Log.d(TAG, "currentClassName: ${event.className}")
        when (currentClassName) {
            CART_ACTIVITY -> settle(event)
        }
    }

    private fun settle(event: AccessibilityEvent) {
        action(ACTION_CLICK, "结算", event)
    }

    private fun action(action: Int, nodeString: String, event: AccessibilityEvent) {
        event.source?.findAccessibilityNodeInfosByText(nodeString)
            ?.forEachIndexed { index, node ->
                node.performAction(action)
                Log.d(TAG, "Do $action on $nodeString ($index) [$node]")
            }
    }

    companion object {
        const val TAG = "HippoHelper"
        const val PACKAGE_NAME = "com.wudaokou.hippo"
        const val CART_ACTIVITY = "com.wudaokou.hippo.cart.container.CartMainActivity"
    }
}