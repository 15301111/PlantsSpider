# PlantsSpider

PlantsSpider是用java实现的爬虫，爬取https://plants.usda.gov/java/factSheet 这个站点的植物信息，并将其存入MySql数据库中。

## V1实现的功能：
- - - - - - 
  爬取factsheet页面的列表，每一项的profile、、character页面。<br>
  下载pdf、转换为HTML格式。<br>
  解析HTML文件，提取黑体字作为字段名。<br>
  自动建表，增加字段。<br>
    
## 接下来要实现的目标：<br>
- - - - - - 
  受王伟军同学的影响，决定做一个展示结果的网页。<br>
  增加异常情况的处理，使程序更加健壮。<br>
  重构代码，让代码结构更清晰一些。<br>
  增加断点续爬功能。<br>
  将开发时的测试用例整合到一起，实现一键测试。<br>

## 使用到的第三库，感谢这些开源软的作者<br>
- - - - - - 
  [WebMagic](http://blog.csdn.net/guodongxiaren) WebMagic是一个使用java实现的爬虫框架<br>
  [JSoup](https://jsoup.org/) JSoup用于解析HTML文本<br>
  [PDFMiner](http://www.unixuser.org/~euske/python/pdfminer/) 将PDF文件转换为HTML<br>
