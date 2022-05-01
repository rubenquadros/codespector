package com.ruben.codespector

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.classVisitor

/**
 * Created by Ruben Quadros on 01/05/22
 **/
class SerializedNameInspector: AbstractKotlinInspection() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): KtVisitorVoid {
        return classVisitor { ktClass ->
            if (ktClass.isData()) {
                //check if serialized name annotation is required.
                ktClass.getPrimaryConstructorParameterList()?.parameters?.forEach { param ->
                    if (!param.annotationEntries.any { it.shortName?.asString() == "SerializedName" }) {
                        holder.registerProblem(
                            param as PsiElement,
                            "Missing SerializedName annotation"
                        )
                    }
                }
            }
        }
    }
}