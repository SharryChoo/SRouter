## SRouter
SRouter 是一款对 Android 端的**提供渐进式组件化服务的路由框架**

## 功能介绍
- 支持路由模块动态装载与卸载
- 支持自定义页面跳转时的 Transaction 动画
- 支持通过 URL 进行路由寻址
- **支持通过接口中的模板方法进行路由寻址(与 Retrofit 类似)**
- **提供局部和全局两种类型的拦截器, 以满足不同场景的 Hook 需求**
  - 局部拦截器包含**路由拦截器、页面拦截器和模板方法拦截器**
  - 支持拦截器按照优先级排序
- **支持 Activity/Fragment 中 Intent 数据自动注入**
- **支持回调获取目标页面的 ActivityResult**
- **支持通过路由获取 原生/AppCompat/AndroidX 包下的 Fragment**
- **支持添加寻址回调适配器, 可实现与 RxJava 无缝衔接**
- **支持为 PendingIntent 构建转发 Intent, 可通过路由转发到目标页面**

## 安装指南
![New Version](https://jitpack.io/v/SharryChoo/SRouter.svg)

### Step 1
在工程的根目录的 build.gradle 中添加 jitpack 的 maven 仓库
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

#### 模块依赖关系
![模块依赖关系图](https://i.loli.net/2019/06/04/5cf61d6ea8b7576967.jpg)

## 基础功能
### 一) 初始化
SRouter 的初始化操作通过 **SRouter.init()** 方法执行, 推荐在 BaseApplication 的 onCreate 中进行
```
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 在 BaseApplication 中调用 init 方法执行初始化操作
        SRouter.init(this);
    }
}
```

### 二) 模块的装载与卸载 
SRouter 根据**模块的唯一标识**进行装载与卸载, 如下所示
```
// 装载想要使用的 Module
SRouter.registerModules(xxx, ...);
// 卸载要使用的 Module
SRouter.unregisterModules(xxx, ...);
```
请确保传入的模块标识与 module 中 build.gradle 中声明的描述一致

### 三) 声明寻址目标 
SRouter **通过 @Route 注解声明一个 Class 为一个路由目标地址**, 其可作用的 class 如下
- Activity
- Fragment/Fragment(androidx)/Fragment(v4)
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

### 四) 寻址发起
#### 1. 普通寻址
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

#### 2. URL 寻址
```
val url = "{Custom u scheme}://found/found_fragment?title=HelloWorld&amount=12.34"
SRouter.request(url)
        ......
        .navigation(this)
```
url 跳转即 request 构造时不同, 其他使用方式一致

#### 3. 模板方法寻址
SRouter 可**通过 @RouteMethod 注解声明一个方法为寻址入口**
```
public interface RouteApi {

    /**
     * @param context 若无 context 参数, 会使用 application context 跳转
     */
    @RouteMethod(
            authority = ModuleConstants.Personal.NAME,
            path = ModuleConstants.Personal.PERSONAL_ACTIVITY,
    )
    ICall personalCenter(
            Context context,
            @QueryParam(key = "content") String content,
            @RequestCode int requestCode,
            @Flags int activityFlags
    );

}
```
实例化模板方法
```
 val routeApi = SRouter.createApi(RouteApi::class.java)
```
使用接口进行路由寻址
```
val cancelable: ICancelable = routeApi.personalCenter(this).call()
```
使用方式与 Retrofit 的模板接口类似, 使用这种方式可以**更快捷、更精准**的进行寻址操作

#### 4. 中断寻址
如果你想控制路由跳转的时机, 以及中途取消等操作, 可以使用以下方式
```
// 获取可跳转的 ICall 对象
val call:ICall = SRouter.request(ModuleConstants.Personal.NAME, ModuleConstants.Personal.PERSONAL_ACTIVITY)
        .setActivityOptions(ActivityOptionsCompat.makeBasic())
        .newCall(this)

// 自定义跳转时机
val cancelable: ICancelable = call.post(object : IInterceptor.ChainCallback {
        override fun onSuccess(response: Response) {
            // 寻址成功, 获取 Response
        }

        override fun onFailed(throwable: Throwable?) {
            // 寻址失败
        }

        override fun onCanceled() {
            // 寻址被中途取消了
        }
    })
// 自定义取消时机
cancelable.cancel()
```
调用 ICall.call() 方法可以获取到一个 ICancelable 对象, 通过 ICancelable.cancel() 可以在路由寻址过程中执行中断操作

### 五) 获取寻址结果
#### 1. 获取 ActivityResult
```
SRouter.request(xxx, xxx)
        .setRequestCode(100)
        ......
        .navigation(this) {it: Response ->
            if (it.activityResult != null && it.activityResult.resultCode == Activity.RESULT_OK) {
                val intent = it.activityResult.data
                // todo something.
            }
        }
```
navigation 方法支持传入一个 Callback, 当路由成功后可以获取到 Response 对象, 在 Response 中可以获取到你想要的所有数据
- ActivityResult
- Fragment
- Request

#### 2. 获取 Fragment
```
SRouter.request(xxx, xxx)
        .setRequestCode(100)
        ......
        .navigation(this) {it: Response ->
            // 获取 Fragment
            val fragment: Fragment = it.fragment
        }
```
获取 Fragment 的方式与获取 ActivityResult 是一致的
- **需要指定 Fragment 的类型**, 支持 V4 和 Androidx 包下的 Fragment 

#### 3. 获取服务
```
SRouter.request(xxx, xxx)
        ......
        .navigation(this) {it: Response ->
            // 获取 IService
            val service: IService = it.service
            // 同步建立连接, 获取数据
            val obj = service.connect();
            // 异步建立连接
            it.service.connectAsync {
                // TODO 获取数据 
            }
        }
```
获取到 IService 服务之后, 可以调用 IService.connect() 建立连接, 也可以通过 IService.connectAsync() 异步建立连接
- 无论是同步还是异步建立连接, 最终都会返回 Object 对象, 因此 **IService 也充当做跨模块提供数据来使用**

## 拦截器使用
SRouter 的所有拦截器均在主线程执行, 提供了**局部**和**全局**两种类型的拦截器
- 局部拦截器包含**路由拦截器、页面拦截器和模板方法拦截器**
- 支持拦截器按照优先级排序

### 一) 拦截器的定义
拦截器的定义需要实现 IInterceptor 接口
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
  - 通过上下文对象可以获取上游的所有数据
- chain.dispatch(): 若需要继续往下分发, 需要主动调用该方法

拦截器实现完成之后, 还可以选择**使用 @RouteInterceptor 标注, 拓展拦截器优先级排序的功能支持**, 如下所示
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
  - range in [0, 10] 逐级递增, 路由跳转时会根据优先级进行排序

### 二) 拦截器的使用
SRouter 的拦截器根据使用场景, 可以分为如下几种类型
- 局部拦截器
  - 路由拦截器
  - 页面拦截器
  - 模板方法拦截器
- 全区拦截器

接下来对其使用方式进行逐一介绍

#### 1. 局部拦截器
##### 1) 路由拦截器
路由拦截器是指在构建寻址请求时添加的拦截器
- 支持通过拦截器 URI 添加拦截器
  - **通过 URI 添加的拦截器, 可以享受优先级排序**
- 支持直接添加拦截器对象
  - 这种方式当做最高优先级处理

```
SRouter.request(xxx, xxx)
        // 添加拦截器的 URI
        .addInterceptorURI(ModuleConstants.Personal.PERMISSION_INTERCEPTOR)
        // 添加拦截器对象
        .addInterceptor(new PermissionInterceptor())
        ......
```

##### 2) 页面拦截器
页面拦截器集成在了 @Route 注解中, 只支持通过拦截器的 URI 进行添加
- 支持添加多个页面拦截器
```
@Route(
        authority = ModuleConstants.Personal.NAME,
        path = ModuleConstants.Personal.PERSONAL_ACTIVITY,
        // 指定跳转到该页面, 要经过的拦截器
        interceptorURIs = [ModuleConstants.login.LOGIN_INTERCEPTOR],
        desc = "个人中心页面"
)
class PersonalActivity : AppCompatActivity() {
}
```
##### 3) 模板方法拦截器
模板方法拦截器集成到了 @RouteMethod 注解中, 只支持通过拦截器的 URI 进行添加
- 支持添加多个页面拦截器
```
public interface RouteApi {
    @RouteMethod(
            authority = ModuleConstants.Personal.NAME,
            path = ModuleConstants.Personal.PERSONAL_ACTIVITY,
	    // 在方法注解中添加静态拦截器
            interceptorURIs = ModuleConstants.Personal.PERMISSION_INTERCEPTOR
    )
    ICall personalCenter(
            Context context,
            @QueryParam(key = "content") String content,
            @RequestCode int requestCode,
            @Flags int activityFlags
    );
}
```

#### 2. 全局拦截器
全局拦截器即添加之后作用于全局的拦截器, 使用方式如下
```
// 添加全局拦截器对象
SRouter.addGlobalInterceptor(xxx);
// 添加全局拦截器 URI
SRouter.addGlobalInterceptorUri(xxx);
```
全局拦截器可以用来记录路由寻址的数据或者用于进行埋点统计等

### 三) 执行流程图
![拦截器执行流程图](https://i.loli.net/2019/09/16/196XmZsDeI5TlLH.jpg)

## 拓展
### 一) 参数注入
如果你不想写 Intent 参数注入的代码, @Query 注解可以帮你完成这个操作
```
public class FoundFragment extends Fragment {

    @Query(key = "title")
    String title = "default_title";

    @Query(key = "amount")
    double amount = 1.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SRouter.bindQuery(this);
        ......// 处理后续操作
    }
    
}
```

### 二) 回调适配器
如果你习惯了 RxJava 的链式调用, 并为 navigation 是异步回调导致不能链式编写代码而苦恼, 那么这个回调适配器可能会解决你的问题 

这里以将 ICall 适配成 RxJava 的 Observable 为例

#### 1. 编写适配器类
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
#### 2. 编写以 Response 为数据源的 Observable 类
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

#### 3. 添加适配器
```
SRouter.addCallAdapter(new RxJavaAdapter());
```

#### 4. 链式调用
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
#### 5. 模板方法调用
```
public interface RouteApi {

    @RouteMethod(
            authority = ModuleConstants.Found.NAME,
            path = ModuleConstants.Found.FOUND_FRAGMENT
    )
    ResponseObservable foundFragment(
            @QueryParam(key = "title") double title,
            @QueryParam(key = "content") String content
    );

    /**
     * @param context 若无 context 参数, 会使用 application context 跳转
     */
    @RouteMethod(
            authority = ModuleConstants.Personal.NAME,
            path = ModuleConstants.Personal.PERSONAL_ACTIVITY,
            interceptorURIs = ModuleConstants.App.LOGIN_INTERCEPTOR
    )
    ResponseObservable personalCenter(
            Context context,
            @QueryParam(key = "content") String content,
            @RequestCode int requestCode,
            @Flags int activityFlags
    );

}
```
这里只是以 RxJava 举例, 你可以自行实现 ICallAdapter 接口, 将路由的 ICall 适配成任何你想要的对象

## 其他
更多使用方式请查看 Repository 中的示例代码
