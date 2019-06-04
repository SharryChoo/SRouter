package com.sharry.srouter.compiler;


import com.sharry.srouter.annotation.compiler.QueryType;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.sharry.srouter.compiler.Constants.CLASS_NAME_PARCELABLE;
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
        parcelableType = elements.getTypeElement(CLASS_NAME_PARCELABLE).asType();
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
                return QueryType.BYTE.ordinal();
            case Constants.SHORT:
                return QueryType.SHORT.ordinal();
            case Constants.INTEGER:
                return QueryType.INT.ordinal();
            case Constants.LONG:
                return QueryType.LONG.ordinal();
            case Constants.FLOAT:
                return QueryType.FLOAT.ordinal();
            case Constants.DOUBLE:
                return QueryType.DOUBLE.ordinal();
            case Constants.BOOLEAN:
                return QueryType.BOOLEAN.ordinal();
            case Constants.CHAR:
                return QueryType.CHAR.ordinal();
            case Constants.STRING:
                return QueryType.STRING.ordinal();
            default:
                // Other side, maybe the CLASS_NAME_PARCELABLE or SERIALIZABLE or OBJECT.
                if (types.isSubtype(typeMirror, parcelableType)) {
                    // CLASS_NAME_PARCELABLE
                    return QueryType.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    // SERIALIZABLE
                    return QueryType.SERIALIZABLE.ordinal();
                } else {
                    return -1;
                }
        }
    }
}
