package com.genersoft.iot.vmp.gb28181.session;

import com.genersoft.iot.vmp.conf.SipConfig;
import com.genersoft.iot.vmp.conf.UserSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ssrc使用
 */
@Component
public class SSRCFactory {

    /**
     * 播流最大并发个数
     */
    private static final Integer MAX_STREAM_COUNT = 10000;

    /**
     * 播流最大并发个数
     */
    private static final String SSRC_INFO_KEY = "VMP_SSRC_INFO_";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SipConfig sipConfig;

    @Autowired
    private UserSetting userSetting;


    public void initMediaServerSSRC(String mediaServerId, Set<String> usedSet) {
        String ssrcPrefix = sipConfig.getDomain().substring(3, 8);
        String redisKey = SSRC_INFO_KEY + userSetting.getServerId() + "_" + mediaServerId;
        List<String> ssrcList = new ArrayList<>();
        for (int i = 1; i < MAX_STREAM_COUNT; i++) {
            String ssrc = String.format("%s%04d", ssrcPrefix, i);

            if (null == usedSet || !usedSet.contains(ssrc)) {
                ssrcList.add(ssrc);

            }
        }
        if (redisTemplate.opsForSet().size(redisKey) != null) {
            redisTemplate.delete(redisKey);
        }
        redisTemplate.opsForSet().add(redisKey, ssrcList.toArray(new String[0]));
    }


    /**
     * 获取视频预览的SSRC值,第一位固定为0
     *
     * @return ssrc
     */
    public String getPlaySsrc(String mediaServerId) {
        return "0" + getSN(mediaServerId);
    }

    /**
     * 获取录像回放的SSRC值,第一位固定为1
     */
    public String getPlayBackSsrc(String mediaServerId) {
        return "1" + getSN(mediaServerId);
    }

    /**
     * 释放ssrc，主要用完的ssrc一定要释放，否则会耗尽
     *
     * @param ssrc 需要重置的ssrc
     */
    public void releaseSsrc(String mediaServerId, String ssrc) {
        if (ssrc == null) {
            return;
        }
        String sn = ssrc.substring(1);
        String redisKey = SSRC_INFO_KEY + userSetting.getServerId() + "_" + mediaServerId;
        redisTemplate.opsForSet().add(redisKey, sn);
    }
    
    /**
     * 从池中删除某个特定的ssrc
     * @param mediaServerId
     * @param ssrc
     * 
     * 为什么需要从池中删除 ssrc?
     * 当修正SSRC时，真正使用的SSRC是 下一级平台通过Invite 200 OK 返回的，此时并没有从redis中的 SSRC池中删除。需要手动删除，所以新增了这个方法。
     * 
     * SSRC 池保存在redis的 VMP_SSRC_INFO_000000_[serviceMeidaId]中
     */
    public void removeSsrc(String mediaServerId, String ssrc) {
        if (ssrc == null) {
            return;
        }
        String sn = ssrc.substring(1);
        String redisKey = SSRC_INFO_KEY + userSetting.getServerId() + "_" + mediaServerId;
        redisTemplate.opsForSet().remove(redisKey, sn);
    }

    /**
     * 获取后四位数SN,随机数
     */
    private String getSN(String mediaServerId) {
        String sn = null;
        String redisKey = SSRC_INFO_KEY + userSetting.getServerId() + "_" + mediaServerId;
        Long size = redisTemplate.opsForSet().size(redisKey);
        if (size == null || size == 0) {
            throw new RuntimeException("ssrc已经用完");
        } else {
            // 在集合中移除并返回一个随机成员。
            sn = (String) redisTemplate.opsForSet().pop(redisKey);
            redisTemplate.opsForSet().remove(redisKey, sn);
        }
        return sn;
    }

    /**
     * 重置一个流媒体服务的所有ssrc
     *
     * @param mediaServerId 流媒体服务ID
     */
    public void reset(String mediaServerId) {
        this.initMediaServerSSRC(mediaServerId, null);
    }

    /**
     * 是否已经存在了某个MediaServer的SSRC信息
     *
     * @param mediaServerId 流媒体服务ID
     */
    public boolean hasMediaServerSSRC(String mediaServerId) {
        String redisKey = SSRC_INFO_KEY + userSetting.getServerId() + "_" + mediaServerId;
        return redisTemplate.opsForSet().members(redisKey) != null;
    }

}
