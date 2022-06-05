package com.ruben.codespector

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Created by Ruben Quadros on 02/05/22
 **/
fun KtClass.getMissingAnnotationParams(): List<KtParameter> {
    val paramList = mutableListOf<KtParameter>()
    this.getPrimaryConstructorParameterList()?.parameters?.forEach { param ->
        if (!param.annotationEntries.any { it.shortName?.asString() == "SerializedName" }) {
            paramList.add(param)
        }
    }
    return paramList
}


fun List<KtParameter>.addSerializedNameAnnotations(project: Project) {
    this.forEach { param ->
        WriteCommandAction.runWriteCommandAction(project) {
            param.addAnnotation(
                annotationFqName = FqName("com.google.gson.annotations.SerializedName"),
                annotationInnerText = "\"${param.name}\""
            )
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(param)
            CodeStyleManager.getInstance(project).reformat(param)
        }
    }
}

fun KtParameter?.addSerializedNameAnnotation(project: Project) {
    this?.let { param ->
        WriteCommandAction.runWriteCommandAction(project) {
            param.addAnnotation(
                annotationFqName = FqName("com.google.gson.annotations.SerializedName"),
                annotationInnerText = "\"${param.name}\""
            )
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(param)
            CodeStyleManager.getInstance(project).reformat(param)
        }
    }
}