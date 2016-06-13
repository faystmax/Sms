package SmscRu;



/*
 * SMSC.RU API (smsc.ru) версия 1.1 (24.08.2012) smsc's sms sender package
 */
import Main.Main_Sms;
import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.Math;
import java.sql.*;
import org.apache.commons.codec.digest.DigestUtils;

public class SmscRu implements Main_Sms{

	String LOGIN    = "";         // логин клиента
	String PASSWORD = "";      // пароль или MD5-хеш пароля в нижнем регистре
	String CHARSET  = "utf-8";         // кодировка сообщения: koi8-r, windows-1251 или utf-8 (по умолчанию)


	/**
	 * constructors
	 */
	public SmscRu() {
	}
	public SmscRu(String login, String password) {
		LOGIN    = login;
		PASSWORD = md5Apache(password);
	}
	public SmscRu(String login, String password, String charset) {
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
	public String send_sms(String phones, String message){
		String[] m = {};
		try {
			m = _smsc_send_cmd("send", "cost=3&phones=" + URLEncoder.encode(phones, CHARSET) 
					+ "&mes=" + URLEncoder.encode(message, CHARSET));
		}
		catch (UnsupportedEncodingException e) {
		}
		return m.length == 4 ?	m[0] : m[1];
	};

	/**
	 * Получение стоимости SMS
	 *
	 * @param phones - список телефонов через запятую или точку с запятой
	 * @param message - отправляемое сообщение.
	 * @return <стоимость> либо <код ошибки> в случае ошибки
	 */
        @Override
	public String get_sms_cost(String phones, String message){
		String[] m = {};

		try { 
				m = _smsc_send_cmd("send", "cost=1&phones=" + URLEncoder.encode(phones, CHARSET) 
						+ "&mes=" + URLEncoder.encode(message, CHARSET));
		}
		catch (UnsupportedEncodingException e) {
		}
		// cost или error
		return ("0".equals(m[0]) ? m[1] : m[0]);
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
	public String get_status(int id, String phone){
		String[] m = {};
		try {
			m = _smsc_send_cmd("status", "phone=" + URLEncoder.encode(phone, CHARSET) + "&id=" + id);
		}
		catch (UnsupportedEncodingException e) {
		}
		return (m[0]== "1" ? "succses" : m[0]);
	}
        
        @Override
	public String get_status(String id, String phone){
		String[] m = {};
                
		try {
			m = _smsc_send_cmd("status", "phone=" + URLEncoder.encode(phone, CHARSET) + "&id=" + id );
		}
		catch (UnsupportedEncodingException e) {
		}
		return ("1".equals(m[0]) ? "succses" : m[0]);
	}

        /**
	 * Получения баланса
	 *
	 * @return String баланс или пустую строку в случае ошибки
	 */
        @Override
	public String get_balance() {
		String[] m = {};
		m = _smsc_send_cmd("balance", ""); // (balance) или (0, -error)
		return m.length == 2 ?	"" : m[0];
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
			String url = "http" + "://smsc.ru/sys/" + cmd +".php?login=" + URLEncoder.encode(LOGIN, CHARSET) 
				+ "&psw=" + URLEncoder.encode(PASSWORD, CHARSET) 
				+ "&fmt=1&charset=" + CHARSET + "&" + arg;
			
			int i = 0;
			do {
				if (i > 0)
					Thread.sleep(2000 + 1000 * i);

				if (i == 2)
					url = url.replace("://smsc.ru/", "://www2.smsc.ru/");

				ret = _smsc_read_url(url);
			}
			while (ret == "" && ++i < 4);
		}
		catch (UnsupportedEncodingException | InterruptedException e) {

		}

		return ret.split(",");
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
