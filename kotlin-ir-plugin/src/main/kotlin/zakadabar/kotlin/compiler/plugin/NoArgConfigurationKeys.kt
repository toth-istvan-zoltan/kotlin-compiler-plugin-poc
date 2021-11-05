/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package zakadabar.kotlin.compiler.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.jvm.isJvm
import zakadabar.kotlin.compiler.plugin.NoArgCommandLineProcessor.Companion.SUPPORTED_PRESETS
import zakadabar.kotlin.compiler.plugin.NoArgConfigurationKeys.ANNOTATION
import zakadabar.kotlin.compiler.plugin.NoArgConfigurationKeys.INVOKE_INITIALIZERS
import zakadabar.kotlin.compiler.plugin.NoArgConfigurationKeys.PRESET
import zakadabar.kotlin.compiler.plugin.diagnostics.CliNoArgDeclarationChecker

object NoArgConfigurationKeys {
    val ANNOTATION: CompilerConfigurationKey<List<String>> =
        CompilerConfigurationKey.create("annotation qualified name")

    val PRESET: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create("annotation preset")

    val INVOKE_INITIALIZERS: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create(
        "invoke instance initializers in a no-arg constructor"
    )
}

class NoArgCommandLineProcessor : CommandLineProcessor {
    companion object {
        val SUPPORTED_PRESETS = mapOf(
            "jpa" to listOf("javax.persistence.Entity", "javax.persistence.Embeddable", "javax.persistence.MappedSuperclass")
        )

        val ANNOTATION_OPTION = CliOption(
            "annotation", "<fqname>", "Annotation qualified names",
            required = false, allowMultipleOccurrences = true
        )

        val PRESET_OPTION = CliOption(
            "preset", "<name>", "Preset name (${SUPPORTED_PRESETS.keys.joinToString()})",
            required = false, allowMultipleOccurrences = true
        )

        val INVOKE_INITIALIZERS_OPTION = CliOption(
            "invokeInitializers", "true/false",
            "Invoke instance initializers in a no-arg constructor",
            required = false, allowMultipleOccurrences = false
        )

        const val PLUGIN_ID = "org.jetbrains.kotlin.noarg"
    }

    override val pluginId = PLUGIN_ID
    override val pluginOptions = listOf(ANNOTATION_OPTION, PRESET_OPTION, INVOKE_INITIALIZERS_OPTION)

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) = when (option) {
        ANNOTATION_OPTION -> configuration.appendList(ANNOTATION, value)
        PRESET_OPTION -> configuration.appendList(PRESET, value)
        INVOKE_INITIALIZERS_OPTION -> configuration.put(INVOKE_INITIALIZERS, value == "true")
        else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
    }
}

class NoArgComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val annotations = mutableListOf("Fancy") // configuration.get(ANNOTATION).orEmpty().toMutableList()
        configuration.get(PRESET)?.forEach { preset ->
            SUPPORTED_PRESETS[preset]?.let { annotations += it }
        }
        if (annotations.isNotEmpty()) {
            registerNoArgComponents(
                project, annotations, configuration.getBoolean(JVMConfigurationKeys.IR), configuration.getBoolean(INVOKE_INITIALIZERS),
            )
        }
    }

    internal companion object {
        fun registerNoArgComponents(project: Project, annotations: List<String>, useIr: Boolean, invokeInitializers: Boolean) {
            StorageComponentContainerContributor.registerExtension(project, CliNoArgComponentContainerContributor(annotations, useIr))
            ExpressionCodegenExtension.registerExtension(project, CliNoArgExpressionCodegenExtension(annotations, invokeInitializers))
            IrGenerationExtension.registerExtension(project, NoArgIrGenerationExtension(annotations, invokeInitializers))
        }
    }
}

private class CliNoArgComponentContainerContributor(
    private val annotations: List<String>,
    private val useIr: Boolean,
) : StorageComponentContainerContributor {
    override fun registerModuleComponents(
        container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor
    ) {
        if (!platform.isJvm()) return

        container.useInstance(CliNoArgDeclarationChecker(annotations, useIr))
    }
}
