package Main;



import SmscRu.SmscRu;
import SmsRu.SmsRu;
import SmsAreo.SmsAreo;
import SmsGorod.SmsGorod;

/**
 * Created by Максим on 17.06.2016.
 */

public class FactorySms {
      
   public FactorySms(){
   }

   //возвращает объект для рассылки
   public ISmsClient getGateway(EnumGateWay Sms,String Login,String Password) {
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
