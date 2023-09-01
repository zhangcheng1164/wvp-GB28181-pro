package com.genersoft.iot.vmp.common;

/**
 * 标识invite消息发出后的各个状态，
 * 收到ok钱停止invite发送cancel，
 * 收到200ok后发送BYE停止invite
 */
/**
 * @author zhangcheng
 * 
 * 存在在redis中的 VMP_INVITE:PLAY:[deviceId]:[channelId]:[streamId]:[ssrc] 中，
 * 收到下级平台返回 Invite 200 前 status为ready
 * 收到下级平台返回 Invite 200 后 staus为ok
 */
public enum InviteSessionStatus {
    ready, // redis中保存为0，初始化后 -- invite 200 OK 返回前
    ok,    // redis中保存为1，invite 200 OK 返回后
}
