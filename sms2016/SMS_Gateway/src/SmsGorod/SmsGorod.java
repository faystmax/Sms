/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmsGorod;

/**
 *
 * @author Максим
 */
import org.apache.commons.codec.digest.DigestUtils;
import Main.Main_Sms;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
/**
 *
 * @author Максим
 */
public class SmsGorod implements Main_Sms{

	String LOGIN    = "";         // логин клиента
	String PASSWORD = "";      // пароль или MD5-хеш пароля в нижнем регистре
	String CHARSET  = "utf-8";         // кодировка сообщения: koi8-r, windows-1251 или utf-8 (по умолчанию)

	/**
	 * constructors
	 */
	public SmsGorod() {
	}
	public SmsGorod(String login, String password) {
		LOGIN    = login;
		PASSWORD = password;
	}
	public SmsGorod(String login, String password, String charset) {
		LOGIN    = login;
		PASSWORD = password;
		CHARSET  = charset;
	}

        /**
	 * Отправка SMS
	 * 
	 * @param phones - список телефонов через запятую или точку с запятой
	 * @param message - отправляемое сообщение
	 * @return  <id> 
         * или код ошибки
	 */
	@Override
	public String send_sms(String phones, String message)
    {
		String[] m = {};

		try {                        
			m = _smsc_send_cmd("", "dadr=" + URLEncoder.encode(phones, CHARSET) 
						+ "&text=" + URLEncoder.encode(message, CHARSET));                   
		}
		catch (UnsupportedEncodingException e) {
		}
                //в случае удачи в m идентификатор сообщения
		return m[0];
	};

	/**
	 * Получение стоимости SMS
	 *
	 * @param phones - список телефонов через запятую или точку с запятой
	 * @param message - отправляемое сообщение.
	 * @return <стоимость> либо <код ошибки> в случае ошибки
	 */

        @Override
	public String get_sms_cost(String phones, String message)
	{
                throw new Error("Not supported");
	}

	/**
	 * Проверка статуса отправленного SMS 
	 *
	 * @param id - ID cообщения
	 * @param phone - номер телефона
	 * @return array
	 * для отправленного SMS succses
	 * либо <код ошибки> в случае ошибки
	 */

        @Override
	public String get_status(String id, String phone)
	{
		String[] m = {};
		String tmp;

		try{
                    m = _smsc_send_cmd("", "smsid=" + URLEncoder.encode(id, CHARSET) );
		}
		catch (UnsupportedEncodingException e) {

		}

		return ("deliver".equals(m[0]) ? "succses" : m[0]);
	}
        @Override
	public String get_status(int id, String phone)
	{
		String[] m = {};
		try {
			m = _smsc_send_cmd("sms/status", "&id=" + URLEncoder.encode(Integer.toString(id), CHARSET) );
		}
		catch (UnsupportedEncodingException e) {
		}
		return ("deliver".equals(m[0]) ? "succses" : m[0]);
	}

        /**
	 * Получения баланса
	 *
	 * @return String баланс или пустую строку в случае ошибки
	 */
        @Override
	public String get_balance() {
                            String m;
                            String balance="";
            try {                   
                    String xml="<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
                            + "<request> "
                            + "<security>"
                            + "<login value=\""+URLEncoder.encode(LOGIN, CHARSET)+"\" />"
                            + "<password value=\"" + URLEncoder.encode(PASSWORD, CHARSET)+ "\" />"
                            + "</security>"
                            + "</request>";
                    m = _smsc_send_xml("http://web2.smsgorod.ru/xml/balance.php", xml); // (balance) или (0, -error)
                                                    
                // парсим xml документкк
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(m));
                Document document = builder.parse(is);
                Element root = document.getDocumentElement();
                // для простоты сразу берем message
                Element message = (Element) root.getElementsByTagName("money").item(0);
                balance = message.getTextContent(); // тоже для упрощения
                System.out.println(balance);
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                Logger.getLogger(SmsGorod.class.getName()).log(Level.SEVERE, null, ex);
            } 
            return ( balance== null ? "" : balance) ;
	}

	/**
	 * Формирование и отправка запроса
	 * @param cmd - требуемая команда
	 * @param arg - дополнительные параметры
	 */

	private String[] _smsc_send_cmd(String cmd, String arg){
		String[] m = {};
		String ret = ",";
		try {
			String url = "http://web2.smsgorod.ru/sendsms.php?user="+URLEncoder.encode(LOGIN, CHARSET)
                                                                                + "&pwd=" + URLEncoder.encode(PASSWORD, CHARSET)
                                                                                + "&" + arg;
			int i = 0;
			do {
                            if (i > 0)
                                Thread.sleep(2000 + 1000 * i);
                            ret = _smsc_read_url(url);
			}
			while (ret == "" && ++i < 4);
		}
		catch (UnsupportedEncodingException | InterruptedException e) {
		}

		return ret.split("\n");
	}
        
	private String _smsc_send_xml(String url,String xml){
		String[] m = {};
		String ret = "";   
                int i = 0;
                do {
                        if (i > 0)
                                try {
                                    Thread.sleep(2000 + 1000 * i);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(SmsGorod.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        ret = _smsc_read_xml(url,xml);
                }
                while ("".equals(ret) && ++i < 4);
		return ret;
	}
        private String _smsc_read_xml(String url,String xml) {

		String line = "";
		try {
			URL u = new URL(url);
			InputStream is;
                        URLConnection conn = u.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestProperty("accept-charset", CHARSET);
                        conn.setRequestProperty("content-type", "text/xml");
                        conn.setUseCaches(false);
                        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), CHARSET);
                        os.write(xml);
                        os.flush();
                        os.close();
                        is = conn.getInputStream();

			InputStreamReader reader = new InputStreamReader(is, CHARSET);

			int ch;
			while ((ch = reader.read()) != -1) {
				line += (char)ch;
			} 
			reader.close(); 
		}
		catch (MalformedURLException e) { // Неверно урл, протокол...
		}
		catch (IOException e) {
		}

		return line;
	}
        
        
        
	/**
	 * Чтение URL
	 * @param url - ID cообщения
	 * @return line - ответ сервера
	 */
	private String _smsc_read_url(String url) {

		String line = "", real_url = url;

		try {
			URL u = new URL(real_url);
			InputStream is;
			is = u.openStream();
			

			InputStreamReader reader = new InputStreamReader(is, CHARSET);

			int ch;
			while ((ch = reader.read()) != -1) {
				line += (char)ch;
			}

			reader.close();
		}
		catch (MalformedURLException e) { // Неверно урл, протокол...
		}
		catch (IOException e) {
		}

		return line;
	}
       
        public static String md5Apache(String pass) {
            String md5Hex = DigestUtils.md5Hex(pass);
            return md5Hex;
        }
}
