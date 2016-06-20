package sms;


import sms.ISmsClient;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Максим on 17.06.2016.
 */
public abstract class SmsClientBase implements ISmsClient {
    protected String login    = "";                         // логин клиента
    protected String password = "";                         // пароль или MD5-хеш пароля в нижнем регистре
    protected static final String charset  = "utf-8";       // кодировка сообщения
    protected static final Logger LOG = LoggerFactory.getLogger(SmsClientBase.class);
    /**
     * constructors
     */
    public SmsClientBase(String login, String password) {
        this.login    = login;
        this.password = password;
    }

    /**
     * Чтение xml
     * @param url - сервер
     * @param xml - документ
     * @return line - ответ сервера
     */
    protected String readXml(String url,String xml) throws IOException {
        String line = "";
        StringBuffer stringBuffer = new StringBuffer();

        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        conn.setDoOutput(true);                                 //POST метод
        conn.setRequestProperty("accept-charset", charset);
        conn.setRequestProperty("content-type", "text/xml");
        conn.setUseCaches(false);

        //пишем
        try(OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), charset);)
        {
            os.write(xml);;
            os.flush();
        }

        //читаем
        try (InputStream is = conn.getInputStream();
             InputStreamReader reader = new InputStreamReader(is, charset);
             BufferedReader br = new BufferedReader(reader)
        ) {

            //читаем по 1024
            int charRead;
            char[] buffer = new char[1024];
            while ((charRead = br.read(buffer)) > 0) {
                stringBuffer.append(buffer, 0, charRead);
            }
        }

        return stringBuffer.toString();
    }

    /**
     * Чтение URL
     * @param url - ID cообщения
     * @return line - ответ сервера
     */
    protected String readUrl(String url) throws IOException {
        String line = "";

        URL u = new URL(url);
        InputStream is = u.openStream();
        StringBuffer stringBuffer = new StringBuffer();
        try (InputStreamReader reader = new InputStreamReader(is, charset);
             BufferedReader br = new BufferedReader(reader)
        ) {
            //читаем по 1024
            int charRead = 0;
            char[] buffer = new char[1024];
            while ((charRead = br.read(buffer)) > 0) {
                stringBuffer.append(buffer, 0, charRead);
            }
        }

        return stringBuffer.toString();
    }

    /**
     * Перекодировка в соответствии с Charset
     * @param string - строка для перекодировки
     * @return  результат  типа String
     */
    protected String toCharset(String string) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, charset);
    }
}
