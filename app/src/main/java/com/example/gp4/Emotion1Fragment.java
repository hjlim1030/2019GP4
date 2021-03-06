package com.example.gp4;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

// 스트레스 지수 파악 및 hrv 그래프 그리는 부분
public class Emotion1Fragment extends Fragment {

    private int heartrate = 0; // 전달받은 심박수
    private int turnNum = 0; // 전달받은 프로그레스바가 진행된 횟수
    private List<Entry> redpixels = new ArrayList<>(); // 전달받은 red pixels 저장
    private ArrayList xPeaks = new ArrayList(); // peak일 때의 x 값을 저장하기 위함
    private List<Entry> hrvValue = new ArrayList<>(); // 심박 주기 그래프화 하기 위한 리스트
    private int hrvCount = 0;

    private TextView heartbpm; // 심박수 표시 텍스트 뷰
    private LineChart pixelGraph; // red pixels 그래프
    private LineChart hrvGraph;
    private ImageView stressImage; // 스트레스 지수에 따른 이미지
    private TextView stressTextview; // 스트레스 지수 표시하는 텍스트 뷰

    private XAxis pixelX;
    private YAxis pixelY;
    private XAxis hrvX;
    private YAxis hrvY;
    //private static ArrayList temps = new ArrayList();
    private Bundle toBundle = new Bundle(); // 전달해주기 위한 bundle
    private boolean IsStress = false; // 스트레스 유무
    private Fragment fragment = new Emotion2Fragment();

    public static Emotion1Fragment newInstance(){
        return new Emotion1Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_emotion1, container, false);

        heartbpm = (TextView)viewGroup.findViewById(R.id.fragment_emotion1_textview_heartrate);
        pixelGraph = (LineChart)viewGroup.findViewById(R.id.fragment_emotion1_redchart);
        hrvGraph = (LineChart)viewGroup.findViewById(R.id.fragment_emotion1_hrvchart);
        stressImage = (ImageView)viewGroup.findViewById(R.id.fragment_emotion1_imageview_stress);
        stressTextview = (TextView)viewGroup.findViewById(R.id.fragment_emotion1_textview_howstress);

        setchart(); // 차트 기본 설정

        init(); // 심박 표시 및 그래프 그리기

        return viewGroup;
    }

    // 차트 기본 설정하는 함수
    private void setchart() {
            pixelGraph.setBackgroundColor(Color.TRANSPARENT);
            pixelGraph.getDescription().setEnabled(false);
            pixelGraph.getLegend().setEnabled(false);
            pixelGraph.setTouchEnabled(false);
            pixelGraph.setDrawGridBackground(false);
            pixelGraph.setDragEnabled(false);
            pixelGraph.setScaleEnabled(false);
            pixelGraph.setPinchZoom(false);

            {
                pixelX = pixelGraph.getXAxis();
                pixelX.setDrawGridLines(false);
                pixelX.setDrawAxisLine(false); // x축 숨기기
                pixelX.setDrawLabels(false); // x축 값 숨기기
                pixelX.setPosition(XAxis.XAxisPosition.BOTTOM);
                pixelX.setTextColor(Color.BLACK);
            }

            {
                pixelY = pixelGraph.getAxisLeft();
                pixelGraph.getAxisRight().setEnabled(false);
                pixelY.setDrawGridLines(false);
                pixelY.setDrawAxisLine(false); // y축 숨기기
                pixelY.setDrawLabels(false); // y축 값 숨기기
                pixelY.setTextColor(Color.BLACK);

            }

        hrvGraph.setBackgroundColor(Color.TRANSPARENT);
        hrvGraph.getDescription().setEnabled(false);
        hrvGraph.getLegend().setEnabled(false);
        hrvGraph.setTouchEnabled(false);
        hrvGraph.setDrawGridBackground(false);
        hrvGraph.setDragEnabled(false);
        hrvGraph.setScaleEnabled(false);
        hrvGraph.setPinchZoom(false);

        {
            hrvX = hrvGraph.getXAxis();
            hrvX.setDrawGridLines(false);
            hrvX.setDrawAxisLine(false); // x축 숨기기
            hrvX.setDrawLabels(false); // x축 값 숨기기
            hrvX.setPosition(XAxis.XAxisPosition.BOTTOM);
            hrvX.setTextColor(Color.BLACK);
        }

        {
            hrvY = hrvGraph.getAxisLeft();
            hrvGraph.getAxisRight().setEnabled(false);
            hrvY.setDrawGridLines(false);
            hrvY.setDrawAxisLine(false); // y축 숨기기
            hrvY.setDrawLabels(false); // y축 값 숨기기
            hrvY.setTextColor(Color.BLACK);

        }
    }

    // 심박 표시 및 그래프 그리는 함수
    private void init() {

        Bundle bundle = getArguments();
        heartrate = bundle.getInt("heartrate");
        heartbpm.setText(String.valueOf(heartrate) + " bpm"); // 심박 표시

        turnNum = bundle.getInt("ToNum");

        /* red values 그래프 그리기 */
        redpixels = bundle.getParcelableArrayList("list");

        LineDataSet lineDataSet;
        lineDataSet = new LineDataSet(redpixels, "value");
        lineDataSet.setLineWidth(1);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(Color.parseColor("#FF5c99cc"));
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        pixelGraph.setData(lineData);
        pixelGraph.invalidate();
        // 여기까지 redvalues 그래프 그리기

        /*  심박 주기 그래프 그리기 */
        xPeaks = bundle.getParcelableArrayList("xpeaks");
        for(int i=1; i<xPeaks.size(); i++){
            float temp = (float) xPeaks.get(i) - (float) xPeaks.get(i-1);
            hrvValue.add(new Entry(hrvCount++, temp));
            //temps.add(temp); // 주기 차이를 넣음
        }

        //Log.v("꼭", "차이 주기 배열 길이 : "+ temps.size());
        //Log.v("꼭", "차이 주기 배열 : "+ temps);

        LineDataSet lineDataSet1;
        lineDataSet1 = new LineDataSet(hrvValue, "value");
        lineDataSet1.setLineWidth(1);
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.parseColor("#FF5c99cc"));
        lineDataSet1.setDrawValues(false);

        LineData lineData1 = new LineData(lineDataSet1);
        hrvGraph.setData(lineData1);
        hrvGraph.invalidate();
        // 여기까지 심박 주기 그래프 그리기

        //여기부터 수정
        int N = 1024; // int N = 8;
        FFT fft = new FFT(N);

        double[] window = fft.getWindow();
        double[] re = new double[N];
        double[] im = new double[N];

        // 내가 추가
        for(int i=0; i<N; i++){
            if(i >= hrvValue.size()){
                re[i] = 0;
                im[i] = 0;
            }else{
                //re[i] = (float)temps.get(i);
                re[i] = hrvValue.get(i).getY();
                im[i] = 0;
            }
        }

        fft.fft(re, im);
        // beforeAfter(fft, re, im); - 출력 필요할 때

        ArrayList whathz = new ArrayList();
        ArrayList hzMag = new ArrayList();
        int samplingFrequency = hrvValue.size() / ( 15 * turnNum )  ;

        double a=0; double b = 0;
        for(int i=0; i<= N/2; i++){
            a= Math.sqrt( (re[i]*re[i]) + (im[i]*im[i]) ); // 주파수 magnitude
            b= i * (double) samplingFrequency / (double) N; // 주파수

            hzMag.add(i, a);
            whathz.add(i, b );
        }

        //Log.v("꼭", "HZ : " +whathz);
        //Log.v("꼭", "HZ 크기 : " + hzMag);

        double lf = 0; double hf = 0;
        for(int i=1; i<=N/2; i++){
             if( 0.04 <= (double)whathz.get(i) && (double)whathz.get(i) < 0.15 )
                 lf += (double)hzMag.get(i);
             else if(0.15 <= (double)whathz.get(i) && (double)whathz.get(i) < 0.4)
                 hf += (double)hzMag.get(i);
        }

        final double stress = lf/hf;
        if( stress >= 1){
            stressTextview.setText(String.format("%.2f",stress) + " / 스트레스 있음");
            IsStress = true;
            stressImage.setImageResource(R.drawable.stress);
        }else{
            stressTextview.setText(String.format("%.2f",stress)  + " / 스트레스 없음");
            IsStress = false;
            stressImage.setImageResource(R.drawable.nostress);
        }
        //Log.v("결과 확인", "lf/hf : " + (lf/hf));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toBundle.putBoolean("stress", IsStress);
                fragment.setArguments(toBundle);

                ((MainActivity)getActivity()).replaceFragment(fragment); // 스트레스 전달
            }
        },3000); // 3초 후 실행

    }

    protected static void beforeAfter(FFT fft, double[] re, double[] im){
        System.out.println("Before: ");
        printReIm(re, im);
        fft.fft(re, im);
        System.out.println("After: ");
        printReIm(re, im);


    }

    protected static void printReIm(double[] re, double[] im ){
        System.out.print("Re: [");
        for(int i=0; i<re.length; i++)
            System.out.print(((int)(re[i]*1000)/1000.0) + " ");

        System.out.print("]\nIm: [");
        for(int i=0; i<im.length; i++)
            System.out.print(((int)(im[i]*1000)/1000.0) + " ");

        System.out.println("]");
    }

}

