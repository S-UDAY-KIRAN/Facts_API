package com.h2data.dataTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Random;

@RestController
public class FactsController {
    @Autowired FactsRepository factsRepository;

    @GetMapping("/")
    public String Hello()
    {
        return "<html><body><center>" +
                "<p>localhost:8080/cat for cat facts</p>" +
                "<p>localhost:8080/dog for dog facts</p>" +
                "<p>localhost:8080/getDB for Database </p>" +
                "</center></body></html>";
    }
    @RequestMapping("/cat")
    public String catFact()
    {

        RestTemplate restTemplate = new RestTemplate();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getRemoteAddr();
        String timeStamp = new SimpleDateFormat("HH.mm.ss.dd.MM.yyyy").format(new java.util.Date());
        String item = restTemplate.getForObject("https://catfact.ninja/fact",String.class);
        assert item != null;
        item = item.replaceAll("[\\[\\]{}\"\n\u2019s]","");
        item = item.replaceAll("fact:","");
        String[] factItem = item.split("length");
        Facts facts = new Facts(factItem[0].strip(),ipAddress,timeStamp);
        factsRepository.save(facts);
        return factItem[0].strip();
    }

    @RequestMapping("/dog")
    public String dogFact()
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getRemoteAddr();
        String timeStamp = new SimpleDateFormat("HH.mm.ss.dd.MM.yyyy").format(new java.util.Date());
        String item = restTemplate.getForObject("https://dog-facts-api.herokuapp.com/api/v1/resources/dogs?number=1",String.class);
        assert item != null;
        item = item.replaceAll("[\\[\\]{}\"\n\u2019s]","");
        String[] factsItem = item.split("fact:");
        factsItem[0] = factsItem[new Random().nextInt(1,factsItem.length)].strip();
        Facts facts = new Facts(factsItem[0],ipAddress,timeStamp);
        factsRepository.save(facts);
        return factsItem[0];
    }

    @RequestMapping("/getDB")
    public Iterable<Facts> getDB()
    {
        return   factsRepository.findAll();
    }
}
