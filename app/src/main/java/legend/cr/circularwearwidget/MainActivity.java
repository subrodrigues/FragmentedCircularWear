package legend.cr.circularwearwidget;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Random random = new Random();
        findViewById(R.id.main_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetricModel[] metrics = new MetricModel[random.nextInt(4) + 1];
                for(int i = 0; i < metrics.length; i++){
                    metrics[i] = new MetricModel(100, random.nextInt(100));
                }

                ((SectionedCircularView) findViewById(R.id.segmented_metrics)).setupFields(metrics);
            }
        });
        // Enables Always-on
        setAmbientEnabled();
    }

}
