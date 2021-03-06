/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zakadabar.kotlin.compiler.plugin.diagnostics

import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap

object DefaultErrorMessagesDeclarative : DefaultErrorMessages.Extension {
    private val MAP = DiagnosticFactoryToRendererMap("AnnotationProcessing")
    override fun getMap() = MAP

    init {
        MAP.put(ErrorsDeclarative.NO_SDCP_FUNCION_IN_SUPERCLASS, "Sensible defaults function was not found in the superclass")
        MAP.put(
            ErrorsDeclarative.SDCP_ON_INNER_CLASS,
            "Sensible defaults function generation for inner classes is deprecated and will be prohibited soon"
        )
        MAP.put(ErrorsDeclarative.SDCP_ON_INNER_CLASS_ERROR, "Sensible defaults function generation is not possible for inner classes")
        MAP.put(
            ErrorsDeclarative.SDCP_ON_LOCAL_CLASS,
            "Sensible defaults function generation for local classes is deprecated and will be prohibited soon"
        )
        MAP.put(ErrorsDeclarative.SDCP_ON_LOCAL_CLASS_ERROR, "Sensible defaults function generation is not possible for local classes")
    }
}
