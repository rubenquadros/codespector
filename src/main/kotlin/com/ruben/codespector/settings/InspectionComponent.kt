package com.ruben.codespector.settings

import com.intellij.ui.layout.panel
import javax.swing.JPanel

/**
 * Created by Ruben Quadros on 10/07/22
 **/
class InspectionComponent(inspectionSettingState: InspectionSettingState) {

    private var parser: Parser = Parser.GSON

    init {
        parser = inspectionSettingState.state.parser
    }

    fun getPanel(): JPanel = panel {

        row("Configure plugin settings for your project") {
            //empty
        }

        row("Select JSON parser:") {
            buttonGroup {
                row {
                    radioButton(
                        text = "Gson",
                    ).component.apply {
                        isSelected = getCurrentState() == Parser.GSON
                        addActionListener { setNewState(parser = Parser.GSON) }
                    }
                }
                row {
                    radioButton(
                        text = "Moshi",
                    ).component.apply {
                        isSelected = getCurrentState() == Parser.MOSHI
                        addActionListener { setNewState(parser = Parser.MOSHI) }
                    }
                }
                row {
                    radioButton(
                        text = "Kotlinx-serialization",
                    ).component.apply {
                        isSelected = getCurrentState() == Parser.KOTLINX_SERIALIZATION
                        addActionListener { setNewState(parser = Parser.KOTLINX_SERIALIZATION) }
                    }
                }
            }
        }
    }

    fun getCurrentState(): Parser {
        return this.parser
    }

    private fun setNewState(parser: Parser) {
        this.parser = parser
    }
}


