package basic.watson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechModel;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

/*
   IBM Watson의 Speech to Text 서비스는 IBM의 음성 인식기능을
   응용 프로그램에 추가할 수 있는 API를 제공한다.
   이 서비스는 다양한 언어 및 오디오 형식의 음성을 빠르게 Text로 변환한다.
   모든 응답내용은 UTF-8 인코딩 형식의 JSON형식으로 반환한다.
 */

public class SpeechToTextTest {
   
   private String UserName = "d86e14ee-31b3-4478-bce7-32b242ca4ca5";
   private String PassWord = "TmbVrDWaF7IR";
   
   // SpeechToText 서비스 객체 변수 선언
   private SpeechToText service;
   
   // 서비스 옵션을 저장할 변수 선언
   private RecognizeOptions options;
   
   // 서비스 콜백 객체 변수 선언
   private BaseRecognizeCallback callback;
   
   
   // 서비스를 설정하는 메서드
   // - IBM Watson에 등록한 사용자 계정과 패스워드로 서비스에 접속한다. 
   public void setService() {
      service = new SpeechToText();
      service.setUsernameAndPassword(UserName, PassWord);
   }
   
   // 서비스 헤더 설정
   public void setHeader() {
      Map<String, String> headers = new HashMap<String, String>();
      
      // true-허용, false-불허
      headers.put("X-Watson-Learning-Opt-Out", "false");
      
      service.setDefaultHeaders(headers);
   }
   
   // 서비스에서 사용할 수 있는 언어 모델을 검색하는 메서드
   public void getModel() {
      // 서비스 요청 모델 구하기
      ServiceCall<List<SpeechModel>> serviceCall = service.getModels();
      
      // 서비스 요청을 실행해서 서비스 모델 리스트를 구한다.
      List<SpeechModel> speechModelList = serviceCall.execute();
      
      System.out.println("IBM Watson에서 제공하는 언어모델들...");
      for(SpeechModel model : speechModelList) {
         System.out.println(model);
      }
   }
   
   // 서비스 옵션을 설정하는 메서드
   public void setOption() {
      // - model ==> 사용할 언어 모델
      // - contentType ==> 사용할 컨텐츠 타입 설정
      //         audio/mp3, audio/mpeg, audio/ogg, audio/wav, audio/webm 등등...
      // - interimResults ==> 중간 결과를 반환할 지 여부를 설정
      //         true일 경우 - 임시결과는 JSON SpeechRecognitionResults객체의 스트림으로 반환된다.
      //         false일 경우 - 응답은 최종 결과만 있는 단일 SpeechRecognitionResults객체로 반환된다.
      // - maxAlternatives ==> 반환될 대체 성적 증명서의 최대 수(기본값:1)
      
      options = new RecognizeOptions.Builder()
            .model("en-US_NarrowbandModel") //ko_KR_BroadbandModel
            .contentType("audio/mp3")
            .interimResults(true)
            .maxAlternatives(3)
            .build();
   }
   
   // 서비스 실행 후 처리할 콜백을 지정하는 메서드
   public void setCallBack() {
      callback = new BaseRecognizeCallback() {
         
         // 문자 변환시 처리할 내용을 설정한다.
         @Override
         public void onTranscription(SpeechResults speechResults) {
            for(Transcript transcript : speechResults.getResults()) {
               String text = transcript.getAlternatives().get(0).getTranscript();
               System.out.println(text);
            }
         }
         
         // 연결 종료시 처리할 내용 설정
         @Override
         public void onDisconnected() {
            System.exit(0);
         }
      };
   }
   
   // 서비스를 실행하는 메서드
   public void executeService() {
      try {
         FileInputStream fis = new FileInputStream(getClass().getResource("speech.mp3").getPath());
         service.recognizeUsingWebSocket(fis, options, callback);
         
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) {
      SpeechToTextTest test = new SpeechToTextTest();
      
      test.setService();
      test.setHeader();
      //test.getModel();
      test.setOption();
      test.setCallBack();
      test.executeService();

   }

}