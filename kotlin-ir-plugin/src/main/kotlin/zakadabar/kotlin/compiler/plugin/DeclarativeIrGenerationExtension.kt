package zakadabar.kotlin.compiler.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.dump
import zakadabar.kotlin.compiler.plugin.lower.DeclarativeIrTransformer
import zakadabar.kotlin.compiler.plugin.lower.DeclarativeIrVisitor

internal class DeclarativeIrGenerationExtension(
    private val annotations: List<String>
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        println("================  BEFORE  ================")
        moduleFragment.accept(DeclarativeIrVisitor(pluginContext, annotations), null)
        moduleFragment.transform(DeclarativeIrTransformer(pluginContext, annotations), null)
        println("================  AFTER  ================")
        moduleFragment.accept(DeclarativeIrVisitor(pluginContext, annotations), null)
//        println(moduleFragment.dump())
    }

}

