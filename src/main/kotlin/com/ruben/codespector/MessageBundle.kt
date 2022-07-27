package com.ruben.codespector

import com.intellij.AbstractBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

/**
 * Created by Ruben Quadros on 23/07/22
 **/
object MessageBundle : AbstractBundle(BUNDLE_NAME) {

    fun get(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any?): String {
        return getMessage(key, *params)
    }
}

@NonNls
private const val BUNDLE_NAME = "messages.CodespectorBundle"