---
layout: post
title:  "Spring emial邮件发送"
date:   2017-01-06 08:27:00
categories: java
excerpt: Spring emial邮件发送
---

* content
{:toc}




### 场景

在很多网站注册的时候，为了验证用户信息的真实合法，往往需要验证用户所填邮件的准

确性。形式为：用户注册时填写邮箱，注册完成后，网站会向用户所填邮箱发送一封激活

邮件，用户点击激活邮件中的链接后，方可完成注册。

### 核心代码


maven 依赖：

       <dependency>    
            <groupId>javax.mail</groupId>    
            <artifactId>mail</artifactId>    
            <version>1.4.7</version>    
        </dependency>  


配置文件：

    mail.from=1007393637@qq.com
    mail.host=smtp.qq.com
    mail.password=
    mail.smtp.auth=true  
    mail.smtp.timeout=2500000  
    mail.username=1007393637@qq.com
    mail.to=839676912@qq.com
    mail.smtp.port=465
    mail.smtp.starttls.enable=true







applicationContext.xml


    <bean id="mailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl">    
            <property name="host" value="${mail.host}" />
            <property name="username" value="${mail.username}" />    
            <property name="password" value="${mail.password}" />  
            <property name="defaultEncoding" value="UTF-8"></property>    
            <property name="javaMailProperties">    
                    
                   <props>
                    <prop key="mail.smtp.timeout">${mail.smtp.timeout}</prop>
                    <prop key="mail.smtp.auth">${mail.smtp.auth}</prop>
                    <prop key="mail.smtp.starttls.enable">${mail.smtp.starttls.enable}</prop>
                    <prop key="mail.smtp.socketFactory.port">${mail.smtp.port}</prop>
                    <prop key="mail.smtp.socketFactory.class">javax.net.ssl.SSLSocketFactory</prop>
                    <prop key="mail.smtp.socketFactory.fallback">false</prop>
                
                </props>    
            </property>    
        </bean>   


MailSenderService:


    import java.io.UnsupportedEncodingException;  
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Map;
    import javax.mail.MessagingException;  
    import javax.mail.internet.MimeMessage;  
    import org.springframework.beans.factory.annotation.Autowired;  
    import org.springframework.core.io.FileSystemResource;
    import org.springframework.mail.javamail.JavaMailSenderImpl;  
    import org.springframework.mail.javamail.MimeMessageHelper;  
    import org.springframework.stereotype.Service;  
    import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
    import org.springframework.util.StringUtils;
    import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
    import com.SimulationInfo.model.mail.MailBean;
    import com.SimulationInfo.util.CommonsUtil;
    import com.SimulationInfo.util.Constants;
    import freemarker.template.Template;
    @Service  
    public class MailSenderService {  
        @Autowired  
        private JavaMailSenderImpl javaMailSenderImpl;  
        
        @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
      
    /**  
     * 创建MimeMessage  
     * @param mailBean  
     * @return  
     * @throws MessagingException   
     * @throws UnsupportedEncodingException   
     */  
    public MimeMessage createMimeMessage(MailBean mailBean) throws MessagingException, UnsupportedEncodingException{  
        MimeMessage mimeMessage = javaMailSenderImpl.createMimeMessage();  
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");  
        messageHelper.setFrom(mailBean.getFrom(), mailBean.getFromName());   
        messageHelper.setSubject(mailBean.getSubject());    
        messageHelper.setTo(mailBean.getToEmails()); 
        messageHelper.setSentDate(new Date());
        if(mailBean.isTemplate()){
            
             messageHelper.setText(getMailText(mailBean), true); // html: true
        }else{
              messageHelper.setText(mailBean.getContext(), true); // html: true
        }
      
        //添加图片
        if(!StringUtils.isEmpty(mailBean.getImgPath())){
            FileSystemResource file = new FileSystemResource(mailBean.getImgPath());
            messageHelper.addInline("aaa", file);
        }
        
        return mimeMessage;  
    }  
      
    public void sendMail(MailBean mailBean) throws UnsupportedEncodingException, MessagingException{ 
         mailBean.setFrom(Constants.MAIL_FROM);  
         mailBean.setFromName("仿真大赛官网");  
         mailBean.setSubject("第二届报名成功");  
         mailBean.setTemplate(true);
         mailBean.setToEmails(new String[]{Constants.MAIL_TO});  
         mailBean.setContext("<html><head></head><body><h1>hello!!spring image html mail</h1>" +   
     "<img src=\"cid:aaa\"/></body></html>");  
         mailBean.setImgPath("D:\\111.jpg");
        MimeMessage msg = createMimeMessage(mailBean);  
        javaMailSenderImpl.send(msg);  
    }  
    
    
    //通过模板构造邮件内容，
    public String getMailText(MailBean mailBean){      
        String htmlText="";      
        try {      
            //通过指定模板名获取FreeMarker模板实例  
               
            Template tpl=freeMarkerConfigurer.getConfiguration().getTemplate("email.ftl");      
             
            //FreeMarker通过Map传递动态数据      
            Map map=new HashMap();  
            map.put("name", "foojay");
            map.put("address", "杭州");
            map.put("code", mailBean.getInvateCode());
            htmlText=FreeMarkerTemplateUtils.processTemplateIntoString(tpl,map);      
        } catch (Exception e) {      
            // TODO Auto-generated catch block      
            e.printStackTrace();      
        }      
        return htmlText;      
    }      
    }  

MailBean:

     public class MailBean {
    private String from;    
    private String fromName;    
    private String[] toEmails;    
    private String subject;  
    private String context;
    private String imgPath;
    private String invateCode;
    private boolean isTemplate;
    public boolean isTemplate() {
        return isTemplate;
    }
    public void setTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }
    public String getInvateCode() {
        return invateCode;
    }
    public void setInvateCode(String invateCode) {
        this.invateCode = invateCode;
    }
    public String getImgPath() {
        return imgPath;
    }
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getFromName() {
        return fromName;
    }
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
    public String[] getToEmails() {
        return toEmails;
    }
    public void setToEmails(String[] toEmails) {
        this.toEmails = toEmails;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }  
    }



测试：

    @Get("sendEmail")
        public String sendMail() {
            
        MailBean mailBean = new MailBean();  
        try {
            this.mailSenderService.sendMail(mailBean);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        return "@"+CommonsUtil.returnObjectToJson("发送成功");
    }


### 参考文献

* [小瓶子博客园](http://developer.51cto.com/art/201111/300358.htm)
* [程高伟](http://blog.csdn.net/frankcheng5143/article/details/50436207)