package util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 * @author: 张子开
 * @date：2017年4月8日 上午11:48:37
 * @version 1.0
 */

public class PDFUtil {
	private static String INPUT_PATH = "F:\\PlantsPDF\\";
	private static String OUTPUT_PATH = "F:\\PlantsHTML\\";

	public static void convertToHtml(final String fileName) {
		final String fileNameNoEx = getFileNameNoEx(fileName);
		if (new File(INPUT_PATH + fileNameNoEx + ".pdf").isFile()) {
			if (!new File(OUTPUT_PATH + ".html").exists()) {
				try {
					String cmd = "cmd /c  pdf2txt.py -t html -o" + OUTPUT_PATH + fileNameNoEx + ".html " + INPUT_PATH
							+ fileNameNoEx + ".pdf";
					final Process exec = Runtime.getRuntime().exec(cmd);
					exec.waitFor();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String DownLoadFile(String url) {
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		StringTokenizer tokenizer = new StringTokenizer(url, "/");
		String nextToken = "";
		while (tokenizer.hasMoreTokens()) {
			nextToken = tokenizer.nextToken();
			if (nextToken.contains(".pdf")) {
				System.out.println(nextToken);
			}
		}
		try {
			final HttpResponse response = client.execute(get);
			InputStream inStream = response.getEntity().getContent();
			File file = new File("F://PlantsPDF//" + nextToken);
			if (file.exists()){
				System.out.println(nextToken+"已存在");
				return nextToken;
			}
			file.createNewFile();
			FileOutputStream outStream = new FileOutputStream(file);
			while (true) {
				byte[] bytes = new byte[1024 * 1000];
				int k = inStream.read(bytes);

				if (k >= 0) {
					outStream.write(bytes, 0, k);
					outStream.flush();
				} else
					break;
			}
			inStream.close();
			outStream.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nextToken;
	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static void main(String[] args) throws IOException {
		final File root = new File(INPUT_PATH);
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			for (File file : files) {
				final String fileName = getFileNameNoEx(file.getName());
				System.out.println(fileName);
				convertToHtml(getFileNameNoEx(fileName));
			}
		}
	}
}
