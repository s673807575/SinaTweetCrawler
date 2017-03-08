package com.sina.crawl;
/** 
 * @author  
 * @version 2017��3��6�� ����5:13:11 
 * ��˵�� 
 */
import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.File;  
import java.io.FileReader;  
import java.io.FileWriter;  
import java.io.IOException;  
import java.net.URI;  
import java.net.URISyntaxException;  
import java.util.regex.Pattern;  
import java.util.regex.Matcher;  
  
import org.apache.http.client.ClientProtocolException;  
import org.apache.http.client.config.CookieSpecs;  
import org.apache.http.client.config.RequestConfig;  
import org.apache.http.client.methods.CloseableHttpResponse;  
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.config.Registry;  
import org.apache.http.config.RegistryBuilder;  
import org.apache.http.cookie.Cookie;  
import org.apache.http.cookie.CookieOrigin;  
import org.apache.http.cookie.CookieSpec;  
import org.apache.http.cookie.CookieSpecProvider;  
import org.apache.http.cookie.MalformedCookieException;  
import org.apache.http.impl.client.CloseableHttpClient;  
import org.apache.http.impl.client.HttpClients;  
import org.apache.http.impl.cookie.BestMatchSpecFactory;  
import org.apache.http.impl.cookie.BrowserCompatSpec;  
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;  
import org.apache.http.protocol.HttpContext;  
import org.apache.http.util.EntityUtils;  
public class Test {
	 private String getHTML(String url) throws URISyntaxException, ClientProtocolException, IOException {  
	        //�����û��Զ���cookie����,����ʾcookie rejected�ı���  
	        CookieSpecProvider cookieSpecProvider = new CookieSpecProvider(){  
	            public CookieSpec create(HttpContext context){  
	                return new BrowserCompatSpec(){  
	                    @Override  
	                    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {  
	                          
	                    }  
	                };  
	            }  
	        };  
	        Registry<CookieSpecProvider> r = RegistryBuilder  
	                .<CookieSpecProvider> create()  
	                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())  
	                .register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())  
	                .register("cookie", cookieSpecProvider)  
	                .build();  
	        RequestConfig requestConfig = RequestConfig.custom()  
	                .setCookieSpec("cookie")  
	                .setSocketTimeout(5000) //����socket��ʱʱ��  
	                .setConnectTimeout(5000)  //����connect��ʱʱ��  
	                .build();  
	        CloseableHttpClient httpClient = HttpClients.custom()  
	                .setDefaultCookieSpecRegistry(r)  
	                .setDefaultRequestConfig(requestConfig)  
	                .build();  
	        HttpGet httpGet = new HttpGet(url);  
	        httpGet.setConfig(requestConfig);  
	        String html = "html��ȡʧ��"; //������֤�Ƿ�ȡ��������html  
	        try{  
	            CloseableHttpResponse response = httpClient.execute(httpGet);  
	            html = EntityUtils.toString(response.getEntity());  
	            //System.out.println(html); //��ӡ���ص�html           
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        return html;  
	    }  
	      
	   
	      
	    private void writeWeibo2txt(String html, String savePath) throws IOException {  
	        File htmltxt = new File(savePath); //�½�һ��txt�ļ����ڴ����ȡ�Ľ����Ϣ  
	        FileWriter fw = new FileWriter(htmltxt);  
	        BufferedWriter bw = new BufferedWriter(fw);
//	        bw.write(html);
	        //regex-----"id":\s"\d{19}",(\n*?)|(\s*?)"content":\s".*?",(\n*?)|(\s*?)"prettyTime":\s".*?"  
	        Pattern p = Pattern.compile("(<a class=\\\\\"W_texta W_fb.+?<\\\\/a>)|(<p class=\\\\\"comment_txt.+?<\\\\/p>)");  
//	        System.out.println("(<a class=\\\\\"W_texta W_fb.+?<\\\\/a>)|(<p class=\\\\\"comment_txt.+?<\\\\/p>)");
	        Matcher m = p.matcher(html);  
	        while(m.find()) {  
	        	String temp=convert(removeTag(m.group()))+"\r\n";
	            System.out.println(temp);  
	            bw.write(temp);  
	        }  
	        bw.close();  
	    }  
	    public static String convert(String utfString){  
	        StringBuilder sb = new StringBuilder();  
	        int i = -1;  
	        int pos = 0;  
	          
	        while((i=utfString.indexOf("\\u", pos)) != -1){  
	            sb.append(utfString.substring(pos, i));  
	            if(i+5 < utfString.length()){  
	                pos = i+6;  
	                sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));  
	            }  
	        }  
	          
	        return sb.toString();  
	    }  
	    public static String removeTag(String str){
	    	 Pattern p = Pattern.compile("(<.+?>)|(<\\\\/.+?>)");  
	    	 Matcher m = p.matcher(str); 
	    	 while(m.find()) {  		             
		            str=str.replace(m.group(), ""); 
		        }
	    	 str=str.replace("\\n\\t\\t", "");
	    	 return str;
	    }
	    
	    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {  
	    	
	    	Test crawler = new Test();  
	        String searchword = "������"; //�����ؼ���Ϊ"iPad"��΢��htmlҳ��  
	        String html = crawler.getHTML("http://s.weibo.com/weibo/"+searchword);  
	        String savePath = "e:/weibo/html.txt"; //�����txt�ļ��Ĵ��·��  
	    }  
}	
