package com.jiayou.shanghai

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DingDongHelper(
    val performGlobalAction: (Int) -> Unit
) : AbstractHelper() {
    var currentClassName: String = ""
    var chooseTimeSuccess: Boolean = false
    var enableJumpCart: Boolean = true
    var checkNotificationCount: Int = 0

    override fun handleEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        currentClassName = event.className as String
        Log.d(TAG, "currentClassName: $currentClassName")
        if (currentClassName in listOf(
                CART_ACTIVITY,
                CHOOSE_DELIVERY_TIME,
                CHOOSE_DELIVERY_TIME_V2
            )
        ) {
            enableJumpCart = true
        }
        if (currentClassName == HOME_ACTIVITY) {
            checkNotificationCount = 0
        }
        when (currentClassName) {
            CART_ACTIVITY -> {
                pay(event)
            }
            HOME_ACTIVITY -> {
                jumpToCartActivity(event)
            }
            CHOOSE_DELIVERY_TIME, CHOOSE_DELIVERY_TIME_V2 -> {
                chooseDeliveryTime(event)
            }
            GX0 -> {
                performGlobalAction(GLOBAL_ACTION_BACK)
            }
            XN1 -> {
                checkNotification(event)
            }
            RETURN_CART_DIALOG -> {
                clickReturnCartBtn(event)
            }
            else -> {
                clickDialog(event)
            }
        }
    }

    private fun clickReturnCartBtn(event: AccessibilityEvent) {
        val nodes = event.source?.findAccessibilityNodeInfosByText("返回购物车")
        nodes?.forEach { node ->
            node.performAction(ACTION_CLICK)
            Log.d(TAG, "clickDialog confirm")
            return@forEach
        }
    }

    private fun checkNotification(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            if (checkNotificationCount++ > 1) {
                Log.d(TAG, "checkNotificationCount: $checkNotificationCount, return")
                performGlobalAction(GLOBAL_ACTION_BACK)
            }
        } else {
            checkNotificationCount = 0
        }
    }

    private fun pay(event: AccessibilityEvent) {
        val nodes = event.source?.findAccessibilityNodeInfosByText("立即支付")
        nodes?.forEach { node ->
            node.performAction(ACTION_CLICK)
            Log.d(TAG, "onClick pay button")
        }
    }

    private fun chooseDeliveryTime(event: AccessibilityEvent) {
        Log.d(TAG, "chooseDeliveryTime: ${event.source}")
        val nodes = event.source?.findAccessibilityNodeInfosByText("-")
        nodes?.forEach { node ->
            if (node.parent.isEnabled) {
                chooseTimeSuccess = true
                node.parent.performAction(ACTION_CLICK)
                Log.d(TAG, "onClick chooseDeliveryTime")
                return@forEach
            }
        }
        if (!chooseTimeSuccess) {
            performGlobalAction(GLOBAL_ACTION_BACK)
            MainScope().launch {
                delay(100)
                if (currentClassName in listOf(CHOOSE_DELIVERY_TIME, CHOOSE_DELIVERY_TIME_V2)) {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                }
            }
        }
    }

    private fun jumpToCartActivity(event: AccessibilityEvent) {
        if (!enableJumpCart) {
            Log.d(TAG, "enableJumpCart: $enableJumpCart, return")
            return
        }
        val nodes = event.source?.findAccessibilityNodeInfosByText("去结算")
        nodes?.forEach { node ->
            node.performAction(ACTION_CLICK)
            enableJumpCart = false
            Log.d(TAG, "onClick jump to cart")
            return@forEach
        }
    }

    private fun clickDialog(event: AccessibilityEvent) {
        var nodes = event.source?.findAccessibilityNodeInfosByText("继续支付")
        if (nodes == null) {
            nodes = event.source?.findAccessibilityNodeInfosByText("修改送达时间")
        }
        nodes?.forEach { node ->
            node.performAction(ACTION_CLICK)
            Log.d(TAG, "clickDialog confirm")
            return@forEach
        }
    }

    companion object {
        private const val TAG = "DingDongHelper"
        const val PACKAGE_NAME = "com.yaya.zone"
        const val HOME_ACTIVITY = "com.yaya.zone.home.HomeActivity"
        const val CART_ACTIVITY = "cn.me.android.cart.activity.WriteOrderActivity"
        const val CHOOSE_DELIVERY_TIME = "gy"
        const val CHOOSE_DELIVERY_TIME_V2 = "iy"
        const val RETURN_CART_DIALOG = "by"
        const val GX0 = "gx0"
        const val XN1 = "xn1"
    }
}