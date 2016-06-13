/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmsRu;
import Main.Main_Sms;
import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.Math;
import java.sql.*;
/**
 *
 * @author Максим
 */
public class SmsRu implements Main_Sms{

	String LOGIN    = "";         // логин клиента(номер телефона)
	String PASSWORD = "";      // пароль или MD5-хеш пароля в нижнем регистре
	String CHARSET  = "utf-8";         // кодировка сообщения: koi8-r, windows-1251 или utf-8 (по умолчанию)

	/**
	 * constructors
	 */
	public SmsRu() {
	}
	public SmsRu(String login, String password) {
		LOGIN    = login;
		PASSWORD = password;
	}
	public SmsRu(String login, String password, String charset) {
		LOGIN    = login;
		PASSWORD = password;
		CHARSET  = charset;
	}

        /**
	 * Отправка SMS
	 * 
	 * @param phones - список телефонов через запятую 
	 * @param message - отправляемое сообщение
	 * @return  <id>
         * или код ошибки
	 */
	@Override
	public String send_sms(String phones, String message)
    {
		String[] m = {};
		try {                      
        		m = _smsc_send_cmd("sms/send", "to=" + URLEncoder.encode(phones, CHARSET) 
						+ "&text=" + URLEncoder.encode(message, CHARSET)) ;
                        
		}
		catch (UnsupportedEncodingException e) {

		}

                //в случае удачи в m идентификатор сообщения
		return m.length == 1 ?	m[0] : m[1];
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
		String[] m = {};
		try { 
                                m = _smsc_send_cmd("sms/cost", "to=" + URLEncoder.encode(phones, CHARSET) 
						+ "&text=" + URLEncoder.encode(message, CHARSET)) ;
		}
		catch (UnsupportedEncodingException e) {
		}
                // cost или error
		return ("100".equals(m[0]) ? m[1] : m[0]);
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
		try {
			m = _smsc_send_cmd("sms/status", "&id=" + URLEncoder.encode(id, CHARSET) );
		}
		catch (UnsupportedEncodingException e) {
		}
		return ("103".equals(m[0]) ? "succses" : m[0]);
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
		return ("103".equals(m[0]) ? "succses" : m[0]);
	}

        /**
	 * Получения баланса
	 *
	 * @return String баланс или пустую строку в случае ошибки
	 */
        @Override
	public String get_balance() {
		String[] m ;
		m = _smsc_send_cmd("my/balance", ""); // (balance) или (0, -error)
		return m.length == 1 ?	"" : m[1];
	}

	/**
	 * Формирование и отправка запроса
	 * @param cmd - требуемая команда
	 * @param arg - дополнительные параметры
	 */

	private String[] _smsc_send_cmd(String cmd, String arg){
		String[] m = {};
		String ret = "";
		try {
			String url = "http://sms.ru/" + cmd +"?login="+URLEncoder.encode(LOGIN, CHARSET)
                                                                                + "&password=" + URLEncoder.encode(PASSWORD, CHARSET)
                                                                                + "&" + arg;		
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

		return ret.split("\n");
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
}
