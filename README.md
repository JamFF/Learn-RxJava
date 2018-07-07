# Learn-RxJava
学习RxJava2

## RxJava简介

### RxJava的优势
* 函数式风格：对可观察的数据流使用无副作用的输入/输出函数，避免了程序里错综复杂的状态。
* 简化代码：Rx的操作符通常可以将复杂的难题简化为很少的几行代码。
* 异步错误处理：传统的`try/catch`没办法处理异步计算，Rx提供了合适的错误处理机制。
* 轻松使用并发：Rx的`Observablers`（包括`Observable`、`Flowable`、`Single`、`Completable`和`Maybe`）和`Schedulers`可以让开发者摆脱底层的线程同步和各种并发问题。

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

    使用`just`代替`create`，将传入参数依次发出，不需要我们调用`onNext`和`onComplete`

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
1. 创建观察者——`Observable`
2. 创建被观察者——`Observer`
3. 使用`subscribe()`进行订阅

### Observable

在下面例子中，`just()`是创建操作符，创建一个`Observable`；`Consumer`是消费者用于接受单个值。

```java
Observable.just("RxJava_just").subscribe(new Consumer<String>() {
    @Override
    public void accept(String s) throws Exception {
        Log.d(TAG, s);
    }
});
```

### subscribe

`subscribe`有多个重载方法。
>subscribe(onNext)
>subscribe(onNext, onError)
>subscribe(onNext, onError, onComplete)
>subscribe(onNext, onError, onComplete, onSubscribe)

使用第四个重载方法，观察执行顺序

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
subscribe
Hello World
onComplete()

### Observer

RxJava2中，`Observable`不再支持订阅`Subscriber`，而是需要使用`Observer`作为观察者

```java
Observable.just("Hello World").subscribe(new Observer<String>() {
    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "subscribe");
    }

    @Override
    public void onNext(String s) {
        Log.d(TAG, s);
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
subscribe
Hello World
onComplete()

### do操作符

do操作符可以给Observable的生命周期的各个阶段加上一系列的回调监听