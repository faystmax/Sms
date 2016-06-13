package Main;


import SmscRu.SmscRu;
import SmsRu.SmsRu;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Максим
 */
import SmscRu.SmscRu;
import SmsRu.SmsRu;
import SmsAreo.SmsAreo;
import SmsGorod.SmsGorod;

public class FactorySms {
      
   public FactorySms(){
   }
   //возвращает объект для рассылки
   public Main_Sms getGateway(String SMS,String LOGIN,String PASSWORD)
   {
        switch(SMS) 
        {
            case "SmscRu": 
                return new SmscRu(LOGIN,PASSWORD);
            case "SmsRu": 
                return new SmsRu(LOGIN,PASSWORD);
            case "SmsGorod": 
                return new SmsGorod(LOGIN,PASSWORD);
            case "SmsAreo": 
                return new SmsAreo(LOGIN,PASSWORD);      
        }
       return null;
   }
   
}
