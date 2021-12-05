package com.example.tts;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements TextPlayer, View.OnClickListener {

        private final Bundle params = new Bundle();
        private final BackgroundColorSpan colorSpan = new BackgroundColorSpan(Color.YELLOW);
        private TextToSpeech tts;
        private Button playBtn;
        private Button pauseBtn;
        private Button stopBtn;
        private EditText inputEditText;
        private TextView contentTextView;
        private PlayState playState = PlayState.STOP;
        private Spannable spannable;
        private int standbyIndex = 0;
        private int lastPlayIndex = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            initTTS();
            initView();
        }

        private void initView() {
            playBtn = findViewById(R.id.btn_play);
            pauseBtn = findViewById(R.id.btn_pause);
            stopBtn = findViewById(R.id.btn_stop);
            inputEditText = findViewById(R.id.et_input);
            contentTextView = findViewById(R.id.tv_content);

            playBtn.setOnClickListener(this);
            pauseBtn.setOnClickListener(this);
            stopBtn.setOnClickListener(this);
        }

        private void initTTS() {
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int state) {
                    if (state == TextToSpeech.SUCCESS) {
                        tts.setLanguage(Locale.ENGLISH);
                    } else {
                        showState("TTS 객체 초기화 중 문제가 발생했습니다.");
                    }
                }
            });

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {

                }

                @Override
                public void onDone(String s) {
                    clearAll();
                }

                @Override
                public void onError(String s) {
                    showState("재생 중 에러가 발생했습니다.");
                }

                @Override
                public void onRangeStart(String utteranceId, int start, int end, int frame) {
                    changeHighlight(standbyIndex + start, standbyIndex + end);
                    lastPlayIndex = start;
                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_play:
                    startPlay();
                    break;

                case R.id.btn_pause:
                    pausePlay();
                    break;

                case R.id.btn_stop:
                    stopPlay();
                    break;
            }
            showState(playState.getState());
        }

        @Override
        public void startPlay() {
            String content = inputEditText.getText().toString();
            if (playState.isStopping() && !tts.isSpeaking()) {
                setContentFromEditText(content);
                startSpeak(content);
            } else if (playState.isWaiting()) {
                standbyIndex += lastPlayIndex;
                startSpeak(content.substring(standbyIndex));
            }
            playState = PlayState.PLAY;
        }

        @Override
        public void pausePlay() {
            if (playState.isPlaying()) {
                playState = PlayState.WAIT;
                tts.stop();
            }
        }

        @Override
        public void stopPlay() {
            tts.stop();
            clearAll();
        }

        private void changeHighlight(final int start, final int end) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    spannable.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            });
        }

        private void setContentFromEditText(String content) {
            contentTextView.setText(content, TextView.BufferType.SPANNABLE);
            spannable = (SpannableString) contentTextView.getText();
        }

        private void startSpeak(final String text) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, params, text);
        }

        private void clearAll() {
            playState = PlayState.STOP;
            standbyIndex = 0;
            lastPlayIndex = 0;

            if (spannable != null) {
                changeHighlight(0, 0); // remove highlight
            }
        }

        private void showState(final String msg) {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPause() {
            if (playState.isPlaying()) {
                pausePlay();
            }
            super.onPause();
        }

        @Override
        protected void onResume() {
            if (playState.isWaiting()) {
                startPlay();
            }
            super.onResume();
        }

        @Override
        protected void onDestroy() {
            tts.stop();
            tts.shutdown();
            super.onDestroy();
        }
    }
