package Main;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Максим on 17.06.2016.
 */
public abstract class SmsClientAbstr implements ISmsClient {
    protected String login    = "";               // логин клиента
    protected String password = "";               // пароль или MD5-хеш пароля в нижнем регистре
    protected String charset  = "utf-8";          // кодировка сообщения: koi8-r, windows-1251 или utf-8 (по умолчанию)

    /**
     * constructors
     */
    public SmsClientAbstr(String _login, String _password) {
        login    = _login;
        password = _password;
    }
    public SmsClientAbstr(String _login, String _password, String _charset) {
        login    = _login;
        password = _password;
        charset  = _charset;
    }

    /**
     * Чтение xml
     * @param url - сервер
     * @param xml - документ
     * @return line - ответ сервера
     */
    protected String readXml(String url,String xml) throws IOException {
        String line = "";

        URL u= null;
        InputStream is = null;
        InputStreamReader reader = null;
        BufferedReader br = null;
        URLConnection conn = null;
        OutputStreamWriter os = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            u = new URL(url);
            conn = u.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("accept-charset", charset);
            conn.setRequestProperty("content-type", "text/xml");
            conn.setUseCaches(false);
            os = new OutputStreamWriter(conn.getOutputStream(), charset);
            os.write(xml);
            os.flush();
            os.close();

            is = conn.getInputStream();
            reader = new InputStreamReader(is, charset);
            br =  new BufferedReader(reader);

            //читаем по 1024
            int charRead;
            char[] buffer = new char[1024];
            while ((charRead = br.read(buffer)) > 0) {
                stringBuffer.append(buffer, 0, charRead);
            }
            reader.close();
        }
        catch ( IOException e) { // Неверно урл, протокол...
        }
        finally {
            if(br!=null) {
                br.close();
            }
            if(reader!=null) {
                reader.close();
            }
            if(os!=null) {
                os.close();
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

        URL u= null;
        InputStream is = null;
        InputStreamReader reader = null;
        BufferedReader br =  null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            u = new URL(url);
            is = u.openStream();
            reader = new InputStreamReader(is, charset);
            br =  new BufferedReader(reader);

            //читаем по 1024
            int charRead = 0;
            char[] buffer = new char[1024];
            while ((charRead = br.read(buffer)) > 0) {
                stringBuffer.append(buffer, 0, charRead);
            }
            reader.close();
        }
        catch (IOException e) { // Неверно урл, протокол...
        }
        finally {
            if(br!=null) {
                br.close();
            }
            if(reader!=null) {
                reader.close();
            }
        }

        return stringBuffer.toString();
    }

    /**
     * Перекодировка в соответствии с Charset
     * @param string - строка для перекодировки
     * @return  результат  типа String
     */
    protected String Encode(String string) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, charset);
    }
}
