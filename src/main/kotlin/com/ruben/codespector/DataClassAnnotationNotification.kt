package com.ruben.codespector

import com.intellij.codeInsight.daemon.impl.analysis.FileHighlightingSetting
import com.intellij.codeInsight.daemon.impl.analysis.HighlightLevelUtil
import com.intellij.codeInsight.daemon.impl.analysis.HighlightingLevelManager
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.EditorNotifications
import com.ruben.codespector.settings.InspectionSettingState
import com.ruben.codespector.settings.Parser
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtParameter
import java.util.function.Function
import javax.swing.JComponent

/**
 * Created by Ruben Quadros on 01/05/22
 *
 * Displays the editor notification when annotations are missing for the data class params.
 * Notification is shown for both outer class and inner classes.
 **/
class DataClassAnnotationNotification: EditorNotificationProvider {

    override fun collectNotificationData(project: Project, file: VirtualFile): Function<in FileEditor, out JComponent?>? {
        val psiFile = file.toPsiFile(project)
        val ktFile = psiFile as? KtFile
        val psiClasses = ktFile?.classes
        val settingsState = project.service<InspectionSettingState>()
        val parser = settingsState.parser
        val enabledPackages = settingsState.packages

        psiClasses?.let { psiClassList ->
            psiClassList.forEach { psiClass ->
                //check for main classes
                (psiClass as? KtLightClassForSourceDeclaration)?.let { ktLightClassForSourceDeclaration ->
                    val ktClass = ktLightClassForSourceDeclaration.kotlinOrigin as? KtClass
                    if (ktClass?.shouldInspect(enabledPackages) == true) {
                        val paramList = getMissingAnnotations(parser = parser, ktClass = ktClass)
                        if (paramList.isNotEmpty() && isErrorHighlighted(psiFile, project)) {
                            return Function {
                                val panel = EditorNotificationPanel()
                                panel.text(
                                    MessageBundle.get(
                                        "message.dataclass.annotation.notification",
                                        parser.annotation,
                                        ktClass.name.orEmpty()
                                    )
                                )

                                panel.createActionLabel(MessageBundle.get("message.add.dataclass.annotation")) {
                                    addAnnotation(paramList = paramList, project = project, parser = parser)

                                    //update the notification
                                    EditorNotifications.getInstance(project).updateAllNotifications()
                                }

                                panel.createActionLabel(MessageBundle.get("message.ignore")) {
                                    ignoreInspection(psiFile = psiFile, project = project)
                                }

                                panel
                            }
                        }
                    }
                }

                //check for inner classes
                psiClass.allInnerClasses.forEach { innerClass ->
                    (innerClass as? KtLightClassForSourceDeclaration)?.let { ktLightClassForSourceDeclaration ->
                        val ktClass = ktLightClassForSourceDeclaration.kotlinOrigin as? KtClass
                        if (ktClass?.shouldInspect(enabledPackages) == true) {
                            val paramList = getMissingAnnotations(parser = parser, ktClass = ktClass)
                            if (paramList.isNotEmpty() && isErrorHighlighted(psiFile, project)) {
                                return Function {
                                    val panel = EditorNotificationPanel()
                                    panel.text(
                                        MessageBundle.get(
                                            "message.dataclass.annotation.notification",
                                            parser.annotation,
                                            ktClass.name.orEmpty()
                                        )
                                    )

                                    panel.createActionLabel(MessageBundle.get("message.add.dataclass.annotation")) {
                                        addAnnotation(paramList = paramList, project = project, parser = parser)

                                        //update the notification
                                        EditorNotifications.getInstance(project).updateAllNotifications()
                                    }

                                    panel.createActionLabel(MessageBundle.get("message.ignore")) {
                                        ignoreInspection(psiFile = psiFile, project = project)
                                    }

                                    panel
                                }
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    private fun addAnnotation(paramList: List<KtParameter>, project: Project, parser: Parser) {
        when (parser) {
            Parser.GSON -> paramList.addSerializedNameAnnotations(project = project)
            Parser.MOSHI -> paramList.addJsonAnnotations(project = project)
            else -> paramList.addSerialNameAnnotations(project = project)
        }
    }

    private fun ignoreInspection(psiFile: PsiFile, project: Project) {
        if (isErrorHighlighted(psiFile, project)) {
            HighlightLevelUtil.forceRootHighlighting(psiFile, FileHighlightingSetting.SKIP_HIGHLIGHTING)
            //update the notification
            EditorNotifications.getInstance(project).updateAllNotifications()
        }
        InjectedLanguageManager.getInstance(project).dropFileCaches(psiFile)
    }

    private fun isErrorHighlighted(psiFile: PsiFile, project: Project): Boolean {
        val manager = HighlightingLevelManager.getInstance(project)
        val isSyntaxHighlightingEnabled = manager.shouldHighlight(psiFile)
        val isInspectionsHighlightingEnabled = manager.shouldInspect(psiFile)

        return isSyntaxHighlightingEnabled && isInspectionsHighlightingEnabled
    }

    private fun KtClass?.shouldInspect(enabledPackages: Set<String>): Boolean {
        return this != null && this.isData() && this.isEnabledClass(enabledPackages)
    }

}