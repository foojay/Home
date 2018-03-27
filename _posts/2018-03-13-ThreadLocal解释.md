---
layout: post
title:  "ThreadLocal解释"
date:   2018-03-11 13:27:00
categories: java
excerpt:  ThreadLocal解释
---

* content
{:toc}




### ThreadLocal

    ThreadLocal类用来提供线程内部的局部变量。这种变量在多线程环境下访问(通过get或set方法访问)时能保证各个线程里的变量相对独立于其他线程内的变量。ThreadLocal实例通常来说都是private static类型的，用于关联线程和线程的上下文。可以总结为一句话：ThreadLocal的作用是提供线程内的局部变量，这种变量在线程的生命周期内起作用，减少同一个线程内多个函数或者组件之间一些公共变量的传递的复杂度。举个例子，我出门需要先坐公交再做地铁，这里的坐公交和坐地铁就好比是同一个线程内的两个函数，我就是一个线程，我要完成这两个函数都需要同一个东西：公交卡（北京公交和地铁都使用公交卡），那么我为了不向这两个函数都传递公交卡这个变量（相当于不是一直带着公交卡上路），我可以这么做：将公交卡事先交给一个机构，当我需要刷卡的时候再向这个机构要公交卡（当然每次拿的都是同一张公交卡）。这样就能达到只要是我(同一个线程)需要公交卡，何时何地都能向这个机构要的目的。有人要说了：你可以将公交卡设置为全局变量啊，这样不是也能何时何地都能取公交卡吗？但是如果有很多个人（很多个线程）呢？大家可不能都使用同一张公交卡吧(我们假设公交卡是实名认证的)，这样不就乱套了嘛。现在明白了吧？这就是ThreadLocal设计的初衷：提供线程内部的局部变量，在本线程内随时随地可取，隔离其他线程。




initialValue函数用来设置ThreadLocal的初始值，函数签名如下：    

    protected T initialValue() {
            return null;
        }

该函数在调用get函数的时候会第一次调用，但是如果一开始就调用了set函数，则该函数不会被调用。通常该函数只会被调用一次，除非手动调用了remove函数之后又调用get函数，这种情况下，get函数中还是会调用initialValue函数。该函数是protected类型的，很显然是建议在子类重载该函数的，所以通常该函数都会以匿名内部类的形式被重载，以指定初始值，比如：

    package com.winwill.test;
    /**
     * @author qifuguang
     * @date 15/9/2 00:05
     */
    public class TestThreadLocal {
        private static final ThreadLocal<Integer> value = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return Integer.valueOf(1);
            }
        };
    }


get函数该函数用来获取与当前线程关联的ThreadLocal的值，函数签名如下：public T get()
如果当前线程没有该ThreadLocal的值，则调用initialValue函数获取初始值返回。set函数set函数用来设置当前线程的该ThreadLocal的值，函数签名如下：public void set(T value)
设置当前线程的ThreadLocal的值为value。remove函数remove函数用来将当前线程的ThreadLocal绑定的值删除，函数签名如下：public void remove()
 
在某些情况下需要手动调用该函数，防止内存泄露。代码演示学习了最基本的操作之后，我们用一段代码来演示ThreadLocal的用法，该例子实现下面这个场景：有5个线程，这5个线程都有一个值value，初始值为0，线程运行时用一个循环往value值相加数字。代码实现：


    package com.winwill.test;
    /**
     * @author qifuguang
     * @date 15/9/2 00:05
     */
    public class TestThreadLocal {
        private static final ThreadLocal<Integer> value = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };
        public static void main(String[] args) {
            for (int i = 0; i < 5; i++) {
                new Thread(new MyThread(i)).start();
            }
        }
        static class MyThread implements Runnable {
            private int index;
            public MyThread(int index) {
                this.index = index;
            }
            public void run() {
                System.out.println("线程" + index + "的初始value:" + value.get());
                for (int i = 0; i < 10; i++) {
                    value.set(value.get() + i);
                }
                System.out.println("线程" + index + "的累加value:" + value.get());
            }
        }
    }

执行结果为：
线程0的初始value:0
线程3的初始value:0
线程2的初始value:0
线程2的累加value:45
线程1的初始value:0
线程3的累加value:45
线程0的累加value:45
线程1的累加value:45
线程4的初始value:0
线程4的累加value:45


    总之 ThreadLocal真的不是用来解决对象共享访问问题的，而主要是提供了保持对象的方
    法和避免参数传递的方便的对象访问方式。 
    ThreadLocal自身不会保存这些特定的数据资源，而是由每个线程自己来管理。每个Thread对象都有一个ThreadLocal.ThreadLocalMap类型的名为threadLocals的实例变量，它保存了ThreadLocal设置给这个线程的数据。当通过ThreadLocal的set(data)方法来设置数据的时候，ThreadLocal会首先获取当前线程的引用，然后通过该引用获取当前线程持有的threadLocals，最后，以当前ThreadLocal作为key，将要设置的数据设置到当前线程。
