package zakadabar.kotlin.compiler.plugin.lower

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.extensions.AnnotationBasedExtension
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.interpreter.toIrConst
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtModifierListOwner

class DeclarativeIrVisitor(
    private val context: IrPluginContext,
    private val annotations: List<String>
) : AnnotationBasedExtension, IrElementVisitorVoid {

    override fun getAnnotationFqNames(modifierListOwner: KtModifierListOwner?): List<String> =
        annotations

    override fun visitElement(element: IrElement) {
        element.acceptChildren(this, null)
    }

    override fun visitFunction(declaration: IrFunction) {
        super.visitFunction(declaration)
        if (! isDeclarative(declaration)) return

        println("===============   declarative function: ${declaration.name.identifier}")
        println(declaration.dump())
    }

    override fun visitCall(expression: IrCall) {
        super.visitCall(expression)
        if (! isDeclarative(expression.symbol)) return

        println("===============   declarative call: ${expression.symbol.owner.name}")
        println(expression.dump())
    }

    override fun visitClass(declaration: IrClass) {
        super.visitClass(declaration)
        if (! isDeclarative(declaration)) return
    }

    private fun isDeclarative(declaration: IrClass): Boolean =
        declaration.kind == ClassKind.CLASS &&
            declaration.isAnnotatedWithDeclarative()

    private fun isDeclarative(declaration: IrFunction): Boolean =
        declaration.isAnnotatedWithDeclarative()

    private fun isDeclarative(declaration: IrSimpleFunctionSymbol): Boolean =
        declaration.owner.isAnnotatedWithDeclarative()

    private fun IrClass.isAnnotatedWithDeclarative(): Boolean =
        toIrBasedDescriptor().hasSpecialAnnotation(null)

    private fun IrFunction.isAnnotatedWithDeclarative(): Boolean =
        toIrBasedDescriptor().hasSpecialAnnotation(null)

}
