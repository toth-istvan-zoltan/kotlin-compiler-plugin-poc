/*
 * Copyright (C) 2020 Brian Norman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zakadabar.kotlin.compiler.plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import kotlin.test.assertEquals
import org.junit.Test

val small = """
    import kotlin.reflect.KProperty0

    annotation class UiElement

    object A {
        val b = "Hello"
    }

    class ZkElement {
        
    }

    inline operator fun ZkElement.unaryPlus() {
    
    }

    inline operator fun KProperty0<String>.unaryPlus() {
    
    }

    fun zke(builder : ZkElement.() -> Unit) : ZkElement = ZkElement().also { it.builder() }

    @UiElement
    fun a() = zke { }

    @UiElement
    fun b() {
        + a()
        + A::b
    }

""".trimIndent()

val big = """
    annotation class UiElement

    fun a() : String = ""
    
    @UiElement
    class A(
        val s1 : String,
        var s2 : String,
        val s3 : String?,
        var s4 : String?,
        val s5 : String = "CS5L",
        var s6 : String = "CS5R",
        val s7 : String? = null,
        var s8 : String? = null,
        val s9 : String? = "CS6L",
        var s10 : String? = "CS6R",
        val s11 : String = a()
    )

    val a1 = A("CS1", "CS2", null, null)
    val a2 = A("CS1", "CS2", null, null, s7 = a())
"""

class IrPluginTest {

    @Test
    fun `IR plugin success`() {

        val result = compile(
            sourceFile = SourceFile.kotlin("main.kt", small)
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}

fun compile(
    sourceFiles: List<SourceFile>
): KotlinCompilation.Result {
    return KotlinCompilation().apply {
        sources = sourceFiles
        useIR = true
        compilerPlugins = listOf(SdcpComponentRegistrar())
        inheritClassPath = true
    }.compile()
}

fun compile(
    sourceFile: SourceFile
): KotlinCompilation.Result {
    return compile(listOf(sourceFile))
}
