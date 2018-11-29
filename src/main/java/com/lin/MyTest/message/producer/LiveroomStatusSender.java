package com.lin.MyTest.message.producer;

import com.lin.MyTest.service.LiveroomService;
import com.lin.MyTest.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class LiveroomStatusSender {

	@Autowired
	private Sender sender;

	@Autowired
	private LiveroomService liveroomService;

	@Value("${kafka.topic.liveroom-action-topic}")
	private String liveroomActionTopic;

	private static final int QUEUE_SIZE = 16 * 1024;
	private static BlockingQueue<Map<String, Object>> liveroomActionBlockingQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);

	@PostConstruct
	private void init() {
		ExecutorService messageSender = Executors.newSingleThreadExecutor();

		messageSender.execute(() -> {
            while (true) {
                try {
                    Map<String, Object> map = liveroomActionBlockingQueue.take();
                    long key = (Long) map.get("liveroom_id");
                    String value = JsonUtils.obj2JsonStr(map);
                    sender.send(liveroomActionTopic, String.valueOf(key), value);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
	}

	public void sendLiveroomActionMessage(long liveroomId) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("liveroom_id", liveroomId);
		resultMap.put("t", System.currentTimeMillis());

		liveroomActionBlockingQueue.offer(resultMap);

	}
}
