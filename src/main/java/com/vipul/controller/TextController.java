package com.vipul.controller;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@RestController
public class TextController {

    @Value("${vipul.jain}")
    String age;

    @Value("${logicAppUrl}")
    String logicAppUrl;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/getRandomValue/{index}")
    public String getRandomValue(@PathVariable int index) {
        List<String> list = Arrays.asList("One", "Two", "Three", "Four");
        if (index <= list.size()) {
            return list.get(index) + ", " + age;
        }
        return "Value Does Not Exist";
    }

    @GetMapping("/")
    public String defaultEntry() {
        return "You are on Welcome Page.";
    }

    @GetMapping("/get-file-content")
    public String getFileContent() throws URISyntaxException {
        //return restTemplate.exchange(logicAppUrl, HttpMethod.GET, null, String.class).getBody();
        URI uri = new URI(logicAppUrl);
        return restTemplate.getForEntity(uri, String.class).getBody();
    }

    @GetMapping("/get-default-secret")
    public String getDefaultKeyVault() throws URISyntaxException {
        // get key vault secret from Azure
        // User have Role to read secret, tested from logic App
        String keyVaultUri = "https://keyvaultvjj.vault.azure.net/";
        SecretClient secretClient = new SecretClientBuilder().vaultUrl(keyVaultUri)
                                                             .credential(new DefaultAzureCredentialBuilder().build())
                                                             .buildClient();
        KeyVaultSecret retrievedSecret = secretClient.getSecret("filename");
        return retrievedSecret.getValue();
    }

    @GetMapping("/get-managed-secret")
    public String getManagedKeyVault() throws URISyntaxException {
        // get key vault secret from Azure
        // User have Role to read secret, tested from logic App
        String keyVaultUri = "https://keyvaultvjj.vault.azure.net/";
        SecretClient secretClient = new SecretClientBuilder().vaultUrl(keyVaultUri)
                                                             .credential(new ManagedIdentityCredentialBuilder().build())
                                                             .buildClient();
        KeyVaultSecret retrievedSecret = secretClient.getSecret("filename");
        return retrievedSecret.getValue();
    }

}