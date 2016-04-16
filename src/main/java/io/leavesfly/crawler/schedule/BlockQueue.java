package io.leavesfly.crawler.schedule;

import java.util.LinkedList;
import java.util.List;

public class BlockQueue {
	private LinkedList<String> queue = new LinkedList<String>();

	public void enQueue(List<String> urlList) {
		synchronized (queue) {
			for (int i = 0; i < urlList.size(); i++) {
				queue.addLast(urlList.get(i));
			}
		}
	}

	public String outQueue() {
		String result;
		synchronized (queue) {
			result = queue.pollFirst();
		}
		return result;
	}

}
