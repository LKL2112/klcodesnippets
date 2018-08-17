package S2T;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

public class SpeechToTextGoogleImpl implements SpeechToText {

	@Override
	public String convertAudioFile(String fileName) {
		String textFromAudio = "ERROR:  Unable to convert the given voicemail.";
		try {
			SpeechClient speechClient = SpeechClient.create();

			ByteString audioBytes = readAudioBytes(fileName);

			// build the audio structure to pass to the transcription engine...
			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

			List<String> languageList = buildLanguageList();

			// initialize a map of transcribed objects...
			Map<String, SpeechRecognitionAlternative> transcriptions = new HashMap<String, SpeechRecognitionAlternative>();

			// loop over each language code defined above...
			for (String languageCode : languageList) {
				System.out.print(languageCode + " : ");

				// describe the audio in a configuration object...
				RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.FLAC)
						.setSampleRateHertz(16000).setLanguageCode(languageCode).setEnableAutomaticPunctuation(true)
						.build();

				// transcribe the audio and build a map of the transcriptions...
				RecognizeResponse response = speechClient.recognize(config, audio);
				buildTranscriptionMap(transcriptions, languageCode, response);
			}

			textFromAudio = findBestTranscription(transcriptions).getTranscript();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return textFromAudio;
	}

	private List<String> buildLanguageList() {
		// set up a list of languages we support for transcription...
		List<String> languageList = new ArrayList<>();
		languageList.add("en");
		languageList.add("es");
		return languageList;
	}

	private ByteString readAudioBytes(String fileName) throws IOException {
		// read the audio file into memory...
		Path path = Paths.get(fileName);
		byte[] data = Files.readAllBytes(path);
		ByteString audioBytes = ByteString.copyFrom(data);
		return audioBytes;
	}

	private void buildTranscriptionMap(Map<String, SpeechRecognitionAlternative> transcriptions, String languageCode,
			RecognizeResponse response) {
		for (SpeechRecognitionResult result : response.getResultsList()) {
			for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
				transcriptions.put(languageCode, alternative);
				System.out.println(alternative.getConfidence() + " : " + alternative.getTranscript());
			}
		}
	}

	// a method for determining the most accurate transcription based on the confidence factor of each element...
	private SpeechRecognitionAlternative findBestTranscription(Map<String, SpeechRecognitionAlternative> transcriptions) {
		SpeechRecognitionAlternative mostConfident = SpeechRecognitionAlternative.getDefaultInstance();

		for (SpeechRecognitionAlternative alternative : transcriptions.values()) {
			if (alternative.getConfidence() > mostConfident.getConfidence()) {
				mostConfident = alternative;
			}
		}
		return mostConfident;
	}
}
