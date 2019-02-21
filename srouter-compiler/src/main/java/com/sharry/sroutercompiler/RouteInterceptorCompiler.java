package com.sharry.sroutercompiler;

import com.google.auto.service.AutoService;
import com.sharry.srouterannotation.RouteInterceptor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Generate SRouter$$Routes$$xxx.java file.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/17 16:28
 */
@AutoService(Processor.class)
public class RouteInterceptorCompiler extends AbstractProcessor {

    private Filer mFiler;
    private Logger mLogger;
    private Types mTypeUtils;
    private TypeMirror mTypeInterceptor;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mLogger = new Logger(processingEnv.getMessager());
        mTypeUtils = processingEnv.getTypeUtils();
        mTypeInterceptor = processingEnv.getElementUtils().getTypeElement(Constants.CLASS_NAME_IINTERCEPTOR).asType();
        mLogger.i(">>>>>>>>>>>>>>>>>>>>> init <<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(RouteInterceptor.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // Find module name.
        String moduleName = findModuleName();
        // Validate elements
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(RouteInterceptor.class);
        if (null == elements || elements.isEmpty()) {
            return false;
        }
        mLogger.i("elements size is " + elements.size());
        /*
          ```Map<String, SIMPLE_NAME_ROUTE_INTERCEPTOR_META>```
         */
        ParameterizedTypeName inputMapTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_INTERCEPTOR_META)
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
        parseElements(elements, moduleName, loadIntoMethodSpec);
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
                    .build()
                    .writeTo(mFiler);
            mLogger.i("Generated root, name is " + Constants.SIMPLE_NAME_PREFIX_OF_INTERCEPTOR + moduleName);
        } catch (IOException e) {
            mLogger.e(e);
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
        if (!TextUtils.isEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
            mLogger.i("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            mLogger.e("These no module name, at 'build.gradle', like :\n" +
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
     *
     * @param elements   Elements is mark at @Route class.
     * @param moduleName Component module name.
     * @param loadInto   Need generate method.
     */
    private void parseElements(Set<? extends Element> elements, String moduleName, MethodSpec.Builder loadInto) {
        List<String> authorities = new ArrayList<>();
        for (Element element : elements) {
            RouteInterceptor annotation = element.getAnnotation(RouteInterceptor.class);
            // Get path
            String path = annotation.path();
            if (!path.startsWith(moduleName)) {
                throw new IllegalArgumentException("Found error @RouteInterceptor(path = \"" + path + "\" ) at "
                        + element + ".class, @RouteInterceptor path must start with : " + moduleName);
            }
            if (authorities.contains(path)) {
                throw new IllegalArgumentException("Found duplicate path \"" + path +
                        "\", Unsupported define duplicate path.");
            }
            authorities.add(path);
            // Write code into method loadInto.
            writeToMethodLoadInto(element, loadInto, path, annotation.priority());
        }
    }

    /**
     * Write code to method loadInto.
     *
     * <pre>
     *         interceptorCaches.put(
     *                 "component2/PermissionInterceptor",
     *                 RouteInterceptorMeta.create(
     *                         PermissionInterceptor.class,
     *                         10
     *                 )
     *         );
     * </pre>
     */
    private void writeToMethodLoadInto(Element element, MethodSpec.Builder loadInto, String path, int priority) {
        TypeMirror tm = element.asType();
        if (mTypeUtils.isSubtype(tm, mTypeInterceptor)) {
            loadInto.addStatement(
                    getLoadIntoMethodCode(priority),
                    path,
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_INTERCEPTOR_META),
                    element
            );
        } else {
            throw new IllegalArgumentException("Please ensure @RouteInterceptor marked class is sub class for "
                    + Constants.CLASS_NAME_IINTERCEPTOR);
        }
    }

    /**
     * Build loadInto method code.
     */
    private String getLoadIntoMethodCode(int priority) {
        return Constants.METHOD_LOAD_INTO_PARAMETER_NAME_INTERCEPTION_CACHES + ".put(" + "\n" +
                "      $S, " + "\n" +
                "      $T.create(" + "\n" +
                "          $T.class," + "\n" +
                "          " + priority + "\n" +
                "      )" + "\n" +
                ")";
    }

}
