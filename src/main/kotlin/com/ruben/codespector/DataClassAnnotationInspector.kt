package com.ruben.codespector

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.ruben.codespector.settings.InspectionSettingState
import com.ruben.codespector.settings.Parser
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.classVisitor

/**
 * Created by Ruben Quadros on 01/05/22
 *
 * Inspects the current data class for the required annotation and highlights the params for which the annotation is missing.
 * Also provides a quick fix for the highlighted param.
 **/
class DataClassAnnotationInspector: AbstractKotlinInspection() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): KtVisitorVoid {
        val project = holder.project
        val parser = project.service<InspectionSettingState>().parser

        return classVisitor { ktClass ->
            if (ktClass.isData()) {
                //check if annotation is required.
                val paramList: List<KtParameter> = when (parser) {
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
                paramList.forEach {
                    holder.registerProblem(
                        it as PsiElement,
                        MessageBundle.get("missing.dataclass.annotation", parser.annotation),
                        SerializedNameQuickFix(parser = parser)
                    )
                }
            }
        }
    }
}

/**
 * Adds the required quickfix for the highlighted data class param.
 */
class SerializedNameQuickFix(private val parser: Parser): LocalQuickFix {
    override fun getFamilyName(): String = Constants.DATA_CLASS_ANNOTATION_QUICK_FIX

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val param = descriptor.psiElement as? KtParameter
        param?.let {
            when (parser) {
                Parser.GSON -> it.addSerializedNameAnnotation(project = project)
                Parser.MOSHI -> it.addJsonAnnotation(project = project)
                else -> it.addSerialNameAnnotation(project = project)
            }
        }
    }

}