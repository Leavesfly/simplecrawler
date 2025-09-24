package io.leavesfly.crawler.schedule;

import io.leavesfly.crawler.constant.CommConstant;

/**
 * @author yefei.yf
 */
public class URLMutilQueue {
    private BlockQueue[] urlMutilQueueArray;
    private static URLMutilQueue urlMutilQueue;

    private URLMutilQueue() {
        urlMutilQueueArray = new BlockQueue[CommConstant.URL_QUEUE_NUM];
        for (int i = 0; i < urlMutilQueueArray.length; i++) {
            urlMutilQueueArray[i] = new BlockQueue();
        }
    }

    public static BlockQueue getURLQueueInstace() {
        if (urlMutilQueue == null) {
            synchronized (urlMutilQueue) {
                if (urlMutilQueue == null) {
                    urlMutilQueue = new URLMutilQueue();
                    return urlMutilQueue.getUrlMutilQueueArray()[0];
                }
            }
        } else {
            return urlMutilQueue.getUrlMutilQueueArray()[0];
        }
        return null;
    }

    public BlockQueue[] getUrlMutilQueueArray() {
        return urlMutilQueueArray;
    }

    public void setUrlMutilQueueArray(BlockQueue[] urlMutilQueueArray) {
        this.urlMutilQueueArray = urlMutilQueueArray;
    }

}
