package com.kl.example;

import com.kl.example.service.util.HttpsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KlExamplesWebApplication.class)
@Slf4j
public class HttpsUtilTest {

    @Test
    public void doPostTest() {
    }

    @Test
    public void doGetTest() {
        String result = HttpsUtil.doGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx030fb8b41c7574ce&secret=9a0d0fca4ff83c4ffa2718248e940f17");
    }

    public static void main(String[] args) {
//        String result = HttpsUtil.doGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx030fb8b41c7574ce&secret=9a0d0fca4ff83c4ffa2718248e940f17");
//        System.out.println(result);

//        String result = HttpsUtil.doGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=57_1ltsmbVA7TE9t5skMb6vusjIyjGOzkTGXsTOHBtM3Qtm2vgF4J3L7lWMC1O6HuziLlZ6rQ42RfSmOnf94cCoyplyUxd0ZTXRttsD9Rn5jHiaKfaPC3QNvo3CaVAr-5McNc1qZomVnVNlhMVTQYVfAFAWEB&type=jsapi");
//        System.out.println(result);

        System.out.println(new Date().getTime());
    }
}
