package com.sharry.srouter.compiler;


import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.sharry.srouter.compiler.Constants.PARCELABLE;
import static com.sharry.srouter.compiler.Constants.SERIALIZABLE;

/**
 * Utils for type exchange
 * <p>
 * Thanks for allibaba.
 *
 * @author zhilong <a href="mailto:zhilong.lzl@alibaba-inc.com">Contact me.</a>
 * @version 1.0
 * @since 2017/2/21 下午1:06
 */
class TypeUtils {

    private Types types;
    private TypeMirror parcelableType;
    private TypeMirror serializableType;

    TypeUtils(Types types, Elements elements) {
        this.types = types;
        parcelableType = elements.getTypeElement(PARCELABLE).asType();
        serializableType = elements.getTypeElement(SERIALIZABLE).asType();
    }

    /**
     * Diagnostics out the true java type
     *
     * @param element Raw type
     * @return Type class of java
     */
    int typeExchange(Element element) {
        TypeMirror typeMirror = element.asType();

        // Primitive
        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }

        switch (typeMirror.toString()) {
            case Constants.BYTE:
                return TypeKind.BYTE.ordinal();
            case Constants.SHORT:
                return TypeKind.SHORT.ordinal();
            case Constants.INTEGER:
                return TypeKind.INT.ordinal();
            case Constants.LONG:
                return TypeKind.LONG.ordinal();
            case Constants.FLOAT:
                return TypeKind.FLOAT.ordinal();
            case Constants.DOUBEL:
                return TypeKind.DOUBLE.ordinal();
            case Constants.BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case Constants.CHAR:
                return TypeKind.CHAR.ordinal();
            case Constants.STRING:
                return TypeKind.STRING.ordinal();
            default:
                // Other side, maybe the PARCELABLE or SERIALIZABLE or OBJECT.
                if (types.isSubtype(typeMirror, parcelableType)) {
                    // PARCELABLE
                    return TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    // SERIALIZABLE
                    return TypeKind.SERIALIZABLE.ordinal();
                } else {
                    return -1;
                }
        }
    }
}
