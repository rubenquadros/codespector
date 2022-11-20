package com.ruben.codespector

import com.ruben.codespector.settings.Parser
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Created by Ruben Quadros on 20/11/22
 **/
fun getMissingAnnotations(parser: Parser, ktClass: KtClass): List<KtParameter> {
    return when (parser) {
        Parser.GSON -> {
            ktClass.getMissingSerializedNameAnnotationParams()
        }
        Parser.MOSHI -> {
            ktClass.getMissingJsonAnnotationParams()
        }
        else -> {
            ktClass.getMissingSerialNameParams()
        }
    }
}