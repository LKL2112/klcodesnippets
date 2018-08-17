package S2T;

public class App {
	public static void main(String... args) throws Exception {
//		SpeechToText googleS2T = new SpeechToTextGoogleImpl();
//		for (String arg : args) {
//			System.out.println("Best Translation: " + googleS2T.convertAudioFile(arg));
//			System.out.println("-------------------------------------");
//		}
		
		
		SpeechToText amazonS2T = new SpeechToTextAmazonImpl();
		for (String arg : args) {
			System.out.println("Best Translation: " + amazonS2T.convertAudioFile(arg));
			System.out.println("-------------------------------------");
		}
	}
}