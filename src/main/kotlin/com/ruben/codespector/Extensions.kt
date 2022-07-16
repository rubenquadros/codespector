package com.ruben.codespector

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.renderer.render

/**
 * Created by Ruben Quadros on 02/05/22
 **/

/**
 * @return list of [KtParameter]
 * This method returns all the params of the data class for which the 'Gson SerializedName' annotation is missing.
 */
fun KtClass.getMissingSerializedNameAnnotationParams(): List<KtParameter> {
    val paramList = mutableListOf<KtParameter>()
    this.getPrimaryConstructorParameterList()?.parameters?.forEach { param ->
        if (!param.annotationEntries.any { it.shortName?.asString() == "SerializedName" }) {
            paramList.add(param)
        }
    }
    return paramList
}

/**
 * @return list of [KtParameter]
 * This method returns all the params of the data class for which the 'Moshi Json' annotation is missing.
 */
fun KtClass.getMissingJsonAnnotationParams(): List<KtParameter> {
    val paramList = mutableListOf<KtParameter>()
    this.getPrimaryConstructorParameterList()?.parameters?.forEach { param ->
        if (!param.annotationEntries.any { it.shortName?.asString() == "Json" }) {
            paramList.add(param)
        }
    }
    return paramList
}

/**
 * @return list of [KtParameter]
 * This method returns all the params of the data class for which the 'Kotlinx SerialName' annotation is missing.
 */
fun KtClass.getMissingSerialNameParams(): List<KtParameter> {
    val paramList = mutableListOf<KtParameter>()
    this.getPrimaryConstructorParameterList()?.parameters?.forEach { param ->
        if (!param.annotationEntries.any { it.shortName?.asString() == "SerialName" }) {
            paramList.add(param)
        }
    }
    return paramList
}

/**
 * @param project
 * This method adds the missing 'Gson SerializedName' annotation to all params of the data class.
 */
fun List<KtParameter>.addSerializedNameAnnotations(project: Project) {
    this.forEach { param ->
        param.addSerializedNameAnnotation(project = project)
    }
}

/**
 * @param project
 * This method adds the missing 'Gson SerializedName' annotation to the current param of the data class.
 */
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

/**
 * @param project
 * This method adds the missing 'Moshi Json' annotation to all params of the data class.
 */
fun List<KtParameter>.addJsonAnnotations(project: Project) {
    this.forEach { param ->
        param.addJsonAnnotation(project = project)
    }
}

/**
 * @param project
 * This method adds the missing 'Moshi Json' annotation to the current param of the data class.
 */
fun KtParameter?.addJsonAnnotation(project: Project) {
    this?.let { param ->
        WriteCommandAction.runWriteCommandAction(project) {
            param.addJsonAnnotation(
                annotationFqName = FqName("com.squareup.moshi.Json"),
                annotationInnerText = "\"${param.name}\""
            )
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(param)
            CodeStyleManager.getInstance(project).reformat(param)
        }
    }
}

/**
 * @param project
 * This method adds the missing 'Kotlinx SerialName' annotation to all params of the data class.
 */
fun List<KtParameter>.addSerialNameAnnotations(project: Project) {
    this.forEach { param ->
        param.addSerialNameAnnotation(project = project)
    }
}

/**
 * @param project
 * This method adds the missing 'Kotlinx SerialName' annotation to the current param of the data class.
 */
fun KtParameter?.addSerialNameAnnotation(project: Project) {
    this?.let { param ->
        WriteCommandAction.runWriteCommandAction(project) {
            param.addAnnotation(
                annotationFqName = FqName("kotlinx.serialization.SerialName"),
                annotationInnerText = "\"${param.name}\""
            )
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(param)
            CodeStyleManager.getInstance(project).reformat(param)
        }
    }
}

/**
 * Taken from [org.jetbrains.kotlin.idea.util.addAnnotation]
 * Modified to include named param for 'Moshi Json' annotation.
 */
private fun KtModifierListOwner.addJsonAnnotation(
    annotationFqName: FqName,
    annotationInnerText: String? = null,
    whiteSpaceText: String = "\n",
    addToExistingAnnotation: ((KtAnnotationEntry) -> Boolean)? = null
): Boolean {
    val annotationText = when (annotationInnerText) {
        null -> "@${annotationFqName.render()}"
        else -> "@${annotationFqName.render()}(name = $annotationInnerText)"
    }

    val psiFactory = KtPsiFactory(this)
    val modifierList = modifierList

    if (modifierList == null) {
        val addedAnnotation = addAnnotationEntry(psiFactory.createAnnotationEntry(annotationText))
        ShortenReferences.DEFAULT.process(addedAnnotation)
        return true
    }

    val entry = findAnnotation(annotationFqName)
    if (entry == null) {
        // no annotation
        val newAnnotation = psiFactory.createAnnotationEntry(annotationText)
        val addedAnnotation = modifierList.addBefore(newAnnotation, modifierList.firstChild) as KtElement
        val whiteSpace = psiFactory.createWhiteSpace(whiteSpaceText)
        modifierList.addAfter(whiteSpace, addedAnnotation)

        ShortenReferences.DEFAULT.process(addedAnnotation)
        return true
    }

    if (addToExistingAnnotation != null) {
        return addToExistingAnnotation(entry)
    }

    return false
}