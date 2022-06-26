package com.ruben.codespector

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert

/**
 * Created by Ruben Quadros on 08/05/22
 **/
class SerializedNameInspectorTest: BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData"

    private fun setup() {
        myFixture.configureByFile("BeforeSerializedNameAnnotation.kt")
        myFixture.enableInspections(SerializedNameInspector())
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
        val action = myFixture.findSingleIntention(Constants.SERIALIZED_NAME_QUICK_FIX)
        Assert.assertNotNull(action)
        myFixture.launchAction(action)
        myFixture.checkResultByFile("AfterSerializedNameAnnotation.kt")
    }
}