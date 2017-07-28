package com.liftcore.android.displaytest;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.liftcore.android.displaytest.model.Constants;
import com.liftcore.android.displaytest.util.Utils;
import com.liftcore.android.displaytest.util.XmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String mXmlChildPath = "/DisplayTest/display_test.xml";
    private XmlParser.Entry mEntry = null;

    private FrameLayout mContainView;
    VideoView videoView;
    ImageView imageView;
    TextView textView;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mContainView = (FrameLayout) view.findViewById(R.id.containView);
        new DownloadXmlTask().execute("path");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // Implementation of AsyncTask used to download XML feed from datasource.
    private class DownloadXmlTask extends AsyncTask<String, Void, XmlParser.Entry> {
        @Override
        protected XmlParser.Entry doInBackground(String... urls) {
            //Log.i(TAG, Utils.getInnerSDCardPath());
            FileInputStream fIs = null;
            try {
                fIs = new FileInputStream(Constants.SDCardPath + mXmlChildPath);
                XmlParser xmlParser = new XmlParser();
                mEntry = xmlParser.parse(fIs);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                if (fIs != null) {
                    try {
                        fIs.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return mEntry;
        }

        @Override
        protected void onPostExecute(XmlParser.Entry result) {
            Log.i(TAG, "tX=" + result.tX);
            Log.i(TAG, "tY=" + result.tY);
            Log.i(TAG, "tW=" + result.tW);
            Log.i(TAG, "tH=" + result.tH);
            Log.i(TAG, "text=" + result.text);
            generateLayout(result);
        }
    }

    private void generateLayout(XmlParser.Entry entry) {
        videoView = new VideoView(getActivity());
        imageView = new ImageView(getActivity());
        textView = new TextView(getActivity());
        //动态添加布局(java方式)
        /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout view = new LinearLayout(getActivity());
        view.setLayoutParams(lp);//设置布局参数
        view.setOrientation(LinearLayout.HORIZONTAL);*/

        FrameLayout.LayoutParams vlp = new FrameLayout.LayoutParams(entry.vW, entry.vH);
        FrameLayout.LayoutParams plp = new FrameLayout.LayoutParams(entry.pW, entry.pH);
        FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(entry.tW, entry.tH);
        vlp.setMargins(entry.vX, entry.vY, 0, 0);
        plp.setMargins(entry.pX, entry.pY, 0, 0);
        tlp.setMargins(entry.tX, entry.tY, 0, 0);

        videoView.setLayoutParams(vlp);

        imageView.setLayoutParams(plp);
        imageView.setBackgroundColor(Color.RED);
        textView.setLayoutParams(tlp);
        textView.setBackgroundColor(Color.GREEN);
        textView.setText(entry.text);

        mContainView.removeViewAt(0);
        mContainView.addView(imageView);
        mContainView.addView(videoView);
        mContainView.addView(textView);

        mContainView.setBackgroundColor(Color.BLUE);

        videoView.setVideoPath(entry.vPath);
        videoView.start();
        new GetPicTask().execute(entry.pPath);
    }

    // get pic from storage.
    private class GetPicTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            //Log.i(TAG, Utils.getInnerSDCardPath());
            Bitmap bitmap = Utils.decodeSampledBitmapFromStorage(urls[0], mEntry.pW, mEntry.pH);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (isVisible() && imageView != null) {
                imageView.setImageBitmap(result);
            }
        }
    }

}
