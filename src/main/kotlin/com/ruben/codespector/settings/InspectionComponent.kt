package com.ruben.codespector.settings

import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.layout.panel
import javax.swing.JPanel

/**
 * Created by Ruben Quadros on 10/07/22
 *
 * UI component for the plugin settings.
 **/
class InspectionComponent(private val inspectionSettingState: InspectionSettingState) {

    private var parser: Parser = Parser.GSON

    init {
        parser = inspectionSettingState.state.parser
    }

    var gsonButton: JBRadioButton? = null
    var moshiButton: JBRadioButton? = null
    var kotlinxButton: JBRadioButton? = null

    fun getPanel(): JPanel = panel {

        titledRow("Configure Plugin Settings For Your Project") {
            row("Select JSON parser:") {
                buttonGroup {
                    row {
                        gsonButton = radioButton(
                            text = "Gson",
                        ).component.apply {
                            isSelected = getCurrentState() == Parser.GSON
                            addActionListener { setNewState(parser = Parser.GSON) }
                        }
                    }
                    row {
                        moshiButton = radioButton(
                            text = "Moshi",
                        ).component.apply {
                            isSelected = getCurrentState() == Parser.MOSHI
                            addActionListener { setNewState(parser = Parser.MOSHI) }
                        }
                    }
                    row {
                        kotlinxButton = radioButton(
                            text = "Kotlinx-serialization",
                        ).component.apply {
                            isSelected = getCurrentState() == Parser.KOTLINX_SERIALIZATION
                            addActionListener { setNewState(parser = Parser.KOTLINX_SERIALIZATION) }
                        }
                    }
                }
            }
        }
    }

    fun resetState() {
        this.parser = inspectionSettingState.state.parser
        gsonButton?.isSelected = getCurrentState() == Parser.GSON
        moshiButton?.isSelected = getCurrentState() == Parser.MOSHI
        kotlinxButton?.isSelected = getCurrentState() == Parser.KOTLINX_SERIALIZATION
    }

    fun getCurrentState(): Parser {
        return this.parser
    }

    private fun setNewState(parser: Parser) {
        this.parser = parser
    }
}


