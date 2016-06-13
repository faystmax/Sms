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
public class TEST {

    public static void main(String[] args) {
        // TODO code application logic here
        FactorySms test=new FactorySms();
        Main_Sms SmscRu,
                SmsRu,
                SmsGorod,
                SmsAreo;
        SmscRu = test.getGateway("SmscRu","faystmax","vfrcbvrf77");
        SmsRu = test.getGateway("SmsRu","79130825174","vfrcbvrf77");
        SmsGorod = test.getGateway("SmsGorod","faystmax","vfrcbvrf77");
        SmsAreo = test.getGateway("SmsAreo","faystmax@gmail.com","vfrcbvrf77");
        
        //тест четырёх классов
        String _balance1 = SmscRu.get_balance();
        String _balance2 = SmsRu.get_balance();
        String _balance3 = SmsGorod.get_balance();
        String _balance4 = SmsAreo.get_balance();
        
        //String send_sms1 = SmscRu.send_sms("79130825174", "Test1");           //2
        //String send_sms2 = SmsRu.send_sms("79130825174", "Test2");            //201624-1000000
        //String send_sms3 = SmsGorod.send_sms("79130825174", "Test3");         //2809957679
        //String send_sms4 = SmsAreo.send_sms("79130825174", "Test4");          //33847505
        
        String get_status1 = SmscRu.get_status("2", "79130825174");                        //2
        String get_status2 = SmsRu.get_status("201624-1000000", "79130825174");            //201624-1000000
        String get_status3 = SmsGorod.get_status("2809957679", "79130825174");             //2809957679
        String get_status4 = SmsAreo.get_status("33847505", "79130825174");                //33847505

        
    }
    
}
