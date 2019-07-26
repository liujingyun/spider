package com.xinyan.trust.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @ClassName RedisMessageListener
 * @Description 消息订阅的处理
 * @Author jingyun_liu
 * @Date 2019/7/26 15:28
 * @Version V1.0
 **/
@Component
public class RedisMessageListener implements MessageListener {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Override
    public void onMessage(Message message, byte[] bytes) {

        byte[] body = message.getBody();
        // 请使用valueSerializer
        byte[] channel = message.getChannel();
        // 请参考配置文件，本例中key，value的序列化方式均为string。
        // 其中key必须为stringSerializer。和redisTemplate.convertAndSend对应
        String msgContent = (String) redisTemplate.getValueSerializer().deserialize(body);
        String topic =  redisTemplate.getStringSerializer().deserialize(channel);
        System.out.println("topic:" + topic + "msgContent:" + msgContent);
    }
}
