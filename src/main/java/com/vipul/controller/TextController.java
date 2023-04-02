package com.vipul.controller;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.storage.blob.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
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

    @Value("${filename}")
    String filename;

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
        // this is a Rest call to logic App
        URI uri = new URI(logicAppUrl);
        return restTemplate.getForEntity(uri, String.class).getBody();
    }

    @GetMapping("/get-default-secret")
    public String getDefaultKeyVault() {
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
        System.out.println(filename);
//        String keyVaultUri = "https://keyvaultvjj.vault.azure.net/";
//        SecretClient secretClient = new SecretClientBuilder().vaultUrl(keyVaultUri)
//                                                             .credential(new ManagedIdentityCredentialBuilder().build())
//                                                             .buildClient();
//        KeyVaultSecret retrievedSecret = secretClient.getSecret("filename");
//        return retrievedSecret.getValue();
        return filename;
    }

    @PostMapping("/get-blob-properties")
    public String getBlobProperties(@RequestParam String storageName, @RequestParam String containerName, @RequestParam String blobName) {
        String storageAccountUri = "https://" + storageName + ".blob.core.windows.net";
        String containerUri = storageAccountUri + "/" + containerName;
        String blobUri = containerUri + "/" + blobName;

        BlobClientBuilder blobClientBuilder = new BlobClientBuilder();
        BlobClient blobClient = blobClientBuilder.endpoint(blobUri).buildClient();

        return blobClient.getBlobUrl();
    }

    @PostMapping("/copy-blob")
    public String copyBlob(@RequestParam String storageName, @RequestParam String containerName,
                           @RequestParam String blobName) {
        String storageAccountUri = "https://" + storageName + ".blob.core.windows.net/";
        String containerUri = storageAccountUri + containerName + "/";
        String copyingContainerName = "copyfilecontainer";

        DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                                                        .endpoint(storageAccountUri)
                                                        .credential(defaultCredential)
                                                        .buildClient();

        BlobContainerClient originalBlobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobContainerClient copyingBlobContainerClient = blobServiceClient.getBlobContainerClient(copyingContainerName);

        BlobClient originalBlobClient = originalBlobContainerClient.getBlobClient(blobName);
        BlobClient copyingBlobClient = copyingBlobContainerClient.getBlobClient(originalBlobClient.getBlobName());

        // get the reference of copying container and from that get reference of BLobClient with filename
        copyingBlobClient.copyFromUrl(originalBlobClient.getBlobUrl());

        return originalBlobClient.getProperties().toString();
    }

}