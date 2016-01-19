package com.cundong.memory;

import java.util.ArrayList;

public class Constants {

    public static final boolean SHOW_MEMORY_CLEAR = false;

    // TODO
    /**
     * 此处，改为被监控 total Pss 的 processName 列表
     * <p/>
     * 微信：
     * com.tencent.mm
     * com.tencent.mm:TMAssistantDownloadSDKService
     * com.tencent.mm:push
     * com.tencent.mm:cuploader
     * com.tencent.mm:nospace
     * com.tencent.mm:tools
     * com.tencent.mm:sandbox
     * com.tencent.mm:exdevice
     */
    public static final ArrayList<String> PROCESS_NAME_LIST = new ArrayList();

    static {
        PROCESS_NAME_LIST.add("com.tencent.mm");
        PROCESS_NAME_LIST.add("com.tencent.mm:TMAssistantDownloadSDKService");
        PROCESS_NAME_LIST.add("com.tencent.mm:push");
        PROCESS_NAME_LIST.add("com.tencent.mm:cuploader");
        PROCESS_NAME_LIST.add("com.tencent.mm:nospace");
        PROCESS_NAME_LIST.add("com.tencent.mm:tools");
        PROCESS_NAME_LIST.add("com.tencent.mm:sandbox");
        PROCESS_NAME_LIST.add("com.tencent.mm:exdevice");
    }
}