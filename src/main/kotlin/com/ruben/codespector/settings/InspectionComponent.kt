package com.ruben.codespector.settings

import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.layout.panel
import com.ruben.codespector.MessageBundle
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

        titledRow(MessageBundle.get("settings.title")) {
            row(MessageBundle.get("parser.title")) {
                buttonGroup {
                    row {
                        gsonButton = radioButton(
                            text = MessageBundle.get("parser.gson")
                        ).component.apply {
                            isSelected = parser == Parser.GSON
                            addActionListener { setNewState(parser = Parser.GSON) }
                        }
                    }
                    row {
                        moshiButton = radioButton(
                            text = MessageBundle.get("parser.moshi")
                        ).component.apply {
                            isSelected = parser == Parser.MOSHI
                            addActionListener { setNewState(parser = Parser.MOSHI) }
                        }
                    }
                    row {
                        kotlinxButton = radioButton(
                            text = MessageBundle.get("parser.kotlinx")
                        ).component.apply {
                            isSelected = parser == Parser.KOTLINX_SERIALIZATION
                            addActionListener { setNewState(parser = Parser.KOTLINX_SERIALIZATION) }
                        }
                    }
                }
            }
        }
    }

    fun resetState() {
        this.parser = inspectionSettingState.state.parser
        gsonButton?.isSelected = parser == Parser.GSON
        moshiButton?.isSelected = parser == Parser.MOSHI
        kotlinxButton?.isSelected = parser == Parser.KOTLINX_SERIALIZATION
    }

    fun getCurrentState(): Parser {
        return this.parser
    }

    private fun setNewState(parser: Parser) {
        this.parser = parser
    }
}


