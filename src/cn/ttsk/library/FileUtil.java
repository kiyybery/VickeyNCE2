package cn.ttsk.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileUtil {
	public static long getSize(File path) {
		long size = 0;
		if (!path.exists()) {
			return size;
		}
		if (path.isDirectory()) {
			String[] list = path.list();
			for (String one : list) {
				size += getSize(new File(path, one));
			}
		} else {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(path);
				size += fis.available();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return size;
	}
	
	public static void delete(File path, boolean delSelf) {
		if (!path.exists()) {
			return;
		}
		if (path.isDirectory()) {
			String[] list = path.list();
			for (String one : list) {
				delete(new File(path, one), true);
			}
		}
		if (delSelf) {
			path.delete();
		}
	}
}
