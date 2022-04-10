package com.jiayou.shanghai

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class GetGroceriesService : AccessibilityService() {

    private val dingDongHelper = DingDongHelper {
        performGlobalAction(it)
    }
    private val hippoHelper: HippoHelper = HippoHelper {
        performGlobalAction(it)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d(TAG, "event: $event")
        event?.let {
            when (it.packageName) {
                DingDongHelper.PACKAGE_NAME -> {
                    dingDongHelper.handleEvent(event)
                }
                HippoHelper.PACKAGE_NAME -> {
                    hippoHelper.handleEvent(event)
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.e(TAG, "onInterrupt")
    }

    companion object {
        const val TAG = "GetGroceriesService"
    }
}