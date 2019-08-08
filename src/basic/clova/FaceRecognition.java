package basic.clova;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FaceRecognition {
   // 사용자 계정
   private String Client_Id = "HPhMS5z3Rlc1P1bvVauw";
   private String Client_Secret = "60fJyesyCs";
   
   // 서비스 연결 커넥션
   private HttpURLConnection con;
   
   // 얼굴 인식 URL 연결 설정
   public void setConnection() {
      // 유명인 얼굴 인식 URL
      String apiURL = "https://openapi.naver.com/v1/vision/celebrity";
      
      //"https://openapi.naver.com/v1/vision/face";
      
      try {
         URL url = new URL(apiURL);
         con = (HttpURLConnection) url.openConnection();
         con.setUseCaches(false); // 캐시 사용 안함
         con.setDoOutput(true);
         con.setDoInput(true);
      } catch (MalformedURLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   // 파일 전송 설정하는 메서드
   public void setFileTransfer() {
      // multipart request
      String boundary = "---" + System.currentTimeMillis() + "---";
      con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      con.setRequestProperty("X-Naver-Client-Id", Client_Id);
      con.setRequestProperty("X-Naver-Client-Secret", Client_Secret);
      
      OutputStream outputStream;
      try {
         outputStream = con.getOutputStream();
         
         PrintWriter writer = 
               new PrintWriter(new OutputStreamWriter(outputStream,"UTF-8"), true);
         String Line_Feed  = "\r\n";
         
         // 파일 추가
         String imgFile = FaceRecognition.class.getResource("actress.JPG").getPath();
         File uploadFile = new File(imgFile);
         
         String paramName = "image"; // 파라미터명은 image로 한다.
         String fileName = uploadFile.getName();
         
         writer.append("--" + boundary).append(Line_Feed);
         writer.append("Content-Disposition: form-data; name=\"" + paramName +
                  "\"; filename=\"" + fileName + "\"").append(Line_Feed);
         writer.append("Content-Type: " + 
                  URLConnection.guessContentTypeFromName(fileName)).append(Line_Feed);
         writer.append(Line_Feed);
         writer.flush();
         
         FileInputStream inputstream = new FileInputStream(uploadFile);
         byte[] buffer = new byte[4096];
         int byteRead = -1;
         while((byteRead = inputstream.read(buffer))!=-1) {
            outputStream.write(buffer, 0, byteRead);
         }
         outputStream.flush();
         inputstream.close();
         
         writer.append(Line_Feed).flush();
         writer.append("--" + boundary + "--").append(Line_Feed);
         writer.close();
         
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   // 응답 수신
   public void receiveResponse() {
      BufferedReader br = null;
      int responseCode;
      try {
         responseCode = con.getResponseCode();
         if(responseCode==200) { // 정상 처리
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
         } else { // 에러 발생
            System.out.println("Error!!!! 응답 코드 = " + responseCode);
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
         }
         
         String inputLine;
         if(br!=null) {
            StringBuffer response = new StringBuffer();
            while((inputLine = br.readLine())!=null) {
               response.append(inputLine);
            }
            br.close();
            System.out.println("응답 결과...");
            System.out.println(response.toString());
         }
         
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) {
      FaceRecognition faceTest = new FaceRecognition();
      
      faceTest.setConnection();
      faceTest.setFileTransfer();
      faceTest.receiveResponse();
   }

}