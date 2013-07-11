package msrp.aida.speakengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;

public class SpeakEngineMain extends Activity implements TextToSpeech.OnInitListener {

	private static final int CHECK_TTS_DATA_REQUEST_CODE = 89;
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 3;

	private HashMap<String, String> map;
	private TextToSpeech tts;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CHECK_TTS_DATA_REQUEST_CODE) {

			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				tts = new TextToSpeech(getApplicationContext(), this); // 1
				tts.setLanguage(Locale.US);
			} else {
				// TTS data not yet loaded, try to install it
				Intent ttsLoadIntent = new Intent();
				ttsLoadIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(ttsLoadIntent);
			}


		}
		else if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK)
		{
			// Fill the list view with the strings the recognizer thought it could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);

			for(String str : matches)
			{
				System.out.println(str);
				if(str.equals("yes") || str.equals("ok"))
				{

				}
			}
		}	
	}

	public void init()
	{
		//TTS engine part
		map = new HashMap<String, String>();
		//Verify if TTS is available 
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, CHECK_TTS_DATA_REQUEST_CODE);	
	}

	@Override
	public void onInit(int status) {
		if(status == TextToSpeech.SUCCESS)
		{
			tts.setOnUtteranceProgressListener(new MyUtteranceListener());
			map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "onRespond");
			tts.speak("This is a test to hear my voice", TextToSpeech.QUEUE_ADD, map );
		}
		else
		{
			Log.d("DEBUG", "No TTS working");
		}

	}

	/**
	 * 
	 * @author joseacevedo
	 *
	 */
	@SuppressLint("NewApi")
	class MyUtteranceListener extends UtteranceProgressListener{

		@Override
		public void onDone(String utteranceId) {
			Log.d("DEBUG", "Utterdance Done " + utteranceId);

			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

		}

		@Override
		public void onError(String utteranceId) {
			// TODO Auto-generated method stub
			Log.i("DEBUG", "Error: "+utteranceId);

		}

		@Override
		public void onStart(String utteranceId) {

			Log.d("DEBUG", "Utterance Started " + utteranceId);

		}

	}

}

