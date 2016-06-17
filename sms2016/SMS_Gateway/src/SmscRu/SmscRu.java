package SmscRu;

import Main.SmsClientAbstr;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Максим on 17.06.2016.
 */
public class SmscRu extends SmsClientAbstr{

    /**
     * constructors
     */
    public SmscRu(String _login, String _password) {
        super(_login,_password);
    }
    public SmscRu(String _login, String _password, String _charset) {
        super(_login,_password,_charset);
    }

    /**
	 * Отправка SMS
	 * 
	 * @param phones - список телефонов через запятую или точку с запятой
	 * @param message - отправляемое сообщение
	 * @return  <id> или код ошибки
	 */
	@Override
	public String send(String phones, String message){
		String[] m = {};
		try {
            m = sendСmd("send", "cost=3&phones=" + Encode(phones)
					+ "&mes=" + Encode(message));
		}
		catch (UnsupportedEncodingException e) {
		}
		return m.length == 4 ?	m[0] : m[1];
	}

	/**
	 * Получение стоимости SMS
	 *
	 * @param phones - список телефонов через запятую или точку с запятой
	 * @param message - отправляемое сообщение.
	 * @return <стоимость> либо <код ошибки> в случае ошибки
	 */
    @Override
	public String getCost(String phones, String message){
		String[] m = {};
		try {
            m = sendСmd("send", "cost=1&phones=" + Encode(phones)
                                + "&mes=" + Encode(message));
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
	 * для отправленного SMS  string "succses"
	 * либо <код ошибки> в случае ошибки
	 */
    @Override
	public String getStatus(int id, String phone){
		String[] m = {};
		try {
            m = sendСmd("status", "phone=" + Encode(phone) + "&id=" + id);
		}
		catch (UnsupportedEncodingException e) {
		}
		return (m[0]== "1" ? "succses" : m[0]);
	}

    @Override
	public String getStatus(String id, String phone){
		String[] m = {};
		try {
            m = sendСmd("status", "phone=" + Encode(phone) + "&id=" + id );
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
	public String getBalance() {
		String[] m = {};
        m = sendСmd("balance", ""); // (balance) или (0, -error)
		return m.length == 2 ?	"" : m[0];
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
            String url = "http" + "://smsc.ru/sys/" + cmd +".php?login=" + Encode(login)
                    + "&psw=" + Encode(password)
                    + "&fmt=1&charset=" + charset + "&" + arg;
            ret = readUrl(url);
        }
        catch ( IOException e) {
        }
        return ret.split(",");
    }

}
