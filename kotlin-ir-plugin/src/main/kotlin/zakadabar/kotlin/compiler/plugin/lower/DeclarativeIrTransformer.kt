package zakadabar.kotlin.compiler.plugin.lower

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.extensions.AnnotationBasedExtension
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.interpreter.toIrConst
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.copyTypeAndValueArgumentsFrom
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtModifierListOwner

class DeclarativeIrTransformer(
    private val context: IrPluginContext,
    private val annotations: List<String>
) : IrElementTransformerVoidWithContext(), AnnotationBasedExtension {

    override fun getAnnotationFqNames(modifierListOwner: KtModifierListOwner?): List<String> =
        annotations

    val intClass = context.symbols.int
    val intType = intClass.defaultType

    val funWhatever = context
        .referenceFunctions(FqName.ROOT.child(Name.identifier("whatever")))
        .single {
            val parameters = it.owner.valueParameters
            parameters.size == 1 && parameters[0].type == intType
        }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (! isDeclarative(declaration)) return super.visitFunctionNew(declaration)

        declaration.addValueParameter("callSiteOffset", context.irBuiltIns.intType)

        declaration.body = irDeclarative(declaration)

        return super.visitFunctionNew(declaration)
    }

    private fun irDeclarative(
        function: IrFunction,
    ): IrBlockBody {
        return DeclarationIrBuilder(context, function.symbol).irBlockBody {
            + irDeclarativeEnter(function)
            for (statement in function.body !!.statements) + statement
        }
    }

    private fun IrBuilderWithScope.irDeclarativeEnter(
        function: IrFunction
    ): IrCall {
        return irCall(funWhatever).also { call ->
            call.putValueArgument(0, irGet(function.valueParameters.last()))
        }
    }

    override fun visitCall(expression: IrCall): IrExpression {
        if (! isDeclarative(expression)) return super.visitCall(expression)

        return IrCallImpl(
            expression.startOffset,
            expression.endOffset,
            expression.type,
            expression.symbol,
            expression.typeArgumentsCount,
            expression.valueArgumentsCount + 1,
            expression.origin,
            expression.superQualifierSymbol
        ).also {
            it.copyTypeAndValueArgumentsFrom(expression)
            it.putValueArgument(
                index = expression.valueArgumentsCount,
                expression.startOffset.toIrConst(intType)
            )
        }
    }

    private fun isDeclarative(declaration: IrClass): Boolean =
        declaration.kind == ClassKind.CLASS &&
            declaration.isAnnotatedWithDeclarative()

    private fun isDeclarative(declaration: IrFunction): Boolean =
        declaration.isAnnotatedWithDeclarative()

    private fun isDeclarative(expression: IrCall): Boolean =
        expression.symbol.owner.isAnnotatedWithDeclarative()

    private fun isDeclarative(declaration: IrSimpleFunctionSymbol): Boolean =
        declaration.owner.isAnnotatedWithDeclarative()

    private fun IrClass.isAnnotatedWithDeclarative(): Boolean =
        toIrBasedDescriptor().hasSpecialAnnotation(null)

    private fun IrFunction.isAnnotatedWithDeclarative(): Boolean =
        toIrBasedDescriptor().hasSpecialAnnotation(null)
}
