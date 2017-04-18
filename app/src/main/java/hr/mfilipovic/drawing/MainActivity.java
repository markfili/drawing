package hr.mfilipovic.drawing;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private int value;


//    ToggleButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ValueBar valueBar = (ValueBar) findViewById(R.id.valueBar);

        valueBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value == 100) {
                    value = 0;
                }
                ((ValueBar) v).setValue(value += 5);
            }
        });

//        View.OnLongClickListener listener = new View.OnLongClickListener() {
//
//            // Defines the one method for the interface, which is called when the View is long-clicked
//            public boolean onLongClick(View v) {
//                dragData(v);
//                return false;
//            }
//        };
//
//        View.OnDragListener dragListener = new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                Log.i(TAG, "onDrag: " + event.getAction());
//                return false;
//            }
//        };
//
//
//        LinearLayout parent = (LinearLayout) findViewById(R.id.parent);
//        for (int i = 0; i < parent.getChildCount(); i++) {
//            parent.getChildAt(i).setOnDragListener(dragListener);
//            parent.getChildAt(i).setOnLongClickListener(listener);
//            Log.i(TAG, "onCreate: set to drag child listen");
//        }

//        toggle = (ToggleButton) findViewById(R.id.toggle_button);
//        toggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "onClick " + ((ToggleButton) v).isChecked());
//            }
//        });
//        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.i(TAG, "onCheckedChanged");
//            }
//        });
//
//        Button trigger = (Button) findViewById(R.id.trigger_button);
//        trigger.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                toggle.setChecked(!toggle.isChecked());
//            }
//        });
    }


    private void dragData(View v) {
        // Create a new ClipData.
        // This is done in two steps to provide clarity. The convenience method
        // ClipData.newPlainText() can create a plain text ClipData in one step.

        // Create a new ClipData.Item from the ImageView object's tag
        ClipData.Item item = new ClipData.Item("item");

        ClipData data = new ClipData(new ClipDescription("label", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}), item);

        // Instantiates the drag shadow builder.
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);

        // Starts the drag
        v.startDrag(data,  // the data to be dragged
                myShadow,  // the drag shadow builder
                null,      // no need to use local data
                0     // flags (not currently used, set to 0)
        );
    }
}
