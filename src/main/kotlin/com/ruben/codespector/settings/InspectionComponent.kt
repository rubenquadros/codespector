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
                        getter = { getCurrentState() == Parser.GSON },
                        setter = { }
                    ).component.addActionListener { setNewState(parser = Parser.GSON) }
                }
                row {
                    radioButton(
                        text = "Moshi",
                        getter = { getCurrentState() == Parser.MOSHI},
                        setter = { }
                    ).component.addActionListener { setNewState(parser = Parser.MOSHI) }
                }
                row {
                    radioButton(
                        text = "Kotlinx-serialization",
                        getter = { getCurrentState() == Parser.KOTLINX_SERIALIZATION},
                        setter = { }
                    ).component.addActionListener { setNewState(parser = Parser.KOTLINX_SERIALIZATION) }
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


