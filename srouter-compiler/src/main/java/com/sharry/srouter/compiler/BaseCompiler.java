package com.sharry.srouter.compiler;

import com.sharry.srouter.annotation.compiler.Query;
import com.sharry.srouter.annotation.compiler.Route;
import com.sharry.srouter.annotation.compiler.RouteInterceptor;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.sharry.srouter.compiler.Constants.CLASS_NAME_ACTIVITY;
import static com.sharry.srouter.compiler.Constants.CLASS_NAME_FRAGMENT;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-23
 */
public abstract class BaseCompiler extends AbstractProcessor {

    protected Filer filer;
    protected Logger logger;
    protected Types types;
    protected TypeUtils typeUtils;
    protected Elements elementUtils;
    protected TypeMirror typeActivity;
    protected TypeMirror typeFragment;
    protected TypeMirror typeFragmentV4;
    protected TypeMirror typeFragmentX;
    protected TypeMirror typeService;
    protected TypeMirror typeInterceptor;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        logger = new Logger(processingEnv.getMessager());
        types = processingEnv.getTypeUtils();                // Get type utils.
        elementUtils = processingEnv.getElementUtils();      // Get class meta.
        typeUtils = new TypeUtils(types, elementUtils);
        findDeclaredSpecialType();
        logger.i(">>>>>>>>>>>>>>>>>>>>> init <<<<<<<<<<<<<<<<<<<<<<<");
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Route.class.getCanonicalName());
        annotations.add(RouteInterceptor.class.getCanonicalName());
        annotations.add(Query.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    /**
     * Find declared special type.
     */
    private void findDeclaredSpecialType() {
        typeActivity = elementUtils.getTypeElement(CLASS_NAME_ACTIVITY).asType();
        typeFragment = elementUtils.getTypeElement(CLASS_NAME_FRAGMENT).asType();
        typeFragmentV4 = elementUtils.getTypeElement(Constants.CLASS_NAME_FRAGMENT_V4).asType();
        typeFragmentX = elementUtils.getTypeElement(Constants.CLASS_NAME_FRAGMENT_X).asType();
        typeService = elementUtils.getTypeElement(Constants.CLASS_NAME_ISERVICE).asType();
        typeInterceptor = processingEnv.getElementUtils().getTypeElement(Constants.CLASS_NAME_IINTERCEPTOR).asType();
    }

}
