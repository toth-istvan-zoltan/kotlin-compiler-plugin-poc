package zakadabar.kotlin.compiler.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.dump
import zakadabar.kotlin.compiler.plugin.lower.SdcpIrTransformer

internal class SdcpIrGenerationExtension(
    private val annotations: List<String>
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.accept(SdcpIrTransformer(pluginContext, annotations), null)
        println(moduleFragment.dump())
    }

}

