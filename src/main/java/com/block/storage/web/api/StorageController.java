package com.block.storage.web.api;

import com.block.storage.application.AppData;
import com.block.storage.encrypt.EncryptUtils;
import com.block.storage.log.LogUtils;
import com.block.storage.message.FileMessage;
import com.block.storage.message.Message;
import com.block.storage.message.MessageType;
import com.block.storage.message.RetrieveFileMessage;
import com.block.storage.socket.MessageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

@RestController
public class StorageController {
    int port = 5555;

    @RequestMapping(
            value = "/upload",
            method = RequestMethod.POST)
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        // encrypt
        byte[] fileData = EncryptUtils.encrypt(file.getBytes());

        // calculate hash
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = md.digest(fileData);

        // choose node for storage
        Map<Long, String> nodes = AppData.getNodeIdToAddressMap();
        int randomIndex = new Random().nextInt() % nodes.size();
        long storageNodeId = (long) nodes.keySet().toArray()[randomIndex];
        String host = nodes.get(storageNodeId);

        // write to log fileName, storageNodeId, hash
        LogUtils.writeToStorageLog(file.getOriginalFilename() + "" + storageNodeId + " " + Arrays.toString(hash));

        // send to another node
        MessageService.sendMessage(new FileMessage(MessageType.SAVE_FILE_MESSAGE, AppData.getNodeId(), file.getOriginalFilename(), fileData), host, port);

    }


    @RequestMapping(
            value = "/download/{fileName:.+}",
            method = RequestMethod.GET)
    public ResponseEntity<Resource> getFile(@PathVariable String fileName, HttpServletRequest request) {

        // read from log host containing file
        String logEntry = LogUtils.findFileInfo(fileName);
        String host;
        if (logEntry != null) {
            host = logEntry.split(" ")[1];
        } else {
            // no such file
            return ResponseEntity.notFound().build();
        }

        // get file from another node
        Message responseMessage = MessageService.sendMessage(new RetrieveFileMessage(AppData.getNodeId(), fileName), host, port);
        FileMessage fileMessage = ((FileMessage) responseMessage);
        byte[] fileBody = fileMessage.getFileBody();

        // calculate hash
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = md.digest(fileBody);

        // compare hash
        if (!logEntry.split(" ")[2].equals(Arrays.toString(hash))) {
            // file was modified... (send appropriate response)
            throw new RuntimeException("file was modified");
        }

        // decrypt
        byte[] decryptedFileBody = EncryptUtils.decrypt(fileBody);

        // save temp file
        Path tempFilePath = null;
        try {
            tempFilePath = Files.write(Paths.get("./temp/" + fileName), decryptedFileBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load file as Resource
        Resource resource = null;
        try {
            resource = new UrlResource(tempFilePath.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

        // delete temp file
    }
}
