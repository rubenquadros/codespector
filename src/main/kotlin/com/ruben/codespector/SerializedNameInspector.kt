package com.ruben.codespector

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtParameter
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
                val paramList: List<KtParameter> = ktClass.getMissingAnnotationParams()
                paramList.forEach {
                    holder.registerProblem(
                        it as PsiElement,
                        "Missing SerializedName annotation",
                        SerializedNameQuickFix()
                    )
                }
            }
        }
    }
}

class SerializedNameQuickFix: LocalQuickFix {
    override fun getFamilyName(): String = Constants.SERIALIZED_NAME_QUICK_FIX

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val param = descriptor.psiElement as? KtParameter
        param?.addSerializedNameAnnotation(project = project)
    }

}