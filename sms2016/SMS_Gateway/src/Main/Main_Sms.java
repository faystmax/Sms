package Main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Максим
 */
public interface Main_Sms {
        /**
	 * Отправка SMS
	 * 
	 * @param phones - список телефонов через запятую или точку с запятой
	 * @param message - отправляемое сообщение
	 * @return  <id>
         * или код ошибки
	 */
        public String send_sms(String phones, String message);
        
        /**
	 * Получение стоимости SMS
	 *
	 * @param phones - список телефонов через запятую или точку с запятой
	 * @param message - отправляемое сообщение.
	 * @return array(<стоимость>, <количество sms>) либо (0, -<код ошибки>) в случае ошибки
	 */
        public String get_sms_cost(String phones, String message);
        
	/**
	 * Проверка статуса отправленного SMS 
	 *
	 * @param id - ID cообщения
	 * @param phone - номер телефона
	 * @return array
	 * для отправленного SMS succses
	 * либо <код ошибки> в случае ошибки
	 */
        public String get_status(int id, String phone);
        public String get_status(String id, String phone);
        /**
	 * Получения баланса
	 *
	 * @return String баланс или пустую строку в случае ошибки
	 */
        public String get_balance();
        

}
