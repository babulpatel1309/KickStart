package com.kickstart.utils

interface OperationResponder {
    fun OnComplete(returnValue: Any? = null)
}