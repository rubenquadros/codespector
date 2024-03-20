package com.ruben.codespector

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.ruben.codespector.settings.InspectionSettingState
import com.ruben.codespector.settings.Parser
import org.junit.Assert

/**
 * Created by Ruben Quadros on 16/07/22
 **/
class JsonInspectorTest: BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData"

    private fun setup() {
        myFixture.project.service<InspectionSettingState>().apply {
            parser = Parser.MOSHI
            packages = mutableSetOf()
        }
        myFixture.configureByFile("BeforeJsonAnnotation.kt")
        myFixture.enableInspections(DataClassAnnotationInspector())
    }

    fun testProblemsAreHighlighted() {
        setup()
        val highlights = myFixture.doHighlighting()
        Assert.assertFalse(highlights.isEmpty())
    }

    fun testQuickFixIsProvided() {
        setup()
        myFixture.doHighlighting()
        val quickFix = myFixture.getAllQuickFixes()
        Assert.assertFalse(quickFix.isEmpty())
    }

    fun testQuickFixIsFixingTheProblem() {
        setup()
        myFixture.doHighlighting()
        val action = myFixture.findSingleIntention(Constants.DATA_CLASS_ANNOTATION_QUICK_FIX)
        Assert.assertNotNull(action)
        myFixture.launchAction(action)
        myFixture.checkResultByFile("AfterJsonAnnotation.kt")
    }
}