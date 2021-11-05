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
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.junit.Test

class IrPluginTest {

    @Test
    fun `IR plugin success`() {

        val result = compile(
            sourceFile = SourceFile.kotlin(
                "main.kt",
                """
                    annotation class Fancy

                    fun main() {
                      println(debug())
                    }
                    
                    fun debug() = "Hello, World!"

                    @Fancy
                    class A(
                        val b : String
                    )
                """
            )
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
        compilerPlugins = listOf(NoArgComponentRegistrar(), ZakadabarComponentRegistrar())
        inheritClassPath = true
    }.compile()
}

fun compile(
    sourceFile: SourceFile
): KotlinCompilation.Result {
    return compile(listOf(sourceFile))
}