package e.wilso.audiotester;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      findViewById(R.id.start_test).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, AudioTesters.class));
         }
      });
   }
}
