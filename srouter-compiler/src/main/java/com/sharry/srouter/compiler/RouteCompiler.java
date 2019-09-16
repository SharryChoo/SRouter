package com.sharry.srouter.compiler;

import com.google.auto.service.AutoService;
import com.sharry.srouter.annotation.compiler.Route;
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
import java.util.List;
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
 * Process annotation {@link Route}
 * <p>
 * Generate SRouter$$Routes$$xxx.java file.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/17 16:28
 */
@AutoService(Processor.class)
public class RouteCompiler extends BaseCompiler {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // Find module name.
        String moduleName = findModuleName();

        // Validate elements
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (null == elements || elements.isEmpty()) {
            return false;
        }
        logger.i("elements size is " + elements.size());
        /*
          ```Map<String, RouteMeta>```
         */
        ParameterizedTypeName inputInnerMapTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Constants.PACKAGE_NAME_SROUTER, Constants.SIMPLE_NAME_ROUTE_META)
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
        ClassName superClassName = ClassName.get(Constants.PACKAGE_NAME_SROUTER, Constants.SIMPLE_NAME_IROUTE);
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(Constants.SIMPLE_NAME_PREFIX_OF_ROUTERS + moduleName)
                .addModifiers(Modifier.FINAL, PUBLIC)
                .addSuperinterface(superClassName)
                .addMethod(loadIntoMethodSpec.build());
        // Perform generate java file
        try {
            JavaFile.builder(Constants.PACKAGE_NAME_OF_GENERATE, classBuilder.build())
                    .addFileComment("SRouter-Compiler auto generate.")
                    .indent("    ")
                    .build()
                    .writeTo(filer);
            logger.i("Generated root, name is " + Constants.SIMPLE_NAME_PREFIX_OF_ROUTERS + moduleName);
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
     *                         SampleActivity.class,
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
                ClassName.get(Constants.PACKAGE_NAME_SROUTER, Constants.SIMPLE_NAME_ROUTE_META)
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
            // Write code into method loadInto.
            writeToMethodLoadInto(
                    loadInto,
                    routeAnnotation.authority(),
                    routePath,
                    Arrays.asList(
                            routeAnnotation.interceptorURIs()
                    ),
                    element
            );
        }
    }

    private void writeToMethodLoadInto(MethodSpec.Builder loadInto, String authority, String path,
                                       List<String> interceptorURIs, Element element) {
        TypeMirror tm = element.asType();
        if (types.isSubtype(tm, typeActivity)) {
            // @Route bind class is child for Activity.
            logger.i("Found activity route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "ACTIVITY", authority, path, interceptorURIs, element);
        } else if (types.isSubtype(tm, typeFragment)) {
            // @Route bind class is child for Fragment.
            logger.i("Found fragment route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "FRAGMENT", authority, path, interceptorURIs, element);
        } else if (types.isSubtype(tm, typeFragmentV4)) {
            // @Route bind class is child for Fragment.
            logger.i("Found fragment v4 route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "FRAGMENT_V4", authority, path, interceptorURIs, element);
        } else if (types.isSubtype(tm, typeFragmentX)) {
            // @Route bind class is child for Fragment.
            logger.i("Found fragment x route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "FRAGMENT_X", authority, path, interceptorURIs, element);
        } else if (types.isSubtype(tm, typeService)) {
            // @Route bind class is child for IService.
            logger.i("Found service route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "SERVICE", authority, path, interceptorURIs, element);
        } else {
            // @Route bind class is child for others.
            logger.i("Found other route: " + tm.toString() + " <<<");
            writeLoadInto(loadInto, "UNKNOWN", authority, path, interceptorURIs, element);
        }
    }

    private void writeLoadInto(MethodSpec.Builder loadInto,
                               String routeType,
                               String authority,
                               String path,
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
                ClassName.get(Constants.PACKAGE_NAME_SROUTER, Constants.SIMPLE_NAME_ROUTE_META),
                ClassName.get(Constants.PACKAGE_NAME_SROUTER, Constants.SIMPLE_NAME_ROUTE_META),
                element
        );
    }

}
