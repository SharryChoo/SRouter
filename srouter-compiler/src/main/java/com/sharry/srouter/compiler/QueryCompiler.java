package com.sharry.srouter.compiler;

import com.google.auto.service.AutoService;
import com.sharry.srouter.annotation.Query;
import com.sharry.srouter.annotation.QueryType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Generate xxx$$QueryBinding.java file.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/17 16:28
 */
@AutoService(Processor.class)
public class QueryCompiler extends BaseCompiler {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // Find classes include @Query.
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Query.class);
        Map<Element, List<Element>> elementsMap = new LinkedHashMap<>();
        for (Element fieldElement : elements) {
            try {
                // Fetch element attached class.
                Element classElement = fieldElement.getEnclosingElement();
                // Verify field is private?
                if (fieldElement.getModifiers().contains(Modifier.PRIVATE)) {
                    throw new IllegalAccessException("The inject fields CAN NOT BE 'private'!!! please check field ["
                            + fieldElement.getSimpleName() + "] in class [" + classElement.getSimpleName() + "]");
                }
                // Add field mapper element to class Set.
                List<Element> fieldElements = elementsMap.get(classElement);
                if (fieldElements == null) {
                    fieldElements = new ArrayList<>();
                    elementsMap.put(classElement, fieldElements);
                }
                fieldElements.add(fieldElement);
            } catch (IllegalAccessException e) {
                logger.e(e.getMessage());
            }
        }
        // Foreach every class that include @Query, generate XXX$QueryBinding
        for (Map.Entry<Element, List<Element>> entry : elementsMap.entrySet()) {
            Element classElement = entry.getKey();
            List<Element> fieldElements = entry.getValue();
            generateClass(classElement, fieldElements);
        }
        return false;
    }

    private void generateClass(Element classElement, List<Element> fieldElements) {
        // Verify class type.
        final String originClassNameStr = classElement.getSimpleName().toString();
        // Guess class name.
        ClassName originClassName = ClassName.bestGuess(originClassNameStr);
        boolean isActivity;
        if (types.isSubtype(classElement.asType(), typeActivity)) {  // Activity, then use getIntent()
            isActivity = true;
        } else if (types.isSubtype(classElement.asType(), typeFragment)
                || types.isSubtype(classElement.asType(), typeFragmentV4)
                || types.isSameType(classElement.asType(), typeFragmentX)) {   // Fragment, then use getArguments()
            isActivity = false;
        } else {
            throw new IllegalStateException("The field [" + originClassNameStr + "] need inject " +
                    "from intent, its parent must be activity or fragment!");
        }
        // 1. Create class like XXXX$$QueryBinding.java
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(originClassNameStr +
                Constants.SIMPLE_NAME_SUFFIX_OF_QUERY_BINDING)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC);
        // 2. Create constructor.
        MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(originClassName, "substitute");
        // 4. For each element.
        for (Element fieldElement : fieldElements) {
            // add code.
            addCode(constructorMethodBuilder, fieldElement, isActivity);
        }
        // 5. Add constructor.
        classBuilder.addMethod(constructorMethodBuilder.build());
        // 6. Generate java class.
        try {
            String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
            JavaFile.builder(packageName, classBuilder.build())
                    .addFileComment("SRouter-Compiler auto generate")
                    .indent("    ")
                    .build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCode(MethodSpec.Builder methodBuilder, Element fieldElement, boolean isActivity) {
        String originalValue = "substitute." + fieldElement.getSimpleName().toString();
        String key = fieldElement.getAnnotation(Query.class).key();
        // Add comment.
        methodBuilder.addComment("------------------ @Query(key = \"" + key + "\") ------------------");
        String statement = originalValue + " = ";
        statement += buildCastCode(fieldElement);
        statement += isActivity ? "substitute.getIntent()." : "substitute.getArgument().";
        statement = buildStatement(originalValue, statement, typeUtils.typeExchange(fieldElement), isActivity);
        methodBuilder.addStatement(statement, key);
    }


    private String buildCastCode(Element element) {
        if (typeUtils.typeExchange(element) == QueryType.SERIALIZABLE.ordinal()) {
            return CodeBlock.builder().add("($T) ", ClassName.get(element.asType())).build().toString();
        }
        return "";
    }

    private String buildStatement(String originalValue, String statement, int type, boolean isActivity) {
        switch (QueryType.values()[type]) {
            case BOOLEAN:
                statement += (isActivity ? ("getBooleanExtra($S, " + originalValue + ")") : ("getBoolean($S)"));
                break;
            case BYTE:
                statement += (isActivity ? ("getByteExtra($S, " + originalValue + ")") : ("getByte($S)"));
                break;
            case SHORT:
                statement += (isActivity ? ("getShortExtra($S, " + originalValue + ")") : ("getShort($S)"));
                break;
            case INT:
                statement += (isActivity ? ("getIntExtra($S, " + originalValue + ")") : ("getInt($S)"));
                break;
            case LONG:
                statement += (isActivity ? ("getLongExtra($S, " + originalValue + ")") : ("getLong($S)"));
                break;
            case CHAR:
                statement += (isActivity ? ("getCharExtra($S, " + originalValue + ")") : ("getChar($S)"));
                break;
            case FLOAT:
                statement += (isActivity ? ("getFloatExtra($S, " + originalValue + ")") : ("getFloat($S)"));
                break;
            case DOUBLE:
                statement += (isActivity ? ("getDoubleExtra($S, " + originalValue + ")") : ("getDouble($S)"));
                break;
            case STRING:
                statement += (isActivity ? ("getExtras() == null ? " + originalValue + " : \r\n" +
                        "substitute.getIntent().getExtras().getString($S, " + originalValue + ")") : ("getString($S)"));
                break;
            case SERIALIZABLE:
                statement += (isActivity ? ("getSerializableExtra($S)") : ("getSerializable($S)"));
                break;
            case PARCELABLE:
                statement += (isActivity ? ("getParcelableExtra($S)") : ("getParcelable($S)"));
                break;
            default:
                break;
        }

        return statement;
    }

}
