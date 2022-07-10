package com.ruben.codespector.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Created by Ruben Quadros on 10/07/22
 **/
@State(
    name = "com.ruben.codespector.settings.InspectionSettingState",
    storages = [Storage("Codespector.xml")]
)
class InspectionSettingState: PersistentStateComponent<InspectionSettingState> {

    var parser: Parser = Parser.GSON

    override fun getState(): InspectionSettingState {
        return this
    }

    override fun loadState(state: InspectionSettingState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}

enum class Parser {
    GSON, MOSHI, KOTLINX_SERIALIZATION
}