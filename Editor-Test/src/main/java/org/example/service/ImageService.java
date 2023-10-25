package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {
    @Value("${uploadfile.dir}")
    private String fileUploadPath;

    public String save(MultipartFile file) throws Exception {
        return this.fileSave(file);
    }

    public String getUploadPath() {
        return this.fileUploadPath;
    }

    public String getFilePath(String fileName) {
        return this.fileUploadPath + fileName;
    }

    private String fileSave(MultipartFile multipartFile) throws IOException {
        File file = new File(this.fileUploadPath);

        if (!file.exists()) {
            file.mkdirs();
        }

        // Save 파일명 생성
        UUID uuid = UUID.randomUUID();
        String originalFileName = multipartFile.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String saveFileName = uuid.toString() + fileExtension;
        File saveFile = new File(this.fileUploadPath, saveFileName);
        FileCopyUtils.copy(multipartFile.getBytes(), saveFile);

        return saveFileName;

    }
}
