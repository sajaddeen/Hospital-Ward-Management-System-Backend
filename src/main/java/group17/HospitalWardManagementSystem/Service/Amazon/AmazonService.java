package group17.HospitalWardManagementSystem.Service.Amazon;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;
import group17.HospitalWardManagementSystem.Config.AWSConfiguration;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Service
@Slf4j
public class AmazonService {

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3Client;


    public String uploadFile(MultipartFile file){
        File fileObj=convertMultipartfileToFile(file);
        String fileName= System.currentTimeMillis()+" "+file.getOriginalFilename();
        amazonS3Client.putObject(bucketName,fileName,fileObj);


        String fileUrl = amazonS3Client.getUrl(bucketName, fileName).toString();
        return fileUrl;

    }

    public File downloadFile(String bucketName, String key) throws IOException {
        S3Object object = amazonS3Client.getObject(bucketName, key);
        File file = new File(key); // You can specify a different file path/name here if needed
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = object.getObjectContent().read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        object.close();
        return file;
    }

    public String deleteFile(String filename){
        amazonS3Client.deleteObject(bucketName,filename);
        return "File was deleted";
    }

    public File convertMultipartfileToFile(MultipartFile file){
        File convertedFile=new File(file.getOriginalFilename());
        try(FileOutputStream fos=new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
        }catch (IOException e){
            System.out.println(e);
        }
        return  convertedFile;
    }
}
