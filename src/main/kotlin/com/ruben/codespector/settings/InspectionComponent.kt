package com.ruben.codespector.settings

import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.ruben.codespector.MessageBundle
import javax.swing.JPanel

/**
 * Created by Ruben Quadros on 10/07/22
 *
 * UI component for the plugin settings.
 **/
class InspectionComponent(private val inspectionSettingState: InspectionSettingState) {

    private var codespectorSettings = CodespectorSettings(
        parser = Parser.GSON,
        packages = emptySet()
    )

    init {
        codespectorSettings = codespectorSettings.copy(
            parser = inspectionSettingState.state.parser,
            packages = inspectionSettingState.state.packages
        )
    }

    private val packageTable = PackageTable(
        packages = codespectorSettings.packages,
        packageTableListener = object : PackageTable.PackageTableListener {
            override fun onPackagesChanged(packages: Set<String>) {
                updatePackages(packages)
            }
        }
    )

    private var gsonButton: JBRadioButton? = null
    private var moshiButton: JBRadioButton? = null
    private var kotlinxButton: JBRadioButton? = null

    fun getPanel(): JPanel = panel {

        row {
            label(MessageBundle.get("settings.title"))
        }

        separator()

        row {
            this@panel.buttonsGroup(MessageBundle.get("parser.title")) {
                row {
                    gsonButton = radioButton(
                        text = MessageBundle.get("parser.gson")
                    ).component.apply {
                        isSelected = codespectorSettings.parser == Parser.GSON
                        addActionListener { setNewParser(parser = Parser.GSON) }
                    }
                }
                row {
                    moshiButton = radioButton(
                        text = MessageBundle.get("parser.moshi")
                    ).component.apply {
                        isSelected = codespectorSettings.parser == Parser.MOSHI
                        addActionListener { setNewParser(parser = Parser.MOSHI) }
                    }
                }
                row {
                    kotlinxButton = radioButton(
                        text = MessageBundle.get("parser.kotlinx")
                    ).component.apply {
                        isSelected = codespectorSettings.parser == Parser.KOTLINX_SERIALIZATION
                        addActionListener { setNewParser(parser = Parser.KOTLINX_SERIALIZATION) }
                    }
                }
            }
        }

        separator()

        row {
            label(MessageBundle.get("inspection.package.title"))
        }

        row {
            label(MessageBundle.get("inspection.package.note"))
        }

        row {
            resizableRow().scrollCell(packageTable.createTable()).align(Align.FILL)
        }
    }

    fun resetState() {
        codespectorSettings = codespectorSettings.copy(
            parser = inspectionSettingState.state.parser,
            packages = inspectionSettingState.state.packages
        )
        gsonButton?.isSelected = codespectorSettings.parser == Parser.GSON
        moshiButton?.isSelected = codespectorSettings.parser == Parser.MOSHI
        kotlinxButton?.isSelected = codespectorSettings.parser == Parser.KOTLINX_SERIALIZATION
    }

    fun getCurrentState(): CodespectorSettings {
        return this.codespectorSettings
    }

    fun disposeResources() {
        packageTable.disposeResources()
    }

    private fun setNewParser(parser: Parser) {
        codespectorSettings = codespectorSettings.copy(parser = parser)
    }

    private fun updatePackages(packages: Set<String>) {
        codespectorSettings = codespectorSettings.copy(packages = packages)
    }
}