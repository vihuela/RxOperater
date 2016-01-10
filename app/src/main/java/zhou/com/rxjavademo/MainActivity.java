package zhou.com.rxjavademo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

/**
 * Created by zhouml on 2016/1/6.
 */
public class MainActivity extends Activity {

    public static String TAG = MainActivity.class.getSimpleName();
    Subscriber<String> sub = new Subscriber<String>() {
        @Override public void onStart() {
            super.onStart();
            Log.d(TAG, "start");
        }

        @Override public void onCompleted() {
            Log.d(TAG, "completed");
        }

        @Override public void onError(Throwable e) {

        }

        @Override public void onNext(String s) {
            Log.d(TAG, "s=" + s);
        }


    };
    int i;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = TAG.concat(" " + Thread.currentThread().getName());

//        just();
//        from();
//        action();
//        scheduce();
//        map();
//        flatmap();
//        lift();
//        flatmap();
//        compose();
        defer();
//        merge();
//        filter();
//        take();
//        groupby();
//        zip();
//        buffer();
//        empty();
//        never();
//        error();
//        interval();
    }

    private void interval() {
        Observable.interval(500, TimeUnit.MILLISECONDS)
                .doOnCompleted(new Action0() {
                    @Override public void call() {
                        System.out.println("doOnCompleted");
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override public void call() {
                        System.out.println("doOnSubscribe");
                    }
                })
                .subscribe(new Action1<Long>() {
                    @Override public void call(Long aLong) {
                        System.out.println(aLong);
                    }
                });
    }

    private void error() {
        Observable.error(new RuntimeException()).subscribe(new Subscriber<Object>() {
            @Override public void onCompleted() {
                Log.d("main", "onCompleted");
            }

            @Override public void onError(Throwable e) {
                Log.d("main", "onError");
            }

            @Override public void onNext(Object o) {
                Log.d("main", "onNext");
            }
        });
    }

    private void never() {
        Observable.never().subscribe(new Subscriber<Object>() {
            @Override public void onCompleted() {
                Log.d("main", "onCompleted");
            }

            @Override public void onError(Throwable e) {
                Log.d("main", "onError");
            }

            @Override public void onNext(Object o) {
                Log.d("main", "onNext");
            }
        });
    }

    private void empty() {
        Observable.empty().subscribe(new Subscriber<Object>() {
            @Override public void onCompleted() {
                Log.d("main", "oncompleted");
            }

            @Override public void onError(Throwable e) {

            }

            @Override public void onNext(Object o) {

            }
        });
    }

    /**
     * 订阅者缓存消息
     */
    private void buffer() {
        final String[] mails = new String[]{"Here is an email!", "Another email!", "Yet another email!"};
        //每隔1秒就随机发布一封邮件
        Observable<String> endlessMail = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if (subscriber.isUnsubscribed()) return;
                    Random random = new Random();
                    while (true) {
                        String mail = mails[random.nextInt(mails.length)];
                        subscriber.onNext(mail);
                        Thread.sleep(1000);
                    }
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io());
        //把上面产生的邮件内容缓存到列表中，并每隔3秒通知订阅者
        endlessMail.buffer(3).subscribe(new Action1<List<String>>() {
            @Override
            public void call(List<String> list) {

                System.out.println(String.format("You've got %d new messages!  Here they are!", list.size()));
                for (int i = 0; i < list.size(); i++)
                    System.out.println("**" + list.get(i).toString());
            }
        });

    }

    private void zip() {
        Observable<String> observable = Observable.just("1");
        Observable<String> observable1 = Observable.just("2");


        Observable.zip(observable, observable1, new Func2<String, String, Object>() {
            @Override public Object call(String s, String s2) {

                return s + s2;
            }
        }).subscribe(new Action1<Object>() {
            @Override public void call(Object o) {
                Log.d("main", "o=" + o);
            }
        });
    }

    private void defer() {

        i = 10;
        Observable justObservable = Observable.just(i);
        i = 12;
        Observable deferObservable = Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return Observable.just(i);
            }
        });
        i = 15;

        justObservable.subscribe(new Subscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                System.out.println("just result:" + o.toString());
            }
        });

        deferObservable.subscribe(new Subscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                System.out.println("defer result:" + o.toString());
            }
        });
    }


    private void merge() {
        Observable<String> observable = Observable.just("1", "3");
        Observable<String> observable1 = Observable.just("2");
        Observable.merge(observable, observable1).subscribe(new Action1<String>() {
            @Override public void call(String s) {
                Log.d("main", s);
            }
        });
    }

    private void compose() {
        Observable.just("1").compose(new Observable.Transformer<String, String>() {
            @Override public Observable<String> call(Observable<String> stringObservable) {
                stringObservable.subscribeOn(Schedulers.computation());
                return stringObservable;
            }
        }).subscribe(new Action1<Object>() {
            @Override public void call(Object o) {
                Log.d("main", "o=" + o.toString());
            }
        });
    }

    private void filter() {
        Integer[] arr = {1, 2, 3};
        Observable.from(arr)
                .filter(new Func1<Integer, Boolean>() {
                    @Override public Boolean call(Integer integer) {
                        return integer == 2;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override public void call(Integer integer) {
                Log.d("main", integer + "");
            }
        });
    }

    private void take() {
        Observable.just("1", "2", "3")

                .takeFirst(new Func1<String, Boolean>() {
                    @Override public Boolean call(String s) {
                        System.out.println("asasasa");
                        return TextUtils.equals("1", s);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override public void call(String s) {
                        Log.d("main", s);
                    }
                });
    }

    private void groupby() {
        Observable.just("1", "2", "3", "1", "2", new B())
                .groupBy(new Func1<Object, String>() {
                    @Override public String call(Object o) {
                        if (o instanceof String) {
                            return "Str";
                        }
                        return "A";
                    }
                }).subscribe(new Action1<GroupedObservable<String, Object>>() {
            @Override public void call(GroupedObservable<String, Object> stringObjectGroupedObservable) {
                //分组的key
                Log.d("main", stringObjectGroupedObservable.getKey());
                stringObjectGroupedObservable.subscribe(new Action1<Object>() {
                    @Override public void call(Object o) {
                        Log.d("main", o.toString());
                    }
                });
            }
        });
    }

    private void lift() {

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override public void call(Subscriber<? super Integer> subscriber) {
                System.out.println("1>>>>>>:");
                subscriber.onNext(1);
                subscriber.onError(new RuntimeException());
            }
        }).lift(new Observable.Operator<String, Integer>() {
            @Override public Subscriber<? super Integer> call(final Subscriber<? super String> subscriber) {
                return new Subscriber<Integer>() {
                    @Override public void onCompleted() {
                        System.out.println("2>>>>>>:onCompleted");
                        subscriber.onCompleted();//原来的
                    }

                    @Override public void onError(Throwable e) {
                        System.out.println("2>>>>>>:onError");
                        subscriber.onError(e);//原来的
                    }

                    @Override public void onNext(Integer integer) {
                        System.out.println("2>>>>>>:" + integer);
                        subscriber.onNext(integer + "");//原来的
                    }
                };
            }
        }).subscribe(new Subscriber<String>() {
            @Override public void onCompleted() {
                System.out.println("3>>>>>>:onCompleted");
            }

            @Override public void onError(Throwable e) {
                System.out.println("3>>>>>>:onError");
            }

            @Override public void onNext(String s) {
                System.out.println("3>>>>>>:" + s);
            }
        });

//        Observable.just("1").lift(new Observable.Operator<String, String>() {
//            @Override public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {
//                return new Subscriber<String>() {
//                    @Override public void onCompleted() {
//                        subscriber.onCompleted();
//                    }
//
//                    @Override public void onError(Throwable e) {
//                        subscriber.onError(e);
//                    }
//
//                    @Override public void onNext(String s) {
//                        Log.d("main", "lift");
//                        subscriber.onNext(s);
//                    }
//                };
//            }
//        }).subscribe(new Action1<String>() {
//            @Override public void call(String s) {
//                Log.d("main", "start");
//            }
//        });
    }

    private void flatmap() {
        final List<String[]> list = new ArrayList<>();
        String[] str1 = {"11", "12", "13"};
        String[] str2 = {"21", "22", "23"};
        String[] str3 = {"31", "32", "33"};
        list.add(str1);
        list.add(str2);
        list.add(str3);
        Observable.create(
                new Observable.OnSubscribe<Integer>() {
                    @Override public void call(Subscriber<? super Integer> subscriber) {
                        System.out.println("1>>>>>>:");
                        subscriber.onNext(1);
                        subscriber.onCompleted();
//                subscriber.onError(new RuntimeException());
                    }
                }).flatMap(new Func1<Integer, Observable<String>>() {
            @Override public Observable<String> call(Integer integer) {
                System.out.println("2>>>>>>:");
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override public void call(Subscriber<? super String> subscriber) {
                        System.out.println("2>>>>>>:call");
                        subscriber.onNext("2");
                        subscriber.onError(new RuntimeException());
                    }
                });
            }
        })
                .map(new Func1<String, Integer>() {
                    @Override public Integer call(String s) {
                        return 6;
                    }
                })
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override public Observable<String> call(Integer integer) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override public void call(Subscriber<? super String> subscriber) {
                                subscriber.onNext("6");
                            }
                        });
                    }
                })

                .subscribe(new Subscriber<String>() {
                    @Override public void onCompleted() {
                        System.out.println("3>>>>>>:onCompleted");
                    }

                    @Override public void onError(Throwable e) {
                        System.out.println("3>>>>>>:onError");
                    }

                    @Override public void onNext(String s) {
                        System.out.println("4>>>>>>:onNext" + s);
                    }
                });

                /*.subscribe(new Action1<Integer>() {

                    @Override public void call(Integer integer) {
                        System.out.println("4>>>>>>:" + integer);
                    }
                });*/
                /*.flatMap(new Func1<List<String[]>, Observable<Integer>>() {
                    @Override public Observable<Integer> call(List<String[]> strings) {
                        Log.d("main", "flatMap");
                        return Observable.just(1);
                    }
                })
                .subscribe(new Action1<Object>() {
                    @Override public void call(Object o) {
                        Log.d("main", "call");
                    }
                });*/
//        Observable.from(list)
//                .flatMapIterable(new Func1<String[], Iterable<Integer>>() {
//                    @Override public Iterable<Integer> call(String[] strings) {
//
//
//                        return null;
//                    }
//                })
//                .flatMap(new Func1<Integer, Observable<?>>() {
//                    @Override public Observable<?> call(Integer integer) {
//                        return null;
//                    }
//                })
//                .toList()
//                .subscribe(new Action1<List<Object>>() {
//                    @Override public void call(List<Object> objects) {
//
//                    }
//                })
//
//
//                .subscribe(sub);

//        Observable.create(new Observable.OnSubscribe<List<String[]>>() {
//            @Override public void call(Subscriber<? super List<String[]>> subscriber) {
//                subscriber.onNext(list);
//            }
//        }).flatMap(new Func1<List<String[]>, Observable<?>>() {
//            @Override public Observable<String[]> call(List<String[]> strings) {
//                return Observable.from(strings);
//            }
//        }).subscribe(new Action1<Object>() {
//        });
    }

    private void map() {
        Observable.just(1)
                .map(new Func1<Integer, String>() {
                    @Override public String call(Integer integer) {
                        return integer + "";
                    }
                }).subscribe(sub);
    }

    private void scheduce() {
        Observable.
                create(new Observable.OnSubscribe<String>() {
                    @Override public void call(Subscriber<? super String> subscriber) {
                        Log.d("main", "thread name =" + Thread.currentThread().getName());
                        subscriber.onNext("1");

                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override public void call(String s) {
                        Log.d("main", "thread name = " + Thread.currentThread().getName());
                    }
                });
    }

    private void action() {
        Observable.just("1", "2", null).subscribe(new Action1<String>() {
            @Override public void call(String s) {
                Log.d("main", s);
            }
        }, new Action1<Throwable>() {
            @Override public void call(Throwable throwable) {
                Log.d("main", "error");
            }
        }, new Action0() {

            @Override public void call() {
                Log.d("main", "complete");
            }
        });
    }

    private void from() {
//        String[] ss = {"1","2","3"};
        List<String> ss = new ArrayList<>();
        ss.add("1");
        ss.add("2");
        ss.add("3");
        Observable.from(ss).subscribe(sub);
    }

    private void just() {
        Observable.just("1", "2", "3").subscribe(sub);
    }

    private void base() {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("test");
                subscriber.onCompleted();
            }
        });
        observable.subscribe(sub);
    }

    class B {
    }

}
