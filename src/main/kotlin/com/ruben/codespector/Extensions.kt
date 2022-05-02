package com.ruben.codespector

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Created by Ruben Quadros on 02/05/22
 **/
fun KtClass.getMissingAnnotationParam(): KtParameter? {
    this.getPrimaryConstructorParameterList()?.parameters?.forEach { param ->
        if (!param.annotationEntries.any { it.shortName?.asString() == "SerializedName" }) {
            return param
        }
    }
    return null
}