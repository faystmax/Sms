package sms;

import java.io.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

/**
 * Created by Максим on 17.06.2016.
 */
public class SmsGorod extends SmsClientBase {

    private static final Logger LOG = LoggerFactory.getLogger(SmsGorod.class);
    /**
     * constructors
     */
    public SmsGorod(String login, String password) {
        super(login,password);
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
        String[] m = {};
        try {
            m = sendСmd("", "dadr=" + toCharset(phones) + "&text=" + toCharset(message));
        }
        catch (UnsupportedEncodingException e) {
        }

        return (m.length>0 ? m[0] : "error");
    }

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
     * @return array
     * для отправленного SMS succses
     * либо <код ошибки> в случае ошибки
     */
    @Override
    public String getStatus(String id, String phone) {
        String[] m = {};
        try{
            m = sendСmd("", "smsid=" + toCharset(id) );
        }
        catch (UnsupportedEncodingException e) {
        }

        return ("deliver".equals(m[0]) ? "succses" : m[0]);
    }

    @Override
    public String getStatus(int id, String phone) {
        String[] m = {};
        m = sendСmd("sms/status", "&id=" + id );
        return ("deliver".equals(m[0]) ? "succses" : m[0]);
    }

    /**
     * Получения баланса
     *
     * @return String баланс или пустую строку в случае ошибки
     */
    @Override
    public String getBalance() {
            String m;
            String balance="";

            try {
				String xml="<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
                            + "<request> "
                            + "<security>"
                            + "<login value=\"" + toCharset(login)+"\" />"
                            + "<password value=\"" + toCharset(password)+ "\" />"
                            + "</security>"
                            + "</request>";
				m = readXml("http://web2.smsgorod.ru/xml/balance.php", xml); // (balance) или (0, -error)
                                                    
                // парсим xml документкк
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(m));
                Document document = builder.parse(is);
                Element root = document.getDocumentElement();
                // для простоты сразу берем message
                Element message = (Element) root.getElementsByTagName("money").item(0);
                balance = message.getTextContent(); // тоже для упрощения
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                LOG.info(ex.toString());
            }

            return ( balance== null ? "" : balance) ;
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
            String url = "http://web2.smsgorod.ru/sendsms.php?user=" + toCharset(login)
                    + "&pwd=" + toCharset(password)
                    + "&" + arg;
            ret = readUrl(url);
        }
        catch ( IOException e) {
        }
        return ret.split("\n");
    }
       

}
