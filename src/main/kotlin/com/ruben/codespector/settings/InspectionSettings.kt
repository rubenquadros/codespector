package com.ruben.codespector.settings

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotifications
import com.ruben.codespector.MessageBundle
import javax.swing.JComponent

/**
 * Created by Ruben Quadros on 09/07/22
 *
 * Manages the settings UI component.
 **/
class InspectionSettings(project: Project): Configurable {

    private val inspectionSettingState = project.service<InspectionSettingState>()
    private val editorNotifications = EditorNotifications.getInstance(project)
    private val psiManager = PsiManager.getInstance(project)
    private val codeAnalyser = DaemonCodeAnalyzer.getInstance(project)

    private var component: InspectionComponent? = null

    override fun createComponent(): JComponent? {
        component = InspectionComponent(inspectionSettingState = inspectionSettingState)
        return component?.getPanel()
    }

    override fun isModified(): Boolean {
        return inspectionSettingState.parser != component?.getCurrentState()
    }

    override fun apply() {
        component?.getCurrentState()?.let {
            inspectionSettingState.parser = it
            restartAnalysis()
            editorNotifications.updateAllNotifications()
        }
    }

    override fun reset() {
        super.reset()
        component?.resetState()
    }

    override fun getDisplayName(): String {
        return MessageBundle.get("settings.name")
    }

    private fun restartAnalysis() {
        psiManager.dropPsiCaches()
        psiManager.dropResolveCaches()
        codeAnalyser.restart()
    }
}