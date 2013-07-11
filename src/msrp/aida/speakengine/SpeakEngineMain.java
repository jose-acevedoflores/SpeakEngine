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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SpeakEngineMain extends Activity implements TextToSpeech.OnInitListener {

	private static final int CHECK_TTS_DATA_REQUEST_CODE = 89;
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 3;

	private HashMap<String, String> map;
	private TextToSpeech tts;
	
	private Button button;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CHECK_TTS_DATA_REQUEST_CODE) {

			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				tts = new TextToSpeech(getApplicationContext(), this, "com.ivona.tts"); 
				
				tts.setLanguage(Locale.US);
				
				for( TextToSpeech.EngineInfo e : tts.getEngines())
					System.out.println("Engine: "+e.name);
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

	@SuppressLint("NewApi")
	public void init()
	{
		
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for(TextToSpeech.EngineInfo e : tts.getEngines())
					System.out.println("Engine name: " + e.name);
				tts.speak("Esto es una prueba ", TextToSpeech.QUEUE_FLUSH, null);
			}
		});
		
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
			//tts.speak("This is a test to hear my voice", TextToSpeech.QUEUE_ADD, map );
		}
		else
		{
			Log.d("DEBUG", "No TTS working");
		}

	}

	/**
	 * NOTES: in the HashMap map, the value that we store when we pass it to the speak method
	 * 		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "onRespond");
			tts.speak("This is a test to hear my voice", TextToSpeech.QUEUE_ADD, map );
		In this example is "onRespond", the value of utteranceId will be the value stored in the map.	
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

