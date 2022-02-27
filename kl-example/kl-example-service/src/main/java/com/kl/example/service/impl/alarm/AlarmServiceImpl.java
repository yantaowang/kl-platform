//package com.kl.example.service.impl.alarm;
//
//import cn.hutool.crypto.digest.HmacAlgorithm;
//import com.dingtalk.api.DefaultDingTalkClient;
//import com.dingtalk.api.DingTalkClient;
//import com.dingtalk.api.request.OapiRobotSendRequest;
//import com.ewp.examples.api.dversion.ExamplesApiVersion;
//import com.ewp.examples.api.service.AlarmService;
//import com.ewp.examples.api.vo.AlarmVo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.dubbo.config.annotation.DubboService;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
///**
// * @author hlq
// * 告警
// */
//@DubboService(version = ExamplesApiVersion.VERSION_1, group = ExamplesApiVersion.GROUP_EWP)
//@Slf4j
//public class AlarmServiceImpl implements AlarmService {
//    public static final String SECRET = "SEC613e794eef737cb09f1fa1f50859e09868e4790354e188146299e6664262b521";
//    public static final String WEBHOOK = "https://oapi.dingtalk.com/robot/send?access_token=13e61b69743aa3bcaefb7554ed25af309c4e67ea953745e70608ea51de3463fb";
//
//    /**
//     * sendMessage
//     *
//     * @param alarmList alarmList
//     */
//    @Override
//    public void sendMessage(List<AlarmVo> alarmList) {
//        try {
//            Long timestamp = System.currentTimeMillis();
//            String stringToSign = timestamp + "\n" + SECRET;
//            Mac mac = Mac.getInstance(HmacAlgorithm.HmacSHA256.getValue());
//            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), HmacAlgorithm.HmacSHA256.getValue()));
//            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
//            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), String.valueOf(StandardCharsets.UTF_8));
//
//            String serverUrl = WEBHOOK + "&timestamp=" + timestamp + "&sign=" + sign;
//            DingTalkClient client = new DefaultDingTalkClient(serverUrl);
//            OapiRobotSendRequest request = new OapiRobotSendRequest();
//            request.setMsgtype("text");
//            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
//
//            text.setContent(getContent(alarmList));
//            request.setText(text);
//
//            client.execute(request);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    private String getContent(List<AlarmVo> alarmList) {
//        StringBuilder sb = new StringBuilder();
//        for (AlarmVo param : alarmList) {
//            sb.append("scopeId: ").append(param.getScopeId())
//                    .append("\nscope: ").append(param.getScope())
//                    .append("\n目标 Scope 的实体名称: ").append(param.getName())
//                    .append("\nScope 实体的 ID: ").append(param.getId0())
//                    .append("\nid1: ").append(param.getId1())
//                    .append("\n告警规则名称: ").append(param.getRuleName())
//                    .append("\n告警消息内容: ").append(param.getAlarmMessage())
//                    .append("\n告警时间: ").append(param.getStartTime())
//                    .append("\n\n---------------\n\n");
//        }
//
//        return sb.toString();
//    }
//
//
//}
