# Learn-RxJava
学习RxJava2

## RxJava简介

### RxJava的优势
* 函数式风格：对可观察的数据流使用无副作用的输入/输出函数，避免了程序里错综复杂的状态。
* 简化代码：Rx的操作符通常可以将复杂的难题简化为很少的几行代码。
* 异步错误处理：传统的 `try/catch` 没办法处理异步计算，Rx提供了合适的错误处理机制。
* 轻松使用并发：Rx的 `Observablers`（包括`Observable`、`Flowable`、`Single`、`Completable`和`Maybe`）和 `Schedulers` 可以让开发者摆脱底层的线程同步和各种并发问题。

### Hello World

* RxJava版本的Hello World

    ```java
    Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            emitter.onNext("RxJava_create");
        }
    }).subscribe(new Consumer<String>() {
        @Override
        public void accept(String s) throws Exception {
            Log.d(TAG, s);
        }
    });
    ```
    
* 简化版

    使用 `just` 代替 `create`，将传入参数依次发出，不需要我们调用 `onNext` 和  `onComplete`

    ```java
    Observable.just("RxJava_just").subscribe(new Consumer<String>() {
        @Override
        public void accept(String s) throws Exception {
            Log.d(TAG, s);
        }
    });
    ```
    
* 使用Lambda表达式

    ```java
    Observable.just("RxJava_Lambda").subscribe(s -> Log.d(TAG, s));
    ```
    
## RxJava基础知识

RxJava的使用通常需要三步。
1. 创建观察者——[Observable](#observable)
2. 创建被观察者——[Observer](#observer)
3. 使用[subscribe()](#subscribe)进行订阅

### <span id = "observable">Observable</span>

在下面例子中，`just()` 是创建操作符，创建一个 `Observable`；`Consumer` 是消费者用于接受单个值。

```java
Observable.just("RxJava_just").subscribe(new Consumer<String>() {
    @Override
    public void accept(String s) throws Exception {
        Log.d(TAG, s);
    }
});
```

### <span id = "subscribe">subscribe()</span>

`subscribe` 有多个重载方法。

```java
subscribe()
subscribe(onNext)
subscribe(onNext, onError)
subscribe(onNext, onError, onComplete)
subscribe(onNext, onError, onComplete, onSubscribe)
subscribe(observer)
```

前四个方法更为相似，第五个方法在[Observer](#observer)里具体说明。

* 前四个方法返回值为 `Disposable`，可以在发送完成之前解除订阅。
* `onNext()`: 执行完事件队列中的一个事件后回调。
* `onCompleted()`: 事件队列完结。RxJava 不仅把每个事件单独处理，还会把它们看做一个队列。RxJava 规定，当不会再有新的 `onNext()` 发出时，需要触发 `onCompleted()` 方法作为标志。
* `onError()`: 事件队列异常。在事件处理过程中出异常时，`onError()`会被触发，同时队列自动终止，不允许再有事件发出。
* 在一个正确运行的事件序列中, `onCompleted()` 和 `onError()` 有且只有一个，并且是事件序列中的最后一个。需要注意的是，`onCompleted()` 和 `onError()` 二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个。
* 调用者可根据需求选择不同的回调，处理业务，例如不关心 `onNext` 和 `onComplete`，完全可以使用 `subscribe()`。

使用第五个重载方法，观察执行顺序

```java
Observable.just("Hello World").subscribe(new Consumer<String>() {
    @Override
    public void accept(String s) throws Exception {
        Log.d(TAG, s);
    }
}, new Consumer<Throwable>() {
    @Override
    public void accept(Throwable throwable) throws Exception {
        Log.e(TAG, throwable.getMessage());
    }
}, new Action() {
    @Override
    public void run() throws Exception {
        Log.d(TAG, "onComplete()");
    }
}, new Consumer<Disposable>() {
    @Override
    public void accept(Disposable disposable) throws Exception {
        Log.d(TAG, "subscribe");
    }
});
```

执行结果：
```
subscribe  
Hello World  
onComplete() 
``` 

### <span id = "observer">Observer</span>

RxJava2中，`Observable`不再支持订阅`Subscriber`，而是需要使用`Observer`作为观察者。

当然你也可以根据需求，像上面代码一样使用`Consumer`，其实等价于下面的代码

```java
Observable.just("Hello World").subscribe(new Observer<String>() {
    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        Log.d(TAG, "subscribe");
    }

    @Override
    public void onNext(String s) {
        Log.d(TAG, s);
        if (!"Hello World".equals(s)) {
            // 接收到异常数据，可以解除订阅
            mDisposable.dispose();
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.getMessage());
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete()");
    }
});
```

执行结果：
```
subscribe
Hello World
onComplete()
```

### do操作符

do操作符可以给Observable的生命周期的各个阶段加上一系列的回调监听




### 参考

[《RxJava 2.x 实战》](https://www.jianshu.com/p/9d0db48426ee?utm_source=oschina-app)

[给 Android 开发者的 RxJava 详解](http://gank.io/post/560e15be2dca930e00da1083)

[RxJava2 浅析](https://blog.csdn.net/maplejaw_/article/details/52442065)