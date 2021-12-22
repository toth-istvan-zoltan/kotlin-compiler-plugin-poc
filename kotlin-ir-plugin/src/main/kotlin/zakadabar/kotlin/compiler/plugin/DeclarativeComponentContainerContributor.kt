package zakadabar.kotlin.compiler.plugin

import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.jvm.isJvm
import zakadabar.kotlin.compiler.plugin.diagnostics.DeclarativeDeclarationChecker

class DeclarativeComponentContainerContributor(
    private val annotations: List<String>,
    private val useIr: Boolean,
) : StorageComponentContainerContributor {

    override fun registerModuleComponents(
        container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor
    ) {
        if (!platform.isJvm()) return

        container.useInstance(DeclarativeDeclarationChecker(annotations, useIr))
    }
}
