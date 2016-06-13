package SmsAreo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 *
 * @author Максим
 */
import Main.Main_Sms;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Максим
 */
public class SmsAreo implements Main_Sms{

	String LOGIN    = "";                           // логин клиента
	String PASSWORD = "";                           //  MD5-хеш пароля в нижнем регистре
	String CHARSET  = "utf-8";                      // кодировка сообщения: koi8-r, windows-1251 или utf-8 (по умолчанию)


	/**
	 * constructors
	 */
	public SmsAreo() {
	}
	public SmsAreo(String login, String password) {
		LOGIN    = login;
		PASSWORD = md5Apache(password);
	}
	public SmsAreo(String login, String password, String charset) {
		LOGIN    = login;
		PASSWORD = md5Apache(password);
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
		String[] m ;
                String json = "";
		try {                      
			json = _smsc_send_cmd("send", "to=" + URLEncoder.encode(phones, CHARSET) 
						+ "&text=" + URLEncoder.encode(message, CHARSET)
                                                + "&from=NEWS") ;                  
		}
		catch (UnsupportedEncodingException e) {
		}
                
                JSONParser parser = new JSONParser();
                Object obj=null;
                try {
                    obj = parser.parse(json);
                } catch (ParseException ex) {
                    Logger.getLogger(SmsAreo.class.getName()).log(Level.SEVERE, null, ex);
                }
                JSONObject jsonObj = (JSONObject) obj;
                m = new String[2];
                m[0] = (String) jsonObj.get("result");
                if("reject".equals(m[0]))
                    return  (String) jsonObj.get("reason");
                else
                    return Long.toString((Long) jsonObj.get("id"));
                //в случае удачи в m идентификатор сообщения
	};

	/**
	 * Проверка статуса отправленного SMS 
	 *
	 * @param id - ID cообщения
	 * @param phone - номер телефона
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
	 */

        @Override
	public String get_status(String id, String phone)
	{
		String[] m;
		String tmp;
                String json = "";    
		try {
			json = _smsc_send_cmd("status", "&id=" + URLEncoder.encode(id, CHARSET) );
		}
		catch (UnsupportedEncodingException e) {
		}
                //Парсим json объект
                JSONParser parser = new JSONParser();
                Object obj=null;
                try {
                    obj = parser.parse(json);
                } catch (ParseException ex) {
                    Logger.getLogger(SmsAreo.class.getName()).log(Level.SEVERE, null, ex);
                }
                JSONObject jsonObj = (JSONObject) obj;
                m = new String[2];
                m[0] = (String) jsonObj.get("result");
                m[1] = Long.toString((Long) jsonObj.get("id"));
		return ("delivery success".equals(m[0]) ? "succses" : m[0]);
	}
        @Override
	public String get_status(int id, String phone)
	{
		String[] m ={};
                String json = "";     
		try {
			json = _smsc_send_cmd("sms/status", "&id=" + URLEncoder.encode(Integer.toString(id), CHARSET) );
		}
		catch (UnsupportedEncodingException e) {

		}
                //Парсим json объект
                try {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(json);

                    JSONObject jsonObj = (JSONObject) obj;
                    m = new String[2];
                    m[0] = (String) jsonObj.get("result");
                    m[1] = Long.toString((Long) jsonObj.get("id")); 
                } catch (ParseException ex) {
                    Logger.getLogger(SmsAreo.class.getName()).log(Level.SEVERE, null, ex);
                }
		return ("delivery success".equals(m[0]) ? "succses" : m[0]);
	}

        /**
	 * Получения баланса
	 *
	 * @return String баланс или пустую строку в случае ошибки
	 */
        @Override
	public String get_balance() {
		String m ;

		m = _smsc_send_cmd("balance", ""); // (balance) или (0, -error)
                
                //Парсим json объект
                JSONParser parser = new JSONParser();
                Object obj=null;
                try {
                    obj = parser.parse(m);
                } catch (ParseException ex) {
                    Logger.getLogger(SmsAreo.class.getName()).log(Level.SEVERE, null, ex);
                }
                JSONObject jsonObj = (JSONObject) obj;
                String otv =(String) jsonObj.get("balance");
                return otv == null ? "" : otv;
	}

	/**
	 * Формирование и отправка запроса
	 * @param cmd - требуемая команда
	 * @param arg - дополнительные параметры
	 */

        @SuppressWarnings("SuspiciousIndentAfterControlStatement")
	private String _smsc_send_cmd(String cmd, String arg){
		String[] m = {};
		String ret = ",";

		try {
			String url = "https://gate.smsaero.ru/" + cmd +"/?user="+URLEncoder.encode(LOGIN, CHARSET)
                                                                + "&password=" + URLEncoder.encode(PASSWORD, CHARSET)
                                                                +  "&" + arg+"&answer=json";
			
			int i = 0;
			do {
                            if (i > 0)
                            Thread.sleep(2000 + 1000 * i);
                            ret = _smsc_read_url(url);
			}
			while ("".equals(ret) && ++i < 4);
		}
		catch (UnsupportedEncodingException | InterruptedException e) {
		}

		return ret;
	}

	/**
	 * Чтение URL
	 * @param url - ID cообщения
	 * @return line - ответ сервера
	 */

	private String _smsc_read_url(String url) {

		String line = "", real_url = url;
		String[] param = {};

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
