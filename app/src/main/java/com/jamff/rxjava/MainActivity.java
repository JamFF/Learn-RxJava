package com.jamff.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "JamFF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.bt_hello_world).setOnClickListener(this);
        findViewById(R.id.bt_subscribe_1).setOnClickListener(this);
        findViewById(R.id.bt_subscribe_2).setOnClickListener(this);
        findViewById(R.id.bt_observer).setOnClickListener(this);
        findViewById(R.id.bt_do).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_hello_world:
                helloRxJava_create();
                helloRxJava_just();
                helloRxJavaLambda();
                break;

            case R.id.bt_subscribe_1:
                subscribe_1();
                break;

            case R.id.bt_subscribe_2:
                subscribe_2();
                break;

            case R.id.bt_observer:
                observer();
                break;

            case R.id.bt_do:
                do_operate();
                break;
        }
    }

    private void helloRxJava_create() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<String>() {
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
    }

    private void helloRxJava_just() {
        Disposable disposable = Observable.just("RxJava_just").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    private void helloRxJavaLambda() {
        Disposable disposable = Observable.just("RxJava_Lambda").subscribe(s -> Log.d(TAG, s));
    }

    private void subscribe_1() {

        Disposable disposable = Observable.just("Hello World").subscribe(new Consumer<String>() {
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
        });

    }

    private void subscribe_2() {

        Disposable disposable = Observable.just("Hello World").subscribe(new Consumer<String>() {
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
    }

    private Disposable mDisposable;

    private void observer() {
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
    }

    // do操作符
    private void do_operate() {
        Disposable disposable = Observable.just("Hello")
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "doOnNext: " + s);
                    }
                })
                .doAfterNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "doAfterNext: " + s);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "doOnComplete: ");
                    }
                })
                // 订阅之后回调的方法
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.d(TAG, "doOnSubscribe: ");
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "doAfterTerminate: ");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "doFinally: ");
                    }
                })
                // Observable每发射一个数据就会触发这个回调，不仅包括onNext，还包括onError和onCompleted
                .doOnEach(new Consumer<Notification<String>>() {
                    @Override
                    public void accept(Notification<String> stringNotification) throws Exception {
                        if (stringNotification.isOnNext()) {
                            Log.d(TAG, "doOnEach: onNext");
                        } else if (stringNotification.isOnComplete()) {
                            Log.d(TAG, "doOnEach: onComplete");
                        } else if (stringNotification.isOnError()) {
                            Log.d(TAG, "doOnEach: onError");
                        }
                    }
                })
                // 订阅后可以取消订阅
                .doOnLifecycle(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.d(TAG, "doOnLifecycle: " + disposable.isDisposed());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "doOnLifecycle run: ");
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "收到消息: " + s);
                    }
                });
        Log.d(TAG, "disposable isDisposed: " + disposable.isDisposed());
    }
}
