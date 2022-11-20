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
import com.intellij.ui.EditorNotifications
import com.ruben.codespector.settings.InspectionSettingState
import com.ruben.codespector.settings.Parser
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Created by Ruben Quadros on 01/05/22
 *
 * Displays the editor notification when annotations are missing for the data class params.
 * Notification is shown for both outer class and inner classes.
 **/
class DataClassAnnotationNotification: EditorNotifications.Provider<EditorNotificationPanel>() {

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
        val parser = project.service<InspectionSettingState>().parser
        psiClasses?.let { psiClassList ->
            psiClassList.forEach { psiClass ->
                //check for main classes
                (psiClass as? KtLightClassForSourceDeclaration)?.let { ktLightClassForSourceDeclaration ->
                    val ktClass = ktLightClassForSourceDeclaration.kotlinOrigin as? KtClass
                    if (ktClass?.isData() == true) {
                        val paramList = getMissingAnnotations(parser = parser, ktClass = ktClass)
                        if (paramList.isNotEmpty()) {
                            removeNotification(fileEditor)
                            return createPanel(
                                psiFile = psiFile,
                                project = project,
                                annotation = parser.annotation,
                                name = ktClass.name.orEmpty(),
                                onAddClick = { addAnnotation(paramList = paramList, project = project, parser = parser) },
                                onIgnoreClick = { ignoreInspection(fileEditor = fileEditor, psiFile = psiFile, project = project) }
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
                            val paramList = getMissingAnnotations(parser = parser, ktClass = ktClass)
                            if (paramList.isNotEmpty()) {
                                removeNotification(fileEditor)
                                return createPanel(
                                    psiFile = psiFile,
                                    project = project,
                                    annotation = parser.annotation,
                                    name = ktClass.name.orEmpty(),
                                    onAddClick = { addAnnotation(paramList = paramList, project = project, parser = parser) },
                                    onIgnoreClick = { ignoreInspection(fileEditor = fileEditor, psiFile = psiFile, project = project) }
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

    private fun createPanel(
        psiFile: PsiFile,
        project: Project,
        name: String,
        annotation: String,
        onAddClick: () -> Unit,
        onIgnoreClick: () -> Unit
    ): EditorNotificationPanel? {
        return if (isErrorHighlighted(psiFile, project)) {
            val panel = EditorNotificationPanel()
            panel.text(MessageBundle.get("message.dataclass.annotation.notification", annotation, name))

            panel.createActionLabel(MessageBundle.get("message.add.dataclass.annotation")) {
                onAddClick.invoke()
            }

            panel.createActionLabel(MessageBundle.get("message.ignore")) {
                onIgnoreClick.invoke()
            }

            panel
        } else {
            null
        }
    }

    private fun addAnnotation(paramList: List<KtParameter>, project: Project, parser: Parser) {
        when (parser) {
            Parser.GSON -> paramList.addSerializedNameAnnotations(project = project)
            Parser.MOSHI -> paramList.addJsonAnnotations(project = project)
            else -> paramList.addSerialNameAnnotations(project = project)
        }
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