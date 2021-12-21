package zakadabar.kotlin.compiler.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor

/**
 * Registers the extensions into the compiler.
 */
@AutoService(ComponentRegistrar::class)
class SdcpComponentRegistrar : ComponentRegistrar {

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {

        val annotations = mutableListOf("UiElement") // configuration.get(ANNOTATION).orEmpty().toMutableList()

        if (annotations.isNotEmpty()) {
            registerSdcpComponents(
                project, annotations, configuration.getBoolean(JVMConfigurationKeys.IR)
            )
        }
    }

    fun registerSdcpComponents(project: Project, annotations: List<String>, useIr: Boolean) {

        // registers SdcpDeclarationChecker to check if the plugin can generate the
        // sensible defaults function for the annotated class

        StorageComponentContainerContributor.registerExtension(
            project,
            SdcpComponentContainerContributor(annotations, useIr)
        )
        
        IrGenerationExtension.registerExtension(
            project,
            SdcpIrGenerationExtension(annotations)
        )
    }

}
