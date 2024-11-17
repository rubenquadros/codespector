package com.ruben.codespector

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.ruben.codespector.settings.InspectionSettingState
import com.ruben.codespector.settings.Parser

/**
 * Created by Ruben Quadros on 16/07/22
 **/
class SerialNameInspectorTest: BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData"

    private fun setup() {
        myFixture.project.service<InspectionSettingState>().apply {
            parser = Parser.KOTLINX_SERIALIZATION
            packages = mutableSetOf()
        }
        myFixture.configureByFile("BeforeSerialNameAnnotation.kt")
        myFixture.enableInspections(DataClassAnnotationInspector())
    }

    fun testProblemsAreHighlighted() {
        setup()
        val highlights = myFixture.doHighlighting()
        assertFalse(highlights.isEmpty())
    }

    fun testQuickFixIsProvided() {
        setup()
        myFixture.doHighlighting()
        val quickFix = myFixture.getAllQuickFixes()
        assertFalse(quickFix.isEmpty())
    }

    fun testQuickFixIsFixingTheProblem() {
        setup()
        myFixture.doHighlighting()
        val action = myFixture.findSingleIntention(Constants.DATA_CLASS_ANNOTATION_QUICK_FIX)
        assertNotNull(action)
        myFixture.launchAction(action)
        myFixture.checkResultByFile("AfterSerialNameAnnotation.kt")
    }

}