package com.cundong.memory.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;
import android.os.Debug.MemoryInfo;
import android.text.TextUtils;
import android.util.Log;

import com.cundong.memory.App;
import com.cundong.memory.Constants;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

/**
 * 类说明： 	内存相关数据获取工具类
 *
 * @date 	2015-4-18
 * @version 1.0
 */
public class MemoryUtil {

	/**
	 * getTotalPss，5.0+使用开源的android-processes解决方案，5.0以下使用系统api
	 *
	 * @param processNameList
	 * @return
	 */
	public static HashMap<String, Long> getTotalPss(ArrayList<String> processNameList) {

		HashMap<String, Long> resultMap = new HashMap<>();
		ActivityManager activityMgr = (ActivityManager) App.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);

		if (Build.VERSION.SDK_INT >= 21) {
			List<AndroidAppProcess> list = ProcessManager.getRunningAppProcesses();

			if (list != null) {
				for (AndroidAppProcess processInfo : list) {

					if (processNameList.contains(processInfo.name)) {
						int pid = processInfo.pid;
						MemoryInfo[] memoryInfos = activityMgr.getProcessMemoryInfo(new int[]{pid});

						MemoryInfo memoryInfo = memoryInfos[0];
						int totalPss = memoryInfo.getTotalPss();

						resultMap.put(processInfo.name, new Long(totalPss));
					}
				}
			}
		} else {
			List<RunningAppProcessInfo> list = activityMgr.getRunningAppProcesses();
			if (list != null) {
				for (RunningAppProcessInfo processInfo : list) {

					if (Constants.PROCESS_NAME_LIST.contains(processInfo.processName)) {
						int pid = processInfo.pid;
						MemoryInfo[] memoryInfos = activityMgr.getProcessMemoryInfo(new int[] { pid });

						MemoryInfo memoryInfo = memoryInfos[0];
						int totalPss = memoryInfo.getTotalPss();

						resultMap.put(processInfo.processName, new Long(totalPss));
					}
				}
			}
		}

		return resultMap;
	}

	/**
	 * getTotalPss，5.0+使用开源的android-processes解决方案，5.0以下使用系统api
	 *
	 * @param processName
	 * @return
	 */
	public static long getTotalPss(String processName) {

		ActivityManager activityMgr = (ActivityManager) App.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);

		if (Build.VERSION.SDK_INT >= 21) {
			List<AndroidAppProcess> list = ProcessManager.getRunningAppProcesses();

			if (list != null) {
				for (AndroidAppProcess processInfo : list) {
					if (processInfo.name.equals(processName)) {
						int pid = processInfo.pid;
						MemoryInfo[] memoryInfos = activityMgr.getProcessMemoryInfo(new int[]{pid});

						MemoryInfo memoryInfo = memoryInfos[0];
						int totalPss = memoryInfo.getTotalPss();

						return totalPss;
					}
				}
			}
		} else {
			List<RunningAppProcessInfo> list = activityMgr.getRunningAppProcesses();
			if (list != null) {
				for (RunningAppProcessInfo processInfo : list) {

					if (processInfo.processName.equals(processName)) {
						int pid = processInfo.pid;
						MemoryInfo[] memoryInfos = activityMgr.getProcessMemoryInfo(new int[] { pid });

						MemoryInfo memoryInfo = memoryInfos[0];
						int totalPss = memoryInfo.getTotalPss();

						return totalPss;
					}
				}
			}
		}

		return -1;
	}

	/**
	 * 计算已使用内存的百分比
	 * 
	 */
	public static String getUsedPercentValue() {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine
					.indexOf("MemTotal:"));
			br.close();
			long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll(
					"\\D+", ""));
			long availableSize = getAvailableMemory() / 1024;
			int percent = (int) ((totalMemorySize - availableSize)
					/ (float) totalMemorySize * 100);
			return percent + "%";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取可用内存
	 * 
	 */
	public static long getAvailableMemory() {

		ActivityManager activityManager = (ActivityManager) App.getAppContext()
				.getSystemService(Context.ACTIVITY_SERVICE);

		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(mi);

		return mi.availMem;
	}

	public static void clearMemory(Context context) {
		ActivityManager activityManger = (ActivityManager) App.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);


		if (Build.VERSION.SDK_INT >= 21) {
			List<AndroidAppProcess> list = ProcessManager.getRunningAppProcesses();

			if (list != null) {
				for (AndroidAppProcess processInfo : list) {

					int myPid = android.os.Process.myPid();

					if(myPid == processInfo.pid) {
						continue;
					}

					if (!processInfo.foreground) {
						activityManger.killBackgroundProcesses(processInfo.getPackageName());
					}
				}
			}
		} else {
			List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();

			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					ActivityManager.RunningAppProcessInfo appProcessInfo = list.get(i);

					String[] pkgList = appProcessInfo.pkgList;

					if (appProcessInfo.importance >= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

						for (int j = 0; j < pkgList.length; j++) {

							if (pkgList[j].equals(context.getPackageName())) {
								continue;
							}

							activityManger.killBackgroundProcesses(pkgList[j]);
						}
					}
				}
			}
		}
	}
}