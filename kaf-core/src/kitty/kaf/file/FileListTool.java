package kitty.kaf.file;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kitty.kaf.helper.StringHelper;

public class FileListTool {

	public static void main(String[] args) {
		File f = new File(args[0]);
		ArrayList<File> list = new ArrayList<File>();
		listFiles(f, list, new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return true;
			}
		});
		Collections.sort(list, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return (int) (o2.lastModified() - o1.lastModified());
			}
		});
		System.out.println("===================================");
		for (File file : list) {
			if (!file.isHidden())
				System.out.println(StringHelper.formatDateTime(file.lastModified(), "yyyy-MM-dd HH:mm:ss") + " "
						+ file.getAbsolutePath());
		}
		System.out.println("===================================");
	}

	static void listFiles(File parent, List<File> ls, FileFilter filter) {
		for (File f : parent.listFiles(filter)) {
			if (f.isHidden())
				continue;
			if (f.isFile()) {
				ls.add(f);
			} else
				listFiles(f, ls, filter);
		}
	}
}
