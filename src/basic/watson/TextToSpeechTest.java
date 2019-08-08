package basic.watson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

/*
	IBM의 Text to Speech 서비스는 IBM의 음성합성기술을 사용하여
	다양한 언어, 방언 및 음성으로 텍스트를 자연스러운 발음으로 합성하는 API를 제공한다.
	이 서비스는 각 언어에 대해 남성 또는 여성, 또는 둘 다 지원한다.
*/
public class TextToSpeechTest {
	
	// IMB에서 회원 등록 후 해당 서비스를 사용할 수 있는 라이센스 ID와 PassWord를 제공 받아서 사용한다.
	private String UserName = "f2ca0e6b-75f2-4b1c-8b42-b2cafe7e8f7d";
	private String PassWord = "XSELj8h7gPIY";
	
	// TextToSpeech 서비스 객체를 저장할 변수 선언
	private TextToSpeech service;
	
	// 서비스 설정을 하는 메서드
	public void setService() {
		service  = new TextToSpeech();
		service.setUsernameAndPassword(UserName, PassWord);
	}
	
	/*
		서비스 헤더 설정 메서드
			- 왓슨은 기본적으로 서비스 사용에 대한 로그를 남겨 서비스를 개선하는데 사용하고 있다.
			- 만약 왓슨에서 서비스의 내용을 바꾸길 원하지 않으면 헤더에 그 내용을 명시해 주어야 한다.
	*/
	public void setHeader() {
		Map<String, String> headers = new HashMap<String, String>();
		
		// true는 허용, false는 불허를 뜻한다.
		headers.put("X-Watson-Learning-Opt-Out", "false");
		service.setDefaultHeaders(headers);
	}
	
	// 음성 타입을 검색하는 메서드
	public void getVoice() {
		ServiceCall<List<Voice>> serviceCall = service.getVoices();
		
		List<Voice> voiceList = serviceCall.execute(); // Voice의 List를 구한다.
		
		System.out.println("Watson에서 제공하는 음성 타입들...");
		for(Voice voice : voiceList) {
			System.out.println(voice);
		}
	}
	
	// 서비스를 실행하는 메서드
	public void executeService() {
		String text = "Hello! My name is Allison, Nice to meet you. bye bye bye bye bye bye bye bye bye bye bye bye";
		
		InputStream stream = service.synthesize(text, Voice.EN_ALLISON, AudioFormat.WAV).execute();
		
		// 음성데이터를 재생(저장)
		try {
			InputStream in = WaveUtils.reWriteWaveHeader(stream);
			
			OutputStream os = new FileOutputStream("D:/D_Other/test.wav");
			
			byte[] tmp = new byte[1024];
			int length = 0;
			while((length = in.read(tmp))!=-1) {
				os.write(tmp, 0, length);
			}
			
			System.out.println("작업 완료...");
			os.close();
			in.close();
			stream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TextToSpeechTest test = new TextToSpeechTest();
		test.setService();
		test.setHeader();
		//test.getVoice();
		test.executeService();
	}

}
