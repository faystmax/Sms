package main;



import sms.*;

/**
 * Created by Максим on 17.06.2016.
 */

public class FactorySms {
      
   public FactorySms(){
   }

   //возвращает объект для рассылки
   public ISmsClient getGateway(GateWay Sms, String Login, String Password) {
        switch(Sms) {
            case SMSCRU:
                return new SmscRu(Login, Password);
            case SMSRU:
                return new SmsRu(Login, Password);
            case SMSGOROD:
                return new SmsGorod(Login, Password);
            case SMSAREO:
                return new SmsAreo(Login, Password);
            default:
                return null;
        }
   }
   
}
