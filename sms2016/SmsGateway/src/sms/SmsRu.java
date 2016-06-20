package sms;

import java.io.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by Максим on 17.06.2016.
 */
public class SmsRu extends SmsClientBase {

	private static final Logger LOG = LoggerFactory.getLogger(SmsRu.class);
    /**
     * constructors
     */
    public SmsRu(String login, String password) {
        super(login,password);
    }

    /**
	 * Отправка SMS
	 * 
	 * @param phones - список телефонов через запятую 
	 * @param message - отправляемое сообщение
	 * @return  <id> или код ошибки
	 */
	@Override
	public String send(String phones, String message) {
		String[] m = {};
		try {
            m = sendСmd("sms/send", "to=" + toCharset(phones)
                    + "&text=" + toCharset(message)) ;
		}
		catch (UnsupportedEncodingException e) {
		}
        //в случае удачи в m идентификатор сообщения
		return m.length == 1 ?	m[0] : m[1];
	}

	/**
	 * Получение стоимости SMS
	 *
	 * @param phones - список телефонов через запятую или точку с запятой
	 * @param message - отправляемое сообщение.
	 * @return <стоимость> либо <код ошибки> в случае ошибки
	 */
    @Override
	public String getCost(String phones, String message) {
        String[] m = {};
		try {
            m = sendСmd("sms/cost", "to=" + toCharset(phones)
                    + "&text=" + toCharset(message)) ;
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
	public String getStatus(String id, String phone) {
		String[] m = {};
		try {
            m = sendСmd("sms/status", "&id=" + toCharset(id));
		}
		catch (UnsupportedEncodingException e) {
		}
		return ("103".equals(m[0]) ? "succses" : m[0]);
	}
        
    @Override
	public String getStatus(int id, String phone)
	{
		String[] m = {};
        m = sendСmd("sms/status", "&id=" + id );
		return ("103".equals(m[0]) ? "succses" : m[0]);
	}

    /**
	 * Получения баланса
	 *
	 * @return String баланс или пустую строку в случае ошибки
	 */
    @Override
	public String getBalance() {
		String[] m ;
        m = sendСmd("my/balance", "");
		return m.length == 1 ?	"" : m[1];
	}

    /**
     * Формирование и отправка запроса
     * @param cmd - требуемая команда
     * @param arg - дополнительные параметры
     * @return line - ответ сервера
     */
    private String[] sendСmd(String cmd, String arg){
        String ret = "";
        try {
            String url = "http://sms.ru/" + cmd +"?login=" + toCharset(login)
                    + "&password=" + toCharset(password)
                    + "&" + arg;
            ret = readUrl(url);
        }
        catch ( IOException e) {
        }
        return ret.split("\n");
    }

}
