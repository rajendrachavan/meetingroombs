package neo.spring5.meetingroombooking.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import neo.spring5.meetingroombooking.commons.ResponseDTO;
import neo.spring5.meetingroombooking.commons.SecureCryptPasswdUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/utility")
public class UtilityController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/create/crypt-password")
    public ResponseDTO<String> generateCryptPasswd() throws Exception {

        log.info("ExtController - Starting GenerateCryptPasswd Method");
        ResponseDTO<String> responseDTO = new ResponseDTO<>();

        String passwd = SecureCryptPasswdUtils.generateRandomChars();
        responseDTO.setData(passwd);
        responseDTO.setStatus(HttpStatus.OK.value());
        responseDTO.setMessage("New Cryptographic Password Generated Successfully!");

        log.info("ExtController - Exiting GenerateCryptPasswd Method");
        return responseDTO;
    }

}