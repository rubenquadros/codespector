package com.ruben.codespector

import com.intellij.codeInsight.daemon.impl.analysis.FileHighlightingSetting
import com.intellij.codeInsight.daemon.impl.analysis.HighlightLevelUtil
import com.intellij.codeInsight.daemon.impl.analysis.HighlightingLevelManager
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile

/**
 * Created by Ruben Quadros on 01/05/22
 **/
class SerializedNameNotification: EditorNotifications.Provider<EditorNotificationPanel>() {

    companion object {
        val KEY = Key.create<EditorNotificationPanel>("Add missing annotation?")
    }

    override fun getKey(): Key<EditorNotificationPanel> = KEY

    override fun createNotificationPanel(
        file: VirtualFile,
        fileEditor: FileEditor,
        project: Project
    ): EditorNotificationPanel? {
        val psiFile = file.toPsiFile(project)
        val ktFile = psiFile as? KtFile
        val psiClasses = ktFile?.classes
        psiClasses?.let { psiClassList ->
            psiClassList.forEach { psiClass ->
                //check for main classes
                (psiClass as? KtLightClassForSourceDeclaration)?.let { ktLightClassForSourceDeclaration ->
                    val ktClass = ktLightClassForSourceDeclaration.kotlinOrigin as? KtClass
                    if (ktClass?.isData() == true) {
                        val paramList = ktClass.getMissingAnnotationParams()
                        if (paramList.isNotEmpty()) {
                            removeNotification(fileEditor)
                            return createPanel(
                                psiFile = psiFile,
                                project = project,
                                name = ktClass.name.orEmpty(),
                                onAddClick = { addAnnotation(ktClass, project) },
                                onIgnoreClick = { ignoreInspection(fileEditor, psiFile, project) }
                            )
                        } else {
                            removeNotification(fileEditor)
                        }
                    }
                }

                //check for inner classes
                psiClass.allInnerClasses.forEach { innerClass ->
                    (innerClass as? KtLightClassForSourceDeclaration)?.let { ktLightClassForSourceDeclaration ->
                        val ktClass = ktLightClassForSourceDeclaration.kotlinOrigin as? KtClass
                        if (ktClass?.isData() == true) {
                            val paramList = ktClass.getMissingAnnotationParams()
                            if (paramList.isNotEmpty()) {
                                removeNotification(fileEditor)
                                return createPanel(
                                    psiFile = psiFile,
                                    project = project,
                                    name = ktClass.name.orEmpty(),
                                    onAddClick = { addAnnotation(ktClass, project) },
                                    onIgnoreClick = { ignoreInspection(fileEditor, psiFile, project) }
                                )
                            } else {
                                removeNotification(fileEditor)
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    private fun createPanel(psiFile: PsiFile, project: Project, name: String, onAddClick: () -> Unit, onIgnoreClick: () -> Unit): EditorNotificationPanel? {
        return if (isErrorHighlighted(psiFile, project)) {
            val panel = EditorNotificationPanel()
            panel.text("Add SerializedName annotation to $name params?")

            panel.createActionLabel("Add annotation for data class") {
                onAddClick.invoke()
            }

            panel.createActionLabel("Ignore") {
                onIgnoreClick.invoke()
            }

            panel
        } else {
            null
        }
    }

    private fun addAnnotation(ktClass: KtClass, project: Project) {
        val paramList = ktClass.getMissingAnnotationParams()
        paramList.addSerializedNameAnnotations(project = project)
    }

    private fun removeNotification(fileEditor: FileEditor) {
        fileEditor.getUserData(KEY)?.removeAll()
    }

    private fun ignoreInspection(fileEditor: FileEditor, psiFile: PsiFile, project: Project) {
        if (isErrorHighlighted(psiFile, project)) {
            HighlightLevelUtil.forceRootHighlighting(psiFile, FileHighlightingSetting.SKIP_HIGHLIGHTING)
            removeNotification(fileEditor)
        }
        InjectedLanguageManager.getInstance(project).dropFileCaches(psiFile)
    }

    private fun isErrorHighlighted(psiFile: PsiFile, project: Project): Boolean {
        val manager = HighlightingLevelManager.getInstance(project)
        val isSyntaxHighlightingEnabled = manager.shouldHighlight(psiFile)
        val isInspectionsHighlightingEnabled = manager.shouldInspect(psiFile)

        return isSyntaxHighlightingEnabled && isInspectionsHighlightingEnabled
    }

}