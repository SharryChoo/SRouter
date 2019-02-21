package com.sharry.sroutercompiler;

import com.google.auto.service.AutoService;
import com.sharry.srouterannotation.Route;
import com.sharry.srouterannotation.ThreadMode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.sharry.sroutercompiler.Constants.CLASS_NAME_ACTIVITY;
import static com.sharry.sroutercompiler.Constants.CLASS_NAME_FRAGMENT;
import static com.sharry.sroutercompiler.Constants.CLASS_NAME_SERVICE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Generate SRouter$$Routes$$xxx.java file.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/17 16:28
 */
@AutoService(Processor.class)
public class RouteCompiler extends AbstractProcessor {

    private Filer mFiler;                // File write util
    private Logger mLogger;
    private Types mTypeUtils;
    private Elements mElementUtils;
    // Special super class type.
    private TypeMirror mTypeActivity;
    private TypeMirror mTypeService;
    private TypeMirror mTypeFragment;
    private TypeMirror mTypeFragmentV4;
    private TypeMirror mTypeProvider;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mLogger = new Logger(processingEnv.getMessager());
        mTypeUtils = processingEnv.getTypeUtils();            // Get type utils.
        mElementUtils = processingEnv.getElementUtils();      // Get class meta.
        // Find base type.
        findDeclaredSpecialType();
        mLogger.i(">>>>>>>>>>>>>>>>>>>>> init <<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Route.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // Find module name.
        String moduleName = findModuleName();

        // Validate elements
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (null == elements || elements.isEmpty()) {
            return false;
        }
        mLogger.i("elements size is " + elements.size());
        /*
          ```Map<String, RouteMeta>```
         */
        ParameterizedTypeName inputMapTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META)
        );
        /*
           Build input param name.
        */
        ParameterSpec rootParamSpec = ParameterSpec.builder(
                inputMapTypeName,
                Constants.METHOD_LOAD_INTO_PARAMETER_NAME_ROUTE_CACHES
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
        ClassName superClassName = ClassName.get(Constants.PACKAGE_NAME_TEMPLATE, Constants.SIMPLE_NAME_IROUTE);
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(Constants.SIMPLE_NAME_PREFIX_OF_ROUTERS + moduleName)
                .addModifiers(Modifier.FINAL, PUBLIC)
                .addSuperinterface(superClassName)
                .addMethod(loadIntoMethodSpec.build());
        // Perform generate java file
        try {
            JavaFile.builder(Constants.PACKAGE_NAME_OF_GENERATE_FILE, classBuilder.build())
                    .addFileComment("SRouter-Compiler auto generate.")
                    .build()
                    .writeTo(mFiler);
            mLogger.i("Generated root, name is " + Constants.SIMPLE_NAME_PREFIX_OF_ROUTERS + moduleName);
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
     * Find declared special type.
     */
    private void findDeclaredSpecialType() {
        mTypeActivity = mElementUtils.getTypeElement(CLASS_NAME_ACTIVITY).asType();
        mTypeService = mElementUtils.getTypeElement(CLASS_NAME_SERVICE).asType();
        mTypeFragment = mElementUtils.getTypeElement(CLASS_NAME_FRAGMENT).asType();
        mTypeFragmentV4 = mElementUtils.getTypeElement(Constants.CLASS_NAME_FRAGMENT_V4).asType();
        mTypeProvider = mElementUtils.getTypeElement(Constants.CLASS_NAME_IPROVIDER).asType();
    }

    /**
     * Parse element info then inject to method.
     *
     * @param elements   Elements is mark at @Route class.
     * @param moduleName Component module name.
     * @param loadInto   Need generate method.
     */
    private void parseElements(Set<? extends Element> elements, String moduleName, MethodSpec.Builder loadInto) {
        List<String> paths = new ArrayList<>();
        for (Element element : elements) {
            Route routeAnnotation = element.getAnnotation(Route.class);
            // Setup routeAuthority.
            String routePath = routeAnnotation.path();
            if (!routePath.startsWith(moduleName)) {
                throw new IllegalArgumentException("Found error @Route(route path = \"" + routePath + "\" ) at "
                        + element + ".class, @Route route path must start with : " + moduleName);
            }
            if (paths.contains(routePath)) {
                throw new IllegalArgumentException("Found duplicate route path \"" + routePath +
                        "\", Unsupported define duplicate routeAuthority.");
            }
            paths.add(routePath);
            // Setup ThreadMode.
            ThreadMode threadMode = routeAnnotation.mode();
            // Write code into method loadInto.
            writeToMethodLoadInto(loadInto, routePath, threadMode, Arrays.asList(
                    routeAnnotation.interceptorPaths()), element);
        }
    }

    /**
     * Write code to method loadInto.
     *
     * <pre>
     *         routeCaches.put(
     *                 "component1/Component1Activity",
     *                 RouteMeta.create(
     *                         RouteMeta.Type.CLASS_NAME_ACTIVITY,
     *                         ThreadMode.MAIN,
     *                         Component1Activity.class,
     *                         new String[]{..., ...}
     *                 )
     *         );
     *         ......
     * </pre>
     */
    private void writeToMethodLoadInto(MethodSpec.Builder loadInto, String routeAuthority,
                                       ThreadMode threadMode, List<String> interceptorPaths, Element element) {
        TypeMirror tm = element.asType();
        // @Route bind class is child for Activity.
        if (mTypeUtils.isSubtype(tm, mTypeActivity)) {
            mLogger.i("Found activity route: " + tm.toString() + " <<<");
            loadInto.addStatement(
                    getLoadIntoMethodCode("ACTIVITY", threadMode, interceptorPaths),
                    routeAuthority,
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(ThreadMode.class),
                    element
            );
        } else if (mTypeUtils.isSubtype(tm, mTypeService)) {
            // @Route bind class is child for Service.
            mLogger.i("Found service route: " + tm.toString() + " <<<");
            loadInto.addStatement(
                    getLoadIntoMethodCode("CLASS_NAME_SERVICE", threadMode, interceptorPaths),
                    routeAuthority,
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(ThreadMode.class),
                    element
            );
        } else if (mTypeUtils.isSubtype(tm, mTypeFragment)) {
            // @Route bind class is child for Fragment.
            mLogger.i("Found fragment route: " + tm.toString() + " <<<");
            loadInto.addStatement(
                    getLoadIntoMethodCode("FRAGMENT", threadMode, interceptorPaths),
                    routeAuthority,
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(ThreadMode.class),
                    element
            );
        } else if (mTypeUtils.isSubtype(tm, mTypeFragmentV4)) {
            // @Route bind class is child for Fragment.
            mLogger.i("Found fragment v4 route: " + tm.toString() + " <<<");
            loadInto.addStatement(
                    getLoadIntoMethodCode("FRAGMENT_V4", threadMode, interceptorPaths),
                    routeAuthority,
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(ThreadMode.class),
                    element
            );
        } else if (mTypeUtils.isSubtype(tm, mTypeProvider)) {
            // @Route bind class is child for IProvider.
            mLogger.i("Found provider route: " + tm.toString() + " <<<");
            loadInto.addStatement(
                    getLoadIntoMethodCode("IPROVIDER", threadMode, interceptorPaths),
                    routeAuthority,
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(ThreadMode.class),
                    element
            );
        } else {
            mLogger.i("Found other route: " + tm.toString() + " <<<");
            // @Route bind class is child for others.
            loadInto.addStatement(
                    getLoadIntoMethodCode("UNKNOWN", threadMode, interceptorPaths),
                    routeAuthority,
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                    ClassName.get(ThreadMode.class),
                    element
            );
        }
    }

    /**
     * Build loadInto method code.
     */
    private String getLoadIntoMethodCode(String routeType, ThreadMode threadMode, List<String> interceptorClassNames) {
        // append prefix
        StringBuilder builder = new StringBuilder(
                Constants.METHOD_LOAD_INTO_PARAMETER_NAME_ROUTE_CACHES + ".put(" + "\n" +
                        "      $S, " + "\n" +
                        "      $T.create(" + "\n" +
                        "          $T.Type." + routeType + "," + "\n" +
                        "          $T." + threadMode + ", " + "\n" +
                        "          $T.class," + "\n" +
                        "          new String[]{" + "\n"
        );
        // Add interceptorPaths
        int size = interceptorClassNames.size();
        for (int i = 0; i < size; i++) {
            builder.append(
                    "              \"" + interceptorClassNames.get(i) + "\""
            );
            if (i != size - 1) {
                builder.append(", ");
            }
            builder.append("\n");
        }
        // append suffix
        builder.append(
                "          }" + "\n" +
                        "      )" + "\n" +
                        ")"
        );
        return builder.toString();
    }

}
