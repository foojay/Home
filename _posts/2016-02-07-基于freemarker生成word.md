---
layout: post
title:  "基于freemarker生成word"
date:   2016-02-07 11:27:00
categories: java
excerpt:  基于freemarker生成word
---

* content
{:toc}


## 依赖包

        <!-- freemarker -->
        <dependency>
              <groupId>org.freemarker</groupId>
              <artifactId>freemarker</artifactId>
              <version>2.3.23</version>
        </dependency>


## applicationContext.xml

      <!--freeMarke-->
    <bean id="freeMarker" class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer">     
      <property name="templateLoaderPath" value="WEB-INF/template/"/>  
      <property name="freemarkerSettings">
          <props>     
              <prop key="template_update_delay">1800</prop>
              <prop key="default_encoding">UTF-8</prop>
              <prop key="locale">zh_CN</prop>
          </props>     
      </property>     
     </bean>



## 思路

MS-Office下的word在2007以后后缀基本是以.docx结尾，是用一种xml格式的在存储数据（.

doc是用二进制存储数据），这就为使用freemarker提供的条件，如果把template.docx重

命名成template.zip，再用word一样是可以打开的，如果有WinRAR之类的压缩工具打开会

发现如下目录结构

!["图片"](http://ojefm7q7h.bkt.clouddn.com/zip.jpg)

## 具体操作


1.处理模版对应的docx

!["图片"](http://ojefm7q7h.bkt.clouddn.com/docx.jpg)

2.将test.docx重命名test.zip,将document.xml copy 出来 ，打开 document.xml

!["图片"](http://ojefm7q7h.bkt.clouddn.com/xmldocx.jpg)


3.java代码如下

        try {
            Template template=fr.getConfiguration().getTemplate("document.xml");
            
             /** 指定输出word文件的路径 **/
             String outFilePath = "C:/Program Files (x86)/OpenOffice 4/data.xml";
             File docFile = new File(outFilePath);
             FileOutputStream fos = new FileOutputStream(docFile);
             OutputStreamWriter oWriter = new OutputStreamWriter(fos);
             Writer out = new BufferedWriter(new OutputStreamWriter(fos),10240);
             Map<String,String> dataMap = new HashMap<String, String>();
                /** 在ftl文件中有${textDeal}这个标签**/
                dataMap.put("id","黄浦江吴彦祖");
             try {
                template.process(dataMap,out);
            } catch (TemplateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
             if(out != null){
                 out.close();
             }
             ZipInputStream zipInputStream = wrapZipInputStream(new FileInputStream(new File("C:/Program Files (x86)/OpenOffice 4/demo.zip")));
                ZipOutputStream zipOutputStream = wrapZipOutputStream(new FileOutputStream(new File("C:/Program Files (x86)/OpenOffice 4/demo.docx")));
                String itemname = "word/document.xml";
                replaceItem(zipInputStream, zipOutputStream, itemname, new FileInputStream(new File(outFilePath)));
                System.out.println("success");
        } catch (TemplateNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedTemplateNameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 


4.静态方法

    /**
         * 替换某个 item,
         * @param zipInputStream zip文件的zip输入流
         * @param zipOutputStream 输出的zip输出流
         * @param itemName 要替换的 item 名称
         * @param itemInputStream 要替换的 item 的内容输入流
         */
        public static void replaceItem(ZipInputStream zipInputStream,
                                       ZipOutputStream zipOutputStream,
                                       String itemName,
                                       InputStream itemInputStream
        ){
            //
            if(null == zipInputStream){return;}
            if(null == zipOutputStream){return;}
            if(null == itemName){return;}
            if(null == itemInputStream){return;}
            //
            ZipEntry entryIn;
            try {
                while((entryIn = zipInputStream.getNextEntry())!=null)
                {
                    String entryName =  entryIn.getName();
                    ZipEntry entryOut = new ZipEntry(entryName);
                    // 只使用 name
                    zipOutputStream.putNextEntry(entryOut);
                    // 缓冲区
                    byte [] buf = new byte[8*1024];
                    int len;
                    if(entryName.equals(itemName)){
                    // 使用替换流
                    while((len = (itemInputStream.read(buf))) > 0) {
                        zipOutputStream.write(buf, 0, len);
                    }
                } else {
                    // 输出普通Zip流
                    while((len = (zipInputStream.read(buf))) > 0) {
                        zipOutputStream.write(buf, 0, len);
                    }
                }
                // 关闭此 entry
                zipOutputStream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //e.printStackTrace();
            close(itemInputStream);
            close(zipInputStream);
            close(zipOutputStream);
        }
    }
    /**
     * 包装输入流
     */
    public static ZipInputStream wrapZipInputStream(InputStream inputStream){
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        return zipInputStream;
    }
    /**
     * 包装输出流
     */
    public static ZipOutputStream wrapZipOutputStream(OutputStream outputStream){
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        return zipOutputStream;
    }
    private static void close(InputStream inputStream){
        if (null != inputStream){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void close(OutputStream outputStream){
        if (null != outputStream){
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
    }


## 参考文献

* [HuCheng's](http://hucheng91.github.io/2017/04/09/web/java/freemarker_xdocxreport/)
