package e.wilso.audiotester;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import e.wilso.audiotester.modules.AudioView;
import e.wilso.audiotester.modules.AudioViewLayout;

public class MainActivity extends AppCompatActivity {

   AudioView audioView;
   AudioViewLayout audioViewLayout;
   Button btn_test;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();

      btn_test = findViewById(R.id.btn_test);
      btn_test.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AudioViewLayout.class);
            startActivity(intent);
         }
      });

      /*audioView = new AudioView(this);
      setContentView(audioView);*/
   }

   @Override
   protected void onResume() {
      super.onResume();
      //audioView = new AudioView(this);
      //setContentView(audioView);
      Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
   }

   @Override
   protected void onPause() {
      super.onPause();
      Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
   }
}
