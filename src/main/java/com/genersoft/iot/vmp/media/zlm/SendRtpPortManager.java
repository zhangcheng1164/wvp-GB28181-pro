package com.genersoft.iot.vmp.media.zlm;

import com.genersoft.iot.vmp.common.VideoManagerConstants;
import com.genersoft.iot.vmp.conf.UserSetting;
import com.genersoft.iot.vmp.gb28181.bean.SendRtpItem;
import com.genersoft.iot.vmp.media.zlm.dto.MediaServerItem;
import com.genersoft.iot.vmp.utils.redis.RedisUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SendRtpPortManager {

    private final static Logger logger = LoggerFactory.getLogger(SendRtpPortManager.class);

    @Autowired
    private UserSetting userSetting;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private final String KEY = "VM_MEDIA_SEND_RTP_PORT_";

    public synchronized int getNextPort(MediaServerItem mediaServer) {
        if (mediaServer == null) {
            logger.warn("[发送端口管理] 参数错误，mediaServer为NULL");
            return -1;
        }
        String sendIndexKey = KEY + userSetting.getServerId() + "_" +  mediaServer.getId();
        String key = VideoManagerConstants.PLATFORM_SEND_RTP_INFO_PREFIX
                + userSetting.getServerId() + "_*";
        List<Object> queryResult = RedisUtil.scan(redisTemplate, key);
        Map<Integer, SendRtpItem> sendRtpItemMap = new HashMap<>();

        for (Object o : queryResult) {
            SendRtpItem sendRtpItem = (SendRtpItem) redisTemplate.opsForValue().get(o);
            if (sendRtpItem != null) {
                sendRtpItemMap.put(sendRtpItem.getLocalPort(), sendRtpItem);
            }
        }
        String sendRtpPortRange = mediaServer.getSendRtpPortRange();
        int startPort;
        int endPort;
        if (sendRtpPortRange != null) {
            String[] portArray = sendRtpPortRange.split(",");
            if (portArray.length != 2 || !NumberUtils.isParsable(portArray[0]) || !NumberUtils.isParsable(portArray[1])) {
                logger.warn("{}发送端口配置格式错误，自动使用50000-60000作为端口范围", mediaServer.getId());
                startPort = 50000;
                endPort = 60000;
            }else {
                if ( Integer.parseInt(portArray[1]) - Integer.parseInt(portArray[0]) < 1) {
                    logger.warn("{}发送端口配置错误,结束端口至少比开始端口大一，自动使用50000-60000作为端口范围", mediaServer.getId());
                    startPort = 50000;
                    endPort = 60000;
                }else {
                    startPort = Integer.parseInt(portArray[0]);
                    endPort = Integer.parseInt(portArray[1]);
                }
            }
        }else {
            logger.warn("{}未设置发送端口默认值，自动使用50000-60000作为端口范围", mediaServer.getId());
            startPort = 50000;
            endPort = 60000;
        }
        if (redisTemplate == null || redisTemplate.getConnectionFactory() == null) {
            logger.warn("{}获取redis连接信息失败", mediaServer.getId());
            return -1;
        }
//        RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(sendIndexKey , redisTemplate.getConnectionFactory());
//        return redisAtomicInteger.getAndUpdate((current)->{
//            return getPort(current, startPort, endPort, checkPort-> !sendRtpItemMap.containsKey(checkPort));
//        });
        return getSendPort(startPort, endPort, sendIndexKey, sendRtpItemMap);
    }

    /**
     * zhangcheng
     * 之前存在无法同时播放两路视频的问题？
     * 分析日志中报错： "RTP推流失败: bindUdpSock failed on port:30205, err:address already in use, 参数：...."
     * 在Redis中，存放着一个下一次推流会使用的其实端口号，key为 VM_MEDIA_SEND_RTP_PORT_000000_[MediaServiceId]
     * 还存在很多key格式为 VMP_PLATFORM_SEND_RTP_INFO_000000_4560e442-6508-4a42-9c7e-526a5a92d742_37010100002000108213_37010000001311000002_37010100002000108211_37010000001311000002_f0a9129d1c3640411c7c1ba58998704e@192.168.108.213
     * 的值，它们对应着每一个推流时的信息，其中就包括本地推流端口 和 上一级平台的收流端口。
     * 每当下一次需要推流时，就会根据上面这些值找到下一个可用的本地端口，原本的逻辑大体的逻辑是，如果本次推流使用的端口为60200，那么缓存在redis中准备给下一次使用的值为 60200，
     * 当下一次推流时，大概率就是选择这个60200作为本地推流端口，并将redis中的值更新为 60201。
     * 但是不知什么原因，此时60201总是被占用了，这里跟ZLM的通信逻辑有关，暂不知原因？ 这样就导致下一次推流时，发送了 address already in use 的情况。
     * 
     * 为了暂时绕过这个问题，采取了一种比较笨的方式，就是将缓存的端口每次多加几次，比如加三次。这样假设本次使用的时60200，那么下次使用的就是60203，就不容易出问题了。
     */
    private synchronized int getSendPort(int startPort, int endPort, String sendIndexKey, Map<Integer, SendRtpItem> sendRtpItemMap){
        RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(sendIndexKey , redisTemplate.getConnectionFactory());
        if (redisAtomicInteger.get() < startPort) {
            redisAtomicInteger.set(startPort);
            return startPort;
        }else {
        	// zhangcheng 
        	logger.info("======连续将port自增四次之前========={}====", redisAtomicInteger.get());
        	redisAtomicInteger.incrementAndGet(); // zhangcheng 连续三次自增是我加的
        	redisAtomicInteger.incrementAndGet();
        	redisAtomicInteger.incrementAndGet();
        	
        	int port = redisAtomicInteger.getAndIncrement();
        	logger.info("======连续将port自增四次之后========={}====", redisAtomicInteger.get());

            if (port > endPort) {
                redisAtomicInteger.set(startPort);
                // zhangcheng 只要此次选中的port，在已经使用的端口前或者后，都重新计算
                if (sendRtpItemMap.containsKey(startPort) || sendRtpItemMap.containsKey(startPort + 1) || sendRtpItemMap.containsKey(startPort - 1) ) {
                    return getSendPort(startPort, endPort, sendIndexKey, sendRtpItemMap);
                }else {
                	logger.info("=======最终选定的port {} ====", port);
                    return startPort;
                }
            }
            
            // zhangcheng 只要此次选中的port，在已经使用的端口前或者后，都重新计算
            if (sendRtpItemMap.containsKey(port) || sendRtpItemMap.containsKey(port + 1) || sendRtpItemMap.containsKey(port - 1)) {
                return getSendPort(startPort, endPort, sendIndexKey, sendRtpItemMap);
            }else {
            	logger.info("=======最终选定的port {} ====", port);
                return port;
            }
        }

    }

    interface CheckPortCallback{
        boolean check(int port);
    }

    private int getPort(int current, int start, int end, CheckPortCallback checkPortCallback) {
        if (current <= 0) {
            if (start%2 == 0) {
                current = start;
            }else {
                current = start + 1;
            }
        }else {
            current += 2;
            if (current > end) {
                if (start%2 == 0) {
                    current = start;
                }else {
                    current = start + 1;
                }
            }
        }
        if (!checkPortCallback.check(current)) {
            return getPort(current + 2, start, end, checkPortCallback);
        }
        return current;
    }
}
