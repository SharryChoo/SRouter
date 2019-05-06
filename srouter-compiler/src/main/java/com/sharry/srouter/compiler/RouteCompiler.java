package com.sharry.srouter.compiler;

import com.google.auto.service.AutoService;
import com.sharry.srouter.annotation.Route;
import com.sharry.srouter.annotation.ThreadMode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import static com.sharry.srouter.compiler.Constants.CLASS_NAME_ACTIVITY;
import static com.sharry.srouter.compiler.Constants.CLASS_NAME_FRAGMENT;
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
    private TypeMirror mTypeFragment;
    private TypeMirror mTypeFragmentV4;
    private TypeMirror mTypeService;

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
        ParameterizedTypeName inputInnerMapTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META)
        );
        /*
          ```Map<String, Map<String, RouteMeta>>```
         */
        ParameterizedTypeName inputMapTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                inputInnerMapTypeName
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
        completionLoadIntoMethod(elements, loadIntoMethodSpec);
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
                    .indent("    ")
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
        mTypeFragment = mElementUtils.getTypeElement(CLASS_NAME_FRAGMENT).asType();
        mTypeFragmentV4 = mElementUtils.getTypeElement(Constants.CLASS_NAME_FRAGMENT_V4).asType();
        mTypeService = mElementUtils.getTypeElement(Constants.CLASS_NAME_ISERVICE).asType();
    }

    /**
     * Write code to method loadInto.
     *
     * <pre>
     *         Map<String, RouteMeta> metas = null;
     *         ......
     *         metas = caches.get("moduleName");
     *         if (metas == null) {
     *             metas = new HashMap<>();
     *             cache.put("moduleName", metas);
     *         }
     *         metas.put(
     *                 "SampleActivity",
     *                 RouteMeta.create(
     *                         RouteMeta.Type.CLASS_NAME_ACTIVITY,
     *                         ThreadMode.MAIN,
     *                         Component1Activity.class,
     *                         new String[]{..., ...}
     *                 )
     *         );
     *         ......
     * </pre>
     *
     * @param elements Elements is mark at @Route class.
     * @param loadInto Need generate method.
     */
    private void completionLoadIntoMethod(Set<? extends Element> elements, MethodSpec.Builder loadInto) {
        /*
          Map<String, RouteMeta> metas = null;
         */
        loadInto.addStatement(
                "$T<$T, $T> metas = null",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META)
        );
        /*
          Write for each.
         */
        List<String> paths = new ArrayList<>();
        for (Element element : elements) {
            Route routeAnnotation = element.getAnnotation(Route.class);
            // Verify path
            String routePath = routeAnnotation.path();
            if (paths.contains(routePath)) {
                throw new IllegalArgumentException("Found duplicate route path \"" + routePath +
                        "\", Unsupported define duplicate routeAuthority.");
            }
            paths.add(routePath);
            // Setup ThreadMode.
            ThreadMode threadMode = routeAnnotation.mode();
            // Write code into method loadInto.
            writeToMethodLoadInto(
                    loadInto,
                    routeAnnotation.authority(),
                    routePath,
                    threadMode,
                    Arrays.asList(
                            routeAnnotation.interceptorURIs()
                    ),
                    element
            );
        }
    }

    private void writeToMethodLoadInto(MethodSpec.Builder loadInto, String authority, String path,
                                       ThreadMode threadMode, List<String> interceptorURIs, Element element) {
        TypeMirror tm = element.asType();
        if (mTypeUtils.isSubtype(tm, mTypeActivity)) {
            // @Route bind class is child for Activity.
            mLogger.i("Found activity route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "ACTIVITY", authority, path, threadMode, interceptorURIs, element);
        } else if (mTypeUtils.isSubtype(tm, mTypeFragment)) {
            // @Route bind class is child for Fragment.
            mLogger.i("Found fragment route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "FRAGMENT", authority, path, threadMode, interceptorURIs, element);
        } else if (mTypeUtils.isSubtype(tm, mTypeFragmentV4)) {
            // @Route bind class is child for Fragment.
            mLogger.i("Found fragment v4 route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "FRAGMENT_V4", authority, path, threadMode, interceptorURIs, element);
        } else if (mTypeUtils.isSubtype(tm, mTypeService)) {
            // @Route bind class is child for IService.
            mLogger.i("Found service route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "SERVICE", authority, path, threadMode, interceptorURIs, element);
        } else {
            // @Route bind class is child for others.
            mLogger.i("Found other route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "UNKNOWN", authority, path, threadMode, interceptorURIs, element);
        }
    }

    private void writeLoadInto(MethodSpec.Builder loadInto,
                               String routeType,
                               String authority,
                               String path,
                               ThreadMode threadMode,
                               List<String> interceptorURIs,
                               Element element) {
        loadInto.addComment("------------------ @Route(authority = \"" + authority + "\", " +
                "path = \"" + path + "\") ------------------");
        /*
          metas = caches.get("moduleName");
         */
        loadInto.addStatement(
                "metas = " + Constants.METHOD_LOAD_INTO_PARAMETER_NAME_ROUTE_CACHES + ".get($S)",
                authority
        );
        /*
          if (metas == null) {
             metas = new HashMap<>();
             cache.put("moduleName", metas);
          }
         */
        loadInto.addCode(
                "if (metas == null) {" + "\n"
                        + "    metas = new $T<>();" + "\n"
                        + "    " + Constants.METHOD_LOAD_INTO_PARAMETER_NAME_ROUTE_CACHES + ".put($S, metas);" + "\n"
                        + "}" + "\n",
                ClassName.get(HashMap.class),
                authority
        );
        /*
          metas.put(
                    "SampleActivity",
                    RouteMeta.create(
                            RouteMeta.Type.CLASS_NAME_ACTIVITY,
                            ThreadMode.MAIN,
                            Component1Activity.class,
                            new String[]{..., ...}
                    )
           );
         */
        StringBuilder builder = new StringBuilder(
                "metas.put(" + "\n" +
                        "   $S, " + "\n" +
                        "    $T.create(" + "\n" +
                        "        $T.Type." + routeType + "," + "\n" +
                        "        $T." + threadMode + ", " + "\n" +
                        "        $T.class," + "\n" +
                        "        new String[]{" + "\n"
        );
        // Add interceptorURIs
        int size = interceptorURIs.size();
        for (int i = 0; i < size; i++) {
            builder.append(
                    "              \"" + interceptorURIs.get(i) + "\""
            );
            if (i != size - 1) {
                builder.append(", ");
            }
            builder.append("\n");
        }
        // append suffix
        builder.append(
                "        }" + "\n" +
                        "    )" + "\n" +
                        ");" + "\n"
        );
        loadInto.addCode(
                builder.toString(),
                path,
                ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                ClassName.get(Constants.PACKAGE_NAME_DATA, Constants.SIMPLE_NAME_ROUTE_META),
                ClassName.get(ThreadMode.class),
                element
        );
    }

    /**
     * <pre>
     *         metas = caches.get("component1");
     *         if (metas == null) {
     *             metas = new HashMap<>();
     *         }
     *         metas.put(
     *                 "component1",
     *                 "Component1Activity",
     *                 RouteMeta.create(
     *                         RouteMeta.Type.CLASS_NAME_ACTIVITY,
     *                         ThreadMode.MAIN,
     *                         Component1Activity.class,
     *                         new String[]{..., ...}
     *                 )
     *         );
     * </pre>
     */
    private String methodCode(String routeType, ThreadMode threadMode, List<String> interceptorClassNames) {
        // append prefix
        StringBuilder builder = new StringBuilder();
        /*
          metas.put(
                    "component1",
                    "Component1Activity",
                    RouteMeta.create(
                            RouteMeta.Type.CLASS_NAME_ACTIVITY,
                            ThreadMode.MAIN,
                            Component1Activity.class,
                            new String[]{..., ...}
                    )
           );
         */
        builder.append(
                Constants.METHOD_LOAD_INTO_PARAMETER_NAME_ROUTE_CACHES + ".put(" + "\n" +
                        "      $S, " + "\n" +
                        "      $T.create(" + "\n" +
                        "          $T.Type." + routeType + "," + "\n" +
                        "          $T." + threadMode + ", " + "\n" +
                        "          $T.class," + "\n" +
                        "          new String[]{" + "\n"
        );
        // Add interceptorURIs
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
