package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.caverock.androidsvg.SVGImageView;

// 터치 이벤트 : https://stackoverflow.com/questions/14814542/moving-imageview-with-touch-event
// 이미지 실제 위치 계삭 : https://m.blog.naver.com/PostView.nhn?blogId=mauveman&logNo=90131391510&proxyReferer=https%3A%2F%2Fwww.google.com%2F
public class MainFragment extends Fragment implements View.OnTouchListener {

    private SVGImageView svgImageView;

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF scrStart = new PointF();
    PointF scrMid = new PointF();
    float oldDist = 1f;

    float scale = 1f;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.content_main, container, false);
        //inflater는 xml에 잇는 리소스를 실제 객체화 시키는 것 (new가 아니라 새로 가져오는 것이 아니라 xml데이터 상태 그대로 가져오는 것)
        //svgImageView = view.findViewById(R.id.subwaySvg);
        svgImageView = new SVGImageView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        svgImageView.setScaleType(ImageView.ScaleType.MATRIX);
        svgImageView.setLayoutParams(params);
        svgImageView.setImageAsset("Seoul_subway_linemap_ko.svg");
        svgImageView.setOnTouchListener(this);

        //return view;
        return svgImageView;
    }


    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        Log.d(TAG, sb.toString());
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private static final String TAG = "HELLO ";

    private PointF convertAbsolutePt(PointF touchPt) {
        return convertAbsolutePt(touchPt.x, touchPt.y);
    }

    private PointF convertAbsolutePt(float x, float y) {
        float[] value = new float[9];
        matrix.getValues(value);
        float absolute_x = (x / value[0]) - (value[2] / value[0]);
        float absolute_y = (y / value[4]) - (value[5] / value[4]);
        return new PointF(absolute_x, absolute_y);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        SVGImageView view = (SVGImageView) svgImageView;
        dumpEvent(event);

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                scrStart.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);

                savedMatrix.set(matrix);
                midPoint(scrMid, event);
                mode = ZOOM;
                Log.d(TAG, "mode=ZOOM");

                break;
            case MotionEvent.ACTION_UP:
                PointF mapPt = convertAbsolutePt(event.getX(), event.getY());
                String map = mapPt.x + ", " + mapPt.y;
                Log.e("맵", "맵 : " + map);
                Toast.makeText(getActivity(), map, Toast.LENGTH_SHORT).show();
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - scrStart.x, event.getY()
                            - scrStart.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    matrix.set(savedMatrix);
                    scale = newDist / oldDist;
                    matrix.postScale(scale, scale, scrMid.x, scrMid.y);
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }
}
