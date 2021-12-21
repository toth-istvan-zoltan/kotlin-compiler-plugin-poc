package zakadabar.kotlin.compiler.plugin

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

class SdcpCommandLineProcessor : CommandLineProcessor {
    companion object {
        val ANNOTATION_OPTION = CliOption(
            "annotation", "<fqname>", "Annotation qualified names",
            required = false, allowMultipleOccurrences = true
        )
        const val PLUGIN_ID = "zakadabar.kotlin.sdcp"
    }

    override val pluginId = PLUGIN_ID
    override val pluginOptions = listOf(ANNOTATION_OPTION)

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) = when (option) {
        ANNOTATION_OPTION -> configuration.appendList(SdcpConfigurationKeys.ANNOTATION, value)
        else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
    }
}
