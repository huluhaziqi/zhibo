package com.lin.MyTest.message.producer;

import com.lin.MyTest.service.TestService;
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
public class TestStatusSender {

	@Autowired
	private Sender sender;

	@Autowired
	private TestService TestService;

	@Value("${kafka.topic.Test-action-topic}")
	private String TestActionTopic;

	private static final int QUEUE_SIZE = 16 * 1024;
	private static BlockingQueue<Map<String, Object>> TestActionBlockingQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);

	@PostConstruct
	private void init() {
		ExecutorService messageSender = Executors.newSingleThreadExecutor();

		messageSender.execute(() -> {
            while (true) {
                try {
                    Map<String, Object> map = TestActionBlockingQueue.take();
                    long key = (Long) map.get("Test_id");
                    String value = JsonUtils.obj2JsonStr(map);
                    sender.send(TestActionTopic, String.valueOf(key), value);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
	}

	public void sendTestActionMessage(long TestId) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("Test_id", TestId);
		resultMap.put("t", System.currentTimeMillis());

		TestActionBlockingQueue.offer(resultMap);

	}
}
