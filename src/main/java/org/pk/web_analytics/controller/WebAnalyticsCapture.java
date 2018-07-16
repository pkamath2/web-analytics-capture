package org.pk.web_analytics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.pk.web_analytics.bo.Analytics;
import org.pk.web_analytics.properties.WebAnalyticsProducerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WebAnalyticsCapture {

    @Autowired
    Environment env;

    @Autowired
    WebAnalyticsProducerProperties producerProperties;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = producerProperties.getAllKnownProperties();
        return props;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/web-analytics/save")
    public String saveAnalytics(@RequestParam String data,
                                @RequestHeader(value="User-Agent") String userAgent){

        System.out.println("data = [" + data + "], userAgent = [" + userAgent + "]");

        try {
            Map dataMap = objectMapper.readValue(data, HashMap.class);
            Analytics analyticsObj = new Analytics(dataMap.get("name").toString(), userAgent, dataMap.get("preferred_animal").toString(), dataMap.get("gender").toString());
            ProducerRecord<String, String> analyticsProducerRecord = new ProducerRecord<>("WEB_ANALYTICS_IN", new Double(Math.random()*100).toString(), objectMapper.writeValueAsString(analyticsObj));
            kafkaTemplate.send(analyticsProducerRecord);
        } catch (IOException e) {
            e.printStackTrace();
            return "failure";
        }
        return "success";
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/web-analytics/push")
    public void pushToReplay(){

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("WEB_ANALYTICS_REPLAY", "REPLAY_KEY", Math.random()+"");
        kafkaTemplate.send(producerRecord);

    }



}
