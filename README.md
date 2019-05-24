## 一. 功能介绍
- 支持解析 URL 直接进行路由跳转
- 提供可自定义优先级的路由拦截器
- 支持自定义跳转时的 Transaction 动画
- **支持路由模块动态装载与卸载**
- **支持 Activity/Fragment 中 Intent 数据自动注入**
- **支持直接获取目标页面的 Intent 返回值**
- **支持拓展与 RxJava 无缝衔接**

## 二. 功能集成
### Step 1
在工程的根 build.gradle 中添加 jitpack 的 maven 仓库
```
allprojects {
    repositories {
    	...
	    maven { url 'https://jitpack.io' }
    }
}
```
### Step 2
在工程的 base 库中的 build.gradle 添加如下依赖
```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    ......
    // AndroidX 版本
    api "androidx.appcompat:appcompat:x.x.x"
    // SRouter 依赖
    api "com.github.SharryChoo.SRouter:srouter-support:x.x.x"
}
```
请确保工程中的业务 module 和 app 库依赖于你的 base 库

### Step 3
在使用到 SRouter 提供的注解的 module 中的 build.gradle 添加如下依赖

#### Java module
```
apply plugin: 'com.android.library'

android {
    ......
    defaultConfig {
        ......
        /**
         * Java 的 Module 使用该方式进行编译时注解扫描, 不兼容 Kotlin 文件
         */
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ["moduleName": "替换成当前 Module 唯一标示, 用于组件注册"]
            }
        }
    }
}


dependencies {
    ......
    // Java 的编译时注解处理依赖
    annotationProcessor "com.github.SharryChoo.SRouter:srouter-compiler:x.x.x"
}
```

#### Kotlin module
```
apply plugin: 'com.android.library'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'

android {
    compileSdkVersion rootProject.compileSdkVersion
    defaultConfig {
        minSdkVersion rootProject.minSdkVersion
        targetSdkVersion rootProject.targetSdkVersion
    }
}

/**
 * Kotlin 的 Module 使用该方式进行编译时注解扫描, 兼容 Java 文夹
 */
kapt {
    arguments {
        arg("moduleName", "替换成当前 Module 唯一标示, 用于组件注册")
    }
}

dependencies {
    ......
    // Kotlin 的编译时注解处理依赖
    kapt "com.github.SharryChoo.SRouter:srouter-compiler:$sRouterVersion"
}
```
kapt 是支持 Java 代码的, 不用担心 kotlin 与 java 的混编问题

## 三. 功能使用
### 一) 初始化
```
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 1. 在 BaseApplication 中调用 init 方法执行初始化操作
        SRouter.init(this);
        // 2. 注册想要使用的 Module
        SRouter.registerModules(xxx, xxx, xxx);
        // 根据需求动态的解注册想要使用的 Module
        // SRouter.unregisterModules();
    }

}
```
- SRouter.registerModules
  - 确保传入参数与 module 中 build.gralde 中声明的描述一致 
- SRouter.unregisterModules()
  - 根据业务需求, 运行时解注册想要使用的 module 

### 二) 路由跳转
#### 1. 目标声明
路由目标声明使用 @Route 描述, 其可作用的 class 如下
- Activity
- Fragment/Fragment(androidx)
- IService: 用于自己实现目标的服务逻辑
```
@Route( 
        authority = ModuleConstants.Personal.NAME,
        path = ModuleConstants.Personal.PERSONAL_ACTIVITY,
        desc = "路由描述"
)
class PersonalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_activity_personal)
    }

}
```
- authority: 路由作用页面的所属的模块, 不必与当前类所在的 module 名一致, 用于渐进式组件分割
- path: 对应的路径

请确保 authority 与 path 的组合是唯一的

#### 2. 跳转发起
##### 普通跳转
```
SRouter.request(ModuleConstants.Personal.NAME, ModuleConstants.Personal.PERSONAL_ACTIVITY)
        // 设置 Activity 转场动画
        .setActivityOptions(ActivityOptionsCompat.makeBasic())
        // 跳转参数
        .withString("key", "value")
        // 跳转时延
        .setDelay(1000)
        // 执行跳转操作
        .navigation(this)
```
##### url 跳转
```
val url = "router://found/found_activity?opr=15533&password=1234456"
SRouter.request(url)
        ......
        .navigation(this)
```
url 跳转即 request 构造时不同, 其他使用方式一致

##### 获取 ActivityResult
```
SRouter.request(ModuleConstants.Personal.NAME, ModuleConstants.Personal.PERSONAL_ACTIVITY)
        .setRequestCode(100)
        ......
        .navigation(this) {it: Response ->
            if (it.activityResult != null && it.activityResult.resultCode == Activity.RESULT_OK) {
                val intent = it.activityResult.data
                // todo something.
            }
        }
```
navigation 方法支持传入一个 Callback, 当路由成功后可以获取到 Rresponse 对象, 在 Response 中可以获取到你想要的所有数据
- ActivityResult
- Fragment
- Request

##### 获取 Fragment
```
SRouter.request(ModuleConstants.Personal.NAME, ModuleConstants.Personal.PERSONAL_FRGAMENT)
        .setRequestCode(100)
        ......
        .navigation(this) {it: Response ->
            // 获取 android.app.Fragment
            val fragment = it.fragment
            // 获取 androidx.fragment.app.Fragmennt
            val fragmentX = it.fragmentX
        }
```
获取 Fragment 的方式与获取 ActivityResult 是一致的, **你可以根据目标 Fragment 的类型调用不同的方法获取**

##### 按需跳转
如果你想控制路由跳转的时机, 以及中途取消等操作, 可以使用以下方式
```
// 获取可跳转的 ICall 对象
val call:ICall = SRouter.request(ModuleConstants.Personal.NAME, ModuleConstants.Personal.PERSONAL_ACTIVITY)
        .setActivityOptions(ActivityOptionsCompat.makeBasic())
        .newCall(this)
// 自定义跳转时机
val cancelable: ICancelable = call.post(object : IInterceptor.ChainCallback {
        override fun onSuccess(response: Response) {
            // 路由成功, 获取 Response
        }

        override fun onFailed(throwable: Throwable?) {
            // 路由失败
        }

        override fun onCanceled() {
            // 路由被中途取消了
        }
    })
// 自定义取消时机
cancelable.cancel()
```

### 二) 拦截器
在 SRouter 中有效的拦截器, 需要用户实现 IInterceptor 接口

#### 1. 拦截器的定义
```
public class PermissionInterceptor implements IInterceptor {

    @Override
    public void intercept(@NonNull Chain chain) {
        // 责任链上下文 ChainContext
        ChainContext chainContext = chain.chainContext();
        // 路由发起时的 Context
        Context context = chainContext.getBaseContext();
        // 上游分发的 Request
        Request request = chainContext.request;
        // 向下分发
        chain.dispatch();
    }

}
```
- Chain: 描述分发的责任链
- Chain.chainContext(): 获取责任链的上下文

这是最基本的拦截器定义, 你只能够通过 new 对象添加, 除此之外还可以通过注解标注一个拦截器
```
@RouteInterceptor(
        value = ModuleConstants.Personal.PERMISSION_INTERCEPTOR,
        priority = 2
)
public class PermissionInterceptor implements IInterceptor {
      ......
}
```
- **value: 拦截器的唯一标识符 URI**
- **priority: 拦截器的优先级**
  - range in [0, 10], 逐级递增

路由跳转时会根据优先级进行排序

#### 2. 拦截器的使用
##### 方式一
配合路由使用
```
@Route(
        authority = ModuleConstants.Personal.NAME,
        path = ModuleConstants.Personal.PERSONAL_ACTIVITY,
        // 指定跳转到该页面, 要经过的拦截器
        interceptorURIs = [ModuleConstants.App.PERMISSION_INTERCEPTOR],
        desc = "个人中心页面"
)
class PersonalActivity : AppCompatActivity() {
}
```
##### 方式二
在路由跳转时可以通过两种方式添加拦截器
```
SRouter.request(xxx, xxx)
        // 直接添加拦截器对象
        .addInterceptor(new PermissionInterceptor())
        // 添加拦截器的 URI
        .addInterceptorURI(ModuleConstants.Personal.PERMISSION_INTERCEPTOR)
        ......
```



### 三) 拓展
#### 参数的注入
如果你不想写 Intent 参数注入的代码, @Query 注解可以帮你完成这个操作
```
@Route(
        authority = ModuleConstants.Found.NAME,
        path = ModuleConstants.Found.FOUND_ACTIVITY,
        desc = "组件1的入口页面"
)
public class FoundActivity extends AppCompatActivity {

    @Query(key = "opr")
    String oprNo = "123456";

    @Query(key = "password")
    String password = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.found_activity_found);
        parseIntent();
    }

    private void parseIntent() {
        // 调用 SRouter.bindQuery 便可以将 Intent 的值注入到对应的属性中
        SRouter.bindQuery(this);
        ...... // 编写输入注入后的代码 
    }
}
```

#### RxJava 的拓展
##### 1. 编写适配器类
实现 ICallAdapter 接口编写 RxJava 的适配器
```
public class RxJavaAdapter implements ICallAdapter<ResponseObservable> {

    @Override
    public ResponseObservable adapt(@NonNull ICall call) {
        return new ResponseObservable(call);
    }

    @Override
    public Class<ResponseObservable> adaptType() {
        return ResponseObservable.class;
    }

}
```
##### 2. 编写以 Response 为数据源的 Observable 类
```
public final class ResponseObservable extends Observable<Response> {

    private final ICall call;

    ResponseObservable(ICall call) {
        this.call = call;
    }

    @Override
    protected void subscribeActual(final Observer<? super Response> observer) {
        // Since Call is a one-shot type, clone it for each new observer.
        ICancelable cancelable = call.post(new IInterceptor.ChainCallback() {
            @Override
            public void onSuccess(@NonNull Response response) {
                observer.onNext(response);
            }

            @Override
            public void onFailed(Throwable throwable) {
                observer.onError(throwable);
            }

            @Override
            public void onCanceled() {
                // do nothing.
            }
        });
        observer.onSubscribe(new CallDisposable(cancelable));
    }

    private static final class CallDisposable implements Disposable {

        private final ICancelable cancelable;

        CallDisposable(ICancelable cancelable) {
            this.cancelable = cancelable;
        }

        @Override
        public void dispose() {
            cancelable.cancel();
        }

        @Override
        public boolean isDisposed() {
            return cancelable.isCanceled();
        }
    }
}
```

##### 3. 添加适配器
```
SRouter.addCallAdapter(new RxJavaAdapter());
```

##### 4. 结合使用
```
val disposable = SRouter.request(ModuleConstants.App.NAME, ModuleConstants.App.LOGIN_ACTIVITY)
        // 构建 Activity 相关配置
        .setRequestCode(100)
        .addInterceptorURI(ModuleConstants.Personal.PERMISSION_INTERCEPTOR)
        .newCall(chainContext.baseContext)
        // 将 ICall 转为 ResponseObservable
        .adaptTo(ResponseObservable::class.java)
        // 这里可以获取到一个 Observable<Response> 类型的数据源
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
                if (it.activityResult.resultCode == RESULT_OK) {
                    SRouter.navigation(chainContext.baseContext, chainContext.request)
                }
        }
```
这里只是以 RxJava 举例, 你可以自行实现 ICallAdapter 接口, 将路由的 ICall 适配成任何你想要的对象

## 其他
更多使用方式请下载 Demo 体验
- 如果您觉得使用不便, 请多多 issue
- 如果您感觉不错, 请多多 star, 这会让笔者备受鼓舞
