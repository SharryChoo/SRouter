package com.sharry.srouter.compiler;

import com.google.auto.service.AutoService;
import com.sharry.srouter.annotation.PriorityRange;
import com.sharry.srouter.annotation.RouteInterceptor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Generate SRouter$$Interfaces$$xxx.java file.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/17 16:28
 */
@AutoService(Processor.class)
public class RouteInterceptorCompiler extends BaseCompiler {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // Find module name.
        String moduleName = findModuleName();
        // Validate elements
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(RouteInterceptor.class);
        if (null == elements || elements.isEmpty()) {
            return false;
        }
        logger.i("elements size is " + elements.size());
        /*
          ```Map<String, RouteInterceptorMeta>```
         */
        ParameterizedTypeName inputMapTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_INTERCEPTOR_META)
        );
        /*
           Build input param name.
        */
        ParameterSpec rootParamSpec = ParameterSpec.builder(
                inputMapTypeName,
                Constants.METHOD_LOAD_INTO_PARAMETER_NAME_INTERCEPTION_CACHES
        ).build();
        /*
          Build method : 'loadInto'
        */
        MethodSpec.Builder loadIntoMethodSpec = MethodSpec.methodBuilder(Constants.METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(rootParamSpec);

        // parse elements to loadInto method.
        completionLoadInto(elements, loadIntoMethodSpec);
        /*
          Build class : SRouter$$Route$$xxx
          public class SRouter$$Route$$module_name implements IRouter
        */
        ClassName superClassName = ClassName.get(Constants.PACKAGE_NAME_TEMPLATE,
                Constants.SIMPLE_NAME_IROUTE_INTERCEPTOR);
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(
                Constants.SIMPLE_NAME_PREFIX_OF_INTERCEPTOR + moduleName)
                .addModifiers(Modifier.FINAL, PUBLIC)
                .addSuperinterface(superClassName)
                .addMethod(loadIntoMethodSpec.build());
        // Perform generate java file
        try {
            JavaFile.builder(Constants.PACKAGE_NAME_OF_GENERATE_FILE, classBuilder.build())
                    .addFileComment("SRouter-Compiler auto generate.")
                    .indent("    ")
                    .build()
                    .writeTo(filer);
            logger.i("Generated root, name is " + Constants.SIMPLE_NAME_PREFIX_OF_INTERCEPTOR + moduleName);
        } catch (IOException e) {
            logger.e(e);
        }
        return false;
    }

    /**
     * Find bind module whether or not declaration moduleName at build.gradle.
     */
    private String findModuleName() {
        String moduleName = null;
        Map<String, String> options = processingEnv.getOptions();
        if (options != null && !options.isEmpty()) {
            moduleName = options.get(Constants.KEY_MODULE_NAME);
        }
        if (null != moduleName && moduleName.length() > 0) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
            logger.i("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            logger.e("These no module name, at 'build.gradle', like :\n" +
                    "apt {\n" +
                    "    arguments {\n" +
                    "        moduleName project.getName();\n" +
                    "    }\n" +
                    "}\n");
            throw new RuntimeException("SRouter::Compiler >>> No module name, for more information, look at gradle log.");
        }
        return moduleName;
    }

    /**
     * Parse element info then inject to method.
     * <pre>
     *         caches.put(
     *                 "PermissionInterceptor",
     *                 RouteInterceptorMeta.create(
     *                         PermissionInterceptor.class,
     *                         10
     *                 )
     *         );
     * </pre>
     *
     * @param elements Elements is mark at @Route class.
     * @param loadInto Need generate method.
     */
    private void completionLoadInto(Set<? extends Element> elements, MethodSpec.Builder loadInto) {
        for (Element element : elements) {
            RouteInterceptor annotation = element.getAnnotation(RouteInterceptor.class);
            // Verify priority
            int validPriority;
            if (annotation.priority() < PriorityRange.MINIMUM.value()) {
                validPriority = PriorityRange.MINIMUM.value();
            } else if (annotation.priority() > PriorityRange.MAXIMUM.value()) {
                validPriority = PriorityRange.MAXIMUM.value();
            } else {
                validPriority = annotation.priority();
            }
            // Write code into method loadInto.
            writeToMethodLoadInto(loadInto, annotation.value(), validPriority, element);
        }
    }

    private void writeToMethodLoadInto(MethodSpec.Builder loadInto, String value, int priority, Element element) {
        loadInto.addComment("------------------ @RouteInterceptor(value = \"" + value + "\") ------------------");
        TypeMirror tm = element.asType();
        if (types.isSubtype(tm, typeInterceptor)) {
            loadInto.addCode(
                    Constants.METHOD_LOAD_INTO_PARAMETER_NAME_INTERCEPTION_CACHES + ".put(" + "\n" +
                            "      $S, " + "\n" +
                            "      $T.create(" + "\n" +
                            "          $T.class," + "\n" +
                            "          " + priority + "\n" +
                            "      )" + "\n" +
                            ");" + "\n",
                    value,
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_INTERCEPTOR_META),
                    element
            );
        } else {
            throw new IllegalArgumentException("Please ensure @RouteInterceptor marked class is sub class for "
                    + Constants.CLASS_NAME_IINTERCEPTOR);
        }
    }

}
