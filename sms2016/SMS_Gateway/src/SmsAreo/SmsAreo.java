package SmsAreo;

import Main.SmsClientAbstr;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * Created by Максим on 17.06.2016.
 */
public class SmsAreo extends SmsClientAbstr{

    /**
     * constructors
     */
    public SmsAreo(String _login, String _password) {
        super(_login, DigestUtils.md5Hex(_password));
    }
    public SmsAreo(String _login, String _password, String _charset) {
        super(_login,DigestUtils.md5Hex(_password),_charset);
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
	public String send(String phones, String message)
    {
		String[] m ;
        String json = "";
		try {                      
			json = sendСmd("send", "to=" + Encode(phones) + "&text=" + Encode(message)
                    + "&from=NEWS") ;
		}
		catch (UnsupportedEncodingException e) {
		}
        //парсим json объект
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

        //в случае удачи в m идентификатор сообщения
        return ("reject".equals(m[0]) ? (String) jsonObj.get("reason")
                                      :  Long.toString((Long) jsonObj.get("id")));
	};

    /**
     * Получение стоимости SMS
     *
     * @param phones - список телефонов через запятую или точку с запятой
     * @param message - отправляемое сообщение.
     * @return <стоимость> либо <код ошибки> в случае ошибки
     */
    @Override
	public String getCost(String phones, String message)
	{
                throw new Error("Not supported");
	}

	/**
	 * Проверка статуса отправленного SMS 
	 *
	 * @param id - ID cообщения
	 * @param phone - номер телефона
	 * @return <стоимость> либо <код ошибки> в случае ошибки
	 */
    @Override
	public String getStatus(String id, String phone)
	{
		String[] m;
		String tmp;
                String json = "";    
		try {
			json = sendСmd("status", "&id=" + Encode(id) );
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
	public String getStatus(int id, String phone)
	{
		String[] m ={};
        String json = "";
        json = sendСmd("sms/status", "&id=" + id );
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
	public String getBalance() {
		String m ;

		m = sendСmd("balance", ""); // (balance) или (0, -error)
                
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
     * @return line - ответ сервера
     */
    private String sendСmd(String cmd, String arg){
        String ret = "";
        try {
            String url = "https://gate.smsaero.ru/" + cmd +"/?user=" + Encode(login)
                    + "&password=" + Encode(password)
                    +  "&" + arg+"&answer=json";
            ret = readUrl(url);
        }
        catch ( IOException e) {
        }
        return ret;
    }

}
