package hr.mfilipovic.drawing;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by marko on 27/01/17.
 */

public class MatchDetailsView extends View {
    String matchName;
    String code;
    String date;
    private Paint textColor;

    public MatchDetailsView(Context context) {
        super(context);
        init();
    }

    private void init() {
        matchName = "J.Tsonga - J.Tipsarevic";
        code = "3004";
        date = "11.10.2016. 13:45";

        textColor = new Paint();
        textColor.setColor(getResources().getColor(android.R.color.black));
    }

    public MatchDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MatchDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("MatchDetailsView", "onMeasure: " + widthMeasureSpec + " height " + heightMeasureSpec);
        int textSize = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? getMeasuredHeight() : getMeasuredWidth();
        textColor.setTextSize((float) (textSize * 0.03));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(matchName, 0, getMeasuredHeight() / 2, textColor);
    }
}
