package com.taotao.web.evictor;

import org.apache.http.conn.HttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;

public class IdleConnectionEvictor extends Thread {

        @Autowired
        private  HttpClientConnectionManager connMgr;

        private volatile boolean shutdown;
        // 在配置文件中添加 init-method配置可以实现以下功能，这两种方法都 可以。
//        public IdleConnectionEvictor() {
//            super.start();
//        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(1000*3600);
                        // 关闭失效的连接
                        connMgr.closeExpiredConnections();
                    }
                }
            } catch (InterruptedException ex) {
                // 结束
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }
