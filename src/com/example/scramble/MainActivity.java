/*
 * Word Scramble Main Activity
 * Coded By: RaptoR
 */

package com.example.scramble;

import java.util.Random;
import java.util.Scanner;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnTouchListener{

	private String word = new String();
	private String wordList[] = new String[67];
	private Random generator = new Random();
	private String answerString = new String();
	private EditText answerText;
	private TextView info;
	private LinearLayout scrambledLayout;
	private MediaPlayer correctSound;
	private MediaPlayer wrongSound;
	private Button nextButton;
	private Button clearButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(this);
		clearButton = (Button) findViewById(R.id.clear);
		clearButton.setOnClickListener(this);
		answerText = (EditText) findViewById(R.id.answer);
		correctSound = MediaPlayer.create(getApplicationContext(), R.raw.correctsound);
		wrongSound = MediaPlayer.create(getApplicationContext(), R.raw.wrongsound);

		//Populating List of Words from dictionary
		Scanner scan = new Scanner(getResources().openRawResource(R.raw.words));
		try {
			int i = 0;
			while(scan.hasNext()){
				wordList[i++] = scan.next();
			}			
		} catch(Exception e){
			//e.printStackTrace();
		}
		finally {
			scan.close();
		}
		initializeGame();
	}


	public void initializeGame(){
		
		clearButton.setEnabled(Boolean.FALSE);
		
		//getting the word to scramble
		word = getNewWord();
		//scrambling the word
		String scrambledWord = scramble(word);

		//getting Layout for Scrambled word
		scrambledLayout = (LinearLayout) findViewById(R.id.scrambled);
		
		//initializing information TextView
		info = (TextView) findViewById(R.id.inforamtion);


		//Populating TextViews
		for(int i = 0; i < scrambledWord.length(); i++) {
			TextView letter = new TextView(this);
			letter.setText("");
			letter.setText(Character.toString(scrambledWord.charAt(i)));
			letter.setTextSize(75);
			letter.setPadding(7, 7, 7, 7);
			letter.setOnClickListener(this);
			letter.setId(i);
			letter.setOnTouchListener(this);
			scrambledLayout.addView(letter);
		}
	}

	public void onClick(View v){
		
		TextView clicked = (TextView) findViewById(v.getId());
		
		if(answerText.getText().toString().length() == 0){
			clearButton.setEnabled(Boolean.FALSE);
		}

		if(clearButton.getId() == v.getId()){
			if(answerText.getText().toString().length() == 1){
				clearButton.setEnabled(Boolean.FALSE);
			}
			answerText.setText(answerText.getText().toString().substring(0, answerText.getText().toString().length()-1));
			answerString = answerString.substring(0, answerString.length()-1);
		}

		else if(nextButton.getId() == v.getId()) {
			clearButton.setEnabled(Boolean.FALSE);
			scrambledLayout.removeAllViews();
			answerText.setText("");
			answerString = "";
			answerText.setTextColor(Color.rgb(0, 0, 0));
			info.setTextColor(Color.DKGRAY);
			info.setText("Unscramble the below word");
			info.setBackgroundColor(Color.TRANSPARENT);
			initializeGame();
		}
		else {
			clearButton.setEnabled(Boolean.TRUE);
			answerText.setText(answerText.getText().toString() + clicked.getText());
			answerString += clicked.getText();

			try {
				if(answerString.length() == word.length()){
					if(answerString.equalsIgnoreCase(word)){
						clearButton.setEnabled(Boolean.FALSE);
						info.setBackgroundColor(Color.rgb(33, 196, 18));
						info.setTextColor(Color.WHITE);
						info.setText("Correct!");
						answerText.setTextColor(Color.rgb(33, 196, 18));
						correctSound.start();

						new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								scrambledLayout.removeAllViews();
								answerText.setText("");
								answerString = "";
								answerText.setTextColor(Color.rgb(0, 0, 0));
								info.setTextColor(Color.DKGRAY);
								info.setText("Unscramble the below word");
								info.setBackgroundColor(Color.TRANSPARENT);
								initializeGame();
							}
						}, 1200);
					}
					else {
						clearButton.setEnabled(Boolean.FALSE);
						info.setBackgroundColor(Color.rgb(255, 0, 0));
						info.setTextColor(Color.WHITE);
						info.setText("Incorrect!");
						answerText.setTextColor(Color.rgb(255, 0, 0));
						wrongSound.start();
						//vibrator.vibrate(400);

						new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								answerString = "";
								answerText.setText("");
								answerText.setTextColor(Color.rgb(0, 0, 0));
								info.setTextColor(Color.DKGRAY);
								info.setText("Give it another shot!");
								info.setBackgroundColor(Color.TRANSPARENT);
							}
						}, 1200);
					}
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/*
	 * Returns a random word from the Populated WordList Array of Strings
	 */
	public String getNewWord(){
		int randomWord = generator.nextInt(wordList.length);
		String temp = wordList[randomWord];
		return temp;
	}

	/*
	 * Accepts String, and returns a scrambled String
	 */
	public String scramble(String wordToScramble){
		String scrambled = "";
		int randomNumber;

		boolean letter[] = new boolean[wordToScramble.length()];

		do {
			randomNumber = generator.nextInt(wordToScramble.length());
			if(letter[randomNumber] == false){
				scrambled += wordToScramble.charAt(randomNumber);
				letter[randomNumber] = true;
			}
		} while(scrambled.length() < wordToScramble.length());

		if(scrambled.equals(wordToScramble))
			scramble(word);

		return scrambled;
	}

	@Override
	public boolean onTouch(View v, MotionEvent motion) {
		TextView touched = (TextView) findViewById(v.getId());
		
		if(motion.getAction() == MotionEvent.ACTION_DOWN){
			touched.setTextColor(Color.rgb(0, 189, 252));
		}
		else if(motion.getAction() == MotionEvent.ACTION_UP){
			touched.setTextColor(Color.rgb(0, 0, 0));
		}
		return false;
	}
}
