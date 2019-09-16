package com.sharry.srouter.compiler;

import com.google.auto.service.AutoService;
import com.sharry.srouter.annotation.compiler.Query;
import com.sharry.srouter.annotation.compiler.QueryType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
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
 * Process annotation {@link Query}
 * <p>
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

        // 1. Create method.
        /*
           public void bind(FoundActivity target) {
               ......
           }
        */
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.METHOD_BIND)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(originClassName, Constants.METHOD_BIND_PARAMETER_NAME_TARGET);

        // 2. Completion method.
         /*
            Bundle data = target.getArguments();
         */
        ClassName bundleClassName = ClassName.bestGuess(Constants.CLASS_NAME_BUNDLE);
        if (isActivity) {
            methodBuilder.addStatement("$T data = " + Constants.METHOD_BIND_PARAMETER_NAME_TARGET
                    + ".getIntent().getExtras()", bundleClassName);
        } else {
            methodBuilder.addStatement("$T data = " + Constants.METHOD_BIND_PARAMETER_NAME_TARGET
                    + ".getArguments()", bundleClassName);
        }
        /*
           Bundle urlDatum = data.getBundle(Constants.INTENT_EXTRA_URL_DATUM)
         */
        methodBuilder.addStatement("$T urlDatum = data.getBundle($T.INTENT_EXTRA_URL_DATUM)",
                bundleClassName, ClassName.bestGuess(Constants.CLASS_NAME_OF_SROUTER_CONSTANTS));
        // For each element.
        for (Element fieldElement : fieldElements) {
            addCode(methodBuilder, fieldElement);
        }

        // 3. Create class like XXXX$$QueryBinding.java
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(originClassNameStr +
                Constants.SIMPLE_NAME_SUFFIX_OF_QUERY_BINDING)
                .addSuperinterface(
                        // Add implements IQueryBinding<XXXX>
                        ParameterizedTypeName.get(ClassName.get(Constants.PACKAGE_NAME_SROUTER,
                                Constants.SIMPLE_NAME_IQUERY_BINDING), originClassName)
                )
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addMethod(methodBuilder.build());

        // 4. Generate java class.
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

    /**
     * <pre>
     *     public void bind(FoundFragment target) {
     *         ......
     *         // ------------------ @Query(key = "title") ------------------
     *         if (data.containsKey("title")) {
     *             target.title =  (String) data.getString("title");
     *         } else if (urlDatum != null && urlDatum.containsKey("title")) {
     *             target.title = (String) urlDatum.getString("title");
     *         } else {
     *             // do nothing
     *         }
     *         ......
     *     }
     * </pre>
     */
    private void addCode(MethodSpec.Builder methodBuilder, Element fieldElement) {
        /*
          if (data.containsKey("title")) {
              target.title =  (String) data.getString("title");
          } else if (urlDatum != null && urlDatum.containsKey("title")) {
              target.title =  (String) String.parse urlDatum.getString("title");
          } else {
              // do nothing
          }
         */
        String originalValue = "target." + fieldElement.getSimpleName().toString();
        String key = fieldElement.getAnnotation(Query.class).key();
        methodBuilder.addComment("------------------------------------ @Query(key = \"" + key + "\")------------------------------------");
        methodBuilder.addCode(
                "if (data.containsKey($S)) {  \n"
                        + buildDataFetchCode(key, originalValue, fieldElement, typeUtils.typeExchange(fieldElement)) + ";\n"
                        + "} else if (urlDatum != null && urlDatum.containsKey($S)) {\n"
                        + buildURLFetchCode(key, originalValue, fieldElement, typeUtils.typeExchange(fieldElement)) + ";\n"
                        + "} else {\n"
                        + "    // do nothing...... \n"
                        + "}\n",
                key,
                key
        );

    }

    private String buildDataFetchCode(String key, String originalValue, Element fieldElement, int type) {
        String fetchDataCode = "    target." + fieldElement.getSimpleName().toString() + " = "
                + CodeBlock.builder().add("($T) ", ClassName.get(fieldElement.asType())).build().toString();
        switch (QueryType.values()[type]) {
            case BOOLEAN:
                fetchDataCode += "data.getBoolean($S, " + originalValue + ")";
                break;
            case BYTE:
                fetchDataCode += "data.getByte($S, " + originalValue + ")";
                break;
            case SHORT:
                fetchDataCode += "data.getShort($S, " + originalValue + ")";
                break;
            case INT:
                fetchDataCode += "data.getInt($S, " + originalValue + ")";
                break;
            case LONG:
                fetchDataCode += "data.getLong($S, " + originalValue + ")";
                break;
            case CHAR:
                fetchDataCode += "data.getChar($S, " + originalValue + ")";
                break;
            case FLOAT:
                fetchDataCode += "data.getFloat($S, " + originalValue + ")";
                break;
            case DOUBLE:
                fetchDataCode += "data.getDouble($S, " + originalValue + ")";
                break;
            case STRING:
                fetchDataCode += "data.getString($S, " + originalValue + ")";
                break;
            case SERIALIZABLE:
                fetchDataCode += "data.getSerializable($S)";
                break;
            case PARCELABLE:
                fetchDataCode += "data.getParcelable($S)";
                break;
            default:
                return CodeBlock.builder()
                        .add("    throw new $T($S)",
                                UnsupportedOperationException.class,
                                "cannot support this convert that type is " + ClassName.get(fieldElement.asType()))
                        .build().toString();
        }
        return CodeBlock.builder().add(fetchDataCode, key).build().toString();
    }

    private String buildURLFetchCode(String key, String originalValue, Element fieldElement, int type) {
        String fetchDataCode = "    target." + fieldElement.getSimpleName().toString() + " = "
                + CodeBlock.builder().add("($T) ", ClassName.get(fieldElement.asType())).build().toString();
        switch (QueryType.values()[type]) {
            case BOOLEAN:
                fetchDataCode += CodeBlock.builder()
                        .add("$T.parseBoolean(urlDatum.getString($S))", Boolean.class, key)
                        .build().toString();
                break;
            case BYTE:
                fetchDataCode += CodeBlock.builder()
                        .add("$T.parseByte(urlDatum.getString($S))", Byte.class, key)
                        .build().toString();
                break;
            case SHORT:
                fetchDataCode += CodeBlock.builder()
                        .add("$T.parseShort(urlDatum.getString($S))", Short.class, key)
                        .build().toString();
                break;
            case INT:
                fetchDataCode += CodeBlock.builder()
                        .add("$T.parseInt(urlDatum.getString($S))", Integer.class, key)
                        .build().toString();
                break;
            case LONG:
                fetchDataCode += CodeBlock.builder()
                        .add("$T.parseInt(urlDatum.getString($S))", Integer.class, key)
                        .build().toString();
                break;
            case FLOAT:
                fetchDataCode += CodeBlock.builder()
                        .add("$T.parseFloat(urlDatum.getString($S))", Float.class, key)
                        .build().toString();
                break;
            case DOUBLE:
                fetchDataCode += CodeBlock.builder()
                        .add("$T.parseDouble(urlDatum.getString($S))", Double.class, key)
                        .build().toString();
                break;
            case STRING:
                fetchDataCode += CodeBlock.builder()
                        .add("urlDatum.getString($S, " + originalValue + ")", key)
                        .build().toString();
                break;
            default:
                return CodeBlock.builder()
                        .add("    throw new $T($S)",
                                UnsupportedOperationException.class,
                                "cannot support this convert that type is " + ClassName.get(fieldElement.asType()))
                        .build().toString();
        }
        return fetchDataCode;
    }

}
