package com.ruben.codespector.settings

data class CodespectorSettings(
    val parser: Parser,
    val packages: Set<String>
)
