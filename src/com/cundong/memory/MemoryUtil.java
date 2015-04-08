package com.cundong.memory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;
import android.os.Debug.MemoryInfo;

public class MemoryUtil {

	/**
	 * getTotalPss
	 * 
	 * @param context
	 * @return
	 */
	public static long getTotalPss(Context context, String packageName) {

		ActivityManager activityMgr = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = activityMgr.getRunningAppProcesses();

		if (list != null) {
			for (RunningAppProcessInfo processInfo : list) {
				if (processInfo.processName.equals(packageName)) {
					int pid = processInfo.pid;
					MemoryInfo[] memoryInfos = activityMgr
							.getProcessMemoryInfo(new int[] { pid });
					
					MemoryInfo memoryInfo = memoryInfos[0];
					int totalPss = memoryInfo.getTotalPss();
					
					return totalPss;
				}
			}
		}

		return -1;
	}
	
	/**
	 * 计算已使用内存的百分比
	 * 
	 */
	public static String getUsedPercentValue(Context context) {
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
			long availableSize = getAvailableMemory(context) / 1024;
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
	public static long getAvailableMemory(Context context) {

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(mi);

		return mi.availMem;
	}
	
	@SuppressWarnings("deprecation")
	public static void clearMemory(Context context) {
		ActivityManager activityManger = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list = activityManger
				.getRunningAppProcesses();
		if (list != null)
			for (int i = 0; i < list.size(); i++) {
				ActivityManager.RunningAppProcessInfo apinfo = list.get(i);

				String[] pkgList = apinfo.pkgList;
				
				if (apinfo.importance >= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

					for (int j = 0; j < pkgList.length; j++) {

						if (pkgList[j].equals(context.getPackageName())) {
							continue;
						}

						if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
							activityManger.restartPackage(pkgList[j]);
						} else {
							activityManger.killBackgroundProcesses(pkgList[j]);
						}
					}
				}
			}
	}
}