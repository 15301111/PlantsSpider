package praser;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import util.PDFUtil;

/**
 * @author: 张子开
 * @date：2017年4月15日 下午8:30:23
 * @version 1.0
 */

public class HTMLPraser {
	private static String File_DIR = "F:\\PlantsHTML\\";
	private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

	public static List<Entry<String, String>> parse(String fileName) throws IOException {
		Document doc = Jsoup.parse(new File(File_DIR+PDFUtil.getFileNameNoEx(fileName)+".html"), "UTF-8");
		final Elements elements = doc.select("span[style=font-family: TimesNewRomanPS-BoldMT; font-size:9px]");
		final Elements parents = new Elements();
		final List<String> fields = new ArrayList<>();
		final List<String> contents = new ArrayList<>();
		for (Element element : elements) {
			final String text = element.outerHtml().replaceAll(regEx_html, "").trim().replaceAll(" ", "_");
			if (!text.equals("")&&!text.contains(",")) {
				parents.add(element.parent());
				fields.add(text);
			}
		}

		for (Element parent : parents) {
			final List<Node> childNodes = parent.childNodes();
			final StringBuilder builder = new StringBuilder();
			for (int i = 1; i < childNodes.size(); i++) {
				final Node node = childNodes.get(i);
				builder.append(node.outerHtml().replaceAll(regEx_html, ""));
			}
			contents.add(builder.toString());
			builder.delete(0, builder.length());
		}

		final List<Entry<String, String>> result = new ArrayList<>();
		for (int index = 0; index < fields.size(); index++) {
			Entry<String, String> entry = new AbstractMap.SimpleEntry<String, String>(fields.get(index),
					contents.get(index));
			result.add(entry);
		}

		return result;
	}

	public static void main(String[] args) throws IOException {
		final List<Entry<String, String>> parse = HTMLPraser.parse("pg_abam.html");
		for (Entry<String, String> entry : parse) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
	}
}
