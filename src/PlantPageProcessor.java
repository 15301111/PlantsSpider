import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.text.AbstractDocument.Content;

import org.apache.commons.lang.StringUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

/**
 * @author: 张子开
 * @date：2017年4月6日 下午11:58:33
 * @version 1.0
 */

public class PlantPageProcessor implements PageProcessor {
	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	// process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
	public void process(Page page) {
		// 部分二：定义如何抽取页面信息，并保存下来
		final String url = page.getUrl().get();
		final Html html = page.getHtml();
		if (url.contains("factSheet")) {
			final List<String> links = html.xpath("//tr[@class='rowon']/td/a").links().all();
			page.addTargetRequests(links);
			final List<String> symbols = html.xpath("//tr[@class='rowon']/th/text()").all();
			final List<String> commonNames = html.$(".rowon td:nth-child(3)").replace("<td>", "").replace("</td>", "").all();
			final List<String> scientificNames = formatScientificNames(html.xpath("//tr[@class='rowon']").$("em").replace("<em>", "").replace("</em>", "").all());
			String sql = "INSERT INTO usdaplants(Symbol,Scientific_Name,Common_Name) VALUES (?,?,?)";
			try {
				final PreparedStatement stat = JDBCUtil.getConnection().prepareStatement(sql);
				for (int i = 0; i < symbols.size(); i++) {
					stat.setString(1, symbols.get(i));
					stat.setString(2, scientificNames.get(i));
					stat.setString(3, commonNames.get(i));
					stat.execute();
					System.out.println("第" + i + "条记录插入成功");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (url.contains("profile")) {
			final List<String> contents = html.xpath("//table[@class='bordered']").$("[valign=top]").replace(regEx_html, "").replace("&nbsp;", "").all();
			String sql = "UPDATE usdaplants SET m_Group=?,Family=?,Duration=?,Growth_Habit=?,Native_Status=? WHERE Symbol=?";
			PreparedStatement statement;
			try {
				statement = JDBCUtil.getConnection().prepareStatement(sql);
				statement.setString(1, contents.get(3).trim());
				statement.setString(2, contents.get(5).trim());
				statement.setString(3, contents.get(7).trim());
				statement.setString(4, contents.get(9).trim());
				statement.setString(5, contents.get(11).trim());
				statement.setString(6, contents.get(1).trim());
				statement.execute();
				System.out.println("更新成功");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if(url.contains("charProfile")){
			
		}else if(url.contains(".pdf")){
			 final String fileName = PDFUtil.DownLoadFile(url);
			 PDFUtil.convertToHtml(fileName);
			 try {
				final List<Entry<String, String>> records = HTMLPraser.parse(fileName);
				for (Entry<String, String> entry : records) {
					final String fileNameNoEx = PDFUtil.getFileNameNoEx(fileName);
					int dot = fileNameNoEx.lastIndexOf('_');
					String pre = "";
					String symbol="";
					if ((dot > -1) && (dot < (fileNameNoEx.length()))) {
						pre = fileNameNoEx.substring(0, dot);
						symbol = StringUtils.upperCase(fileNameNoEx.substring(dot+1, fileNameNoEx.length()));
					}
					final String field = pre+"_"+entry.getKey();
					final boolean isFieldExist = JDBCUtil.isFieldExist(field);
					if(!isFieldExist){
						JDBCUtil.createField(field);
					}
					String sql = "UPDATE usdaplants SET "+field+"=? WHERE Symbol=?";
					final PreparedStatement prepareStatement = JDBCUtil.getConnection().prepareStatement(sql);
					prepareStatement.setString(1,entry.getValue());
					prepareStatement.setString(2, symbol);
					prepareStatement.execute();
					System.out.println("更新成功");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// 部分三：从页面发现后续的url地址来抓取

	}

	private List<String> formatScientificNames(final List<String> scientificNames) {
		StringBuilder builder = new StringBuilder();
		List<String> out = new ArrayList<>();
		boolean isUpperExist = false;
		for (String string : scientificNames) {
			final boolean isUpperCase = Character.isUpperCase(string.toCharArray()[0]);
			if (isUpperCase) {
				if (isUpperExist) {
					builder.delete(builder.length() - 1, builder.length());
					out.add(builder.toString());
					builder.delete(0, builder.length());
					builder.append(string + " ");
				} else {
					isUpperExist = true;
					builder.append(string + " ");
				}
			} else {
				builder.append(string + " ");
			}
		}
		builder.delete(builder.length() - 1, builder.length());
		out.add(builder.toString());
		return out;
	}

	public static void main(String[] args) {
		Spider.create(new PlantPageProcessor()).addUrl("https://plants.usda.gov/java/factSheet").thread(1)
				.run();
	}
}
