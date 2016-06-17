package Main;

/**
 * Created by Максим on 17.06.2016.
 */
public interface ISmsClient {
    /**
	 * Отправка SMS
	 *
	 * @param phones - список телефонов через запятую или точку с запятой
	 * @param message - отправляемое сообщение
	 * @return  <id> или код ошибки
	 */
    String send(String phones, String message);
        
    /**
	 * Получение стоимости SMS
	 *
	 * @param phones - список телефонов через запятую или точку с запятой
	 * @param message - отправляемое сообщение.
     * @return <стоимость> либо <код ошибки> в случае ошибки
	 */
    String getCost(String phones, String message);
        
	/**
	 * Проверка статуса отправленного SMS 
	 *
	 * @param id - ID cообщения
	 * @param phone - номер телефона
	 * @return array
	 * для отправленного SMS succses
	 * либо <код ошибки> в случае ошибки
	 */
    String getStatus(int id, String phone);
    String getStatus(String id, String phone);

    /**
	 * Получения баланса
	 *
	 * @return String баланс или пустую строку в случае ошибки
	 */
    public String getBalance();
        

}
