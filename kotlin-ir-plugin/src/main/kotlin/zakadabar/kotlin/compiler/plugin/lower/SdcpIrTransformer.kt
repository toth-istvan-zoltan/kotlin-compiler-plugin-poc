package zakadabar.kotlin.compiler.plugin.lower

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.extensions.AnnotationBasedExtension
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtModifierListOwner

class SdcpIrTransformer(
    private val context: IrPluginContext,
    private val annotations: List<String>
) : AnnotationBasedExtension, IrElementVisitorVoid {

    override fun getAnnotationFqNames(modifierListOwner: KtModifierListOwner?): List<String> =
        annotations

    override fun visitElement(element: IrElement) {
        element.acceptChildren(this, null)
    }

    override fun visitClass(declaration: IrClass) {
        super.visitClass(declaration)

        if (!needsSdcpFunction(declaration)) return

        declaration.declarations.add(getOrGenerateSdcpFunction(declaration))
    }

    private val sdcpFunctions = mutableMapOf<IrClass, IrFunction>()

    private fun getOrGenerateSdcpFunction(klass: IrClass): IrFunction = sdcpFunctions.getOrPut(klass) {

        klass.companionObject()

        context.irFactory.buildFun {
            name = Name.identifier("sensibleDefaults")
            startOffset = SYNTHETIC_OFFSET
            endOffset = SYNTHETIC_OFFSET
            returnType = klass.defaultType
        }.also { func ->
            func.parent = klass
            func.body = context.irFactory.createBlockBody(
                func.startOffset, func.endOffset,
                listOfNotNull(
                    //       FUN name:sensibleDefaults visibility:public modality:FINAL <> () returnType:<root>.A
                    //       FUN name:withSensibleDefaults visibility:public modality:FINAL <> ($this:<root>.A.Companion) returnType:<root>.A
//                    IrDelegatingConstructorCallImpl(
//                        ctor.startOffset, ctor.endOffset, context.irBuiltIns.unitType,
//                        superConstructor.symbol, 0, superConstructor.valueParameters.size
//                    ),
//                    IrInstanceInitializerCallImpl(
//                        ctor.startOffset, ctor.endOffset, klass.symbol, context.irBuiltIns.unitType
//                    ).takeIf { invokeInitializers }
                )
            )
        }
    }

    private fun needsSdcpFunction(declaration: IrClass): Boolean =
        declaration.kind == ClassKind.CLASS &&
            declaration.isAnnotatedWithSdcp()

    private fun IrClass.isAnnotatedWithSdcp(): Boolean =
        toIrBasedDescriptor().hasSpecialAnnotation(null)

}
