package com.javayh.austin.handler.deduplication.limit;


import cn.hutool.core.util.IdUtil;
import com.javayh.austin.common.domain.TaskInfo;
import com.javayh.austin.handler.deduplication.DeduplicationParam;
import com.javayh.austin.handler.deduplication.service.AbstractDeduplicationService;
import com.javayh.austin.support.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service(value = "SlideWindowLimitService")
public class SlideWindowLimitService extends AbstractLimitService {

    private static final String LIMIT_TAG = "SW_";
    
    @Autowired
    private RedisUtils redisUtils;
    
    private DefaultRedisScript<Long> redisScript;
    
    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
    }

    /**
     * 
     * @param service  去重器对象
     * @param taskInfo
     * @param param    去重参数
     * @return
     */
    @Override
    public Set<String> limitFilter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param) {
        
        Set<String> filterReceiver = new HashSet<>(taskInfo.getReceiver().size());
        long nowTime = System.currentTimeMillis();
        for (String receiver : taskInfo.getReceiver()) {
            String key = LIMIT_TAG + deduplicationSingleKey(service, taskInfo, receiver);
            String scoreValue = String.valueOf(IdUtil.getSnowflake().nextId());
            String score = String.valueOf(nowTime);

            final Boolean result = redisUtils.execLimitLua(redisScript, Collections.singletonList(key),
                    String.valueOf(param.getDeduplicationTime() * 1000), score, String.valueOf(param.getCountNum()), scoreValue);
            if (Boolean.TRUE.equals(result)) {
                filterReceiver.add(receiver);
            }
        }
        return filterReceiver;
    }
}
