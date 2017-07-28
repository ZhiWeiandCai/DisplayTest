package com.liftcore.android.displaytest;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.liftcore.android.displaytest.model.Constants;
import com.liftcore.android.displaytest.model.DisplayEntry;
import com.liftcore.android.displaytest.util.Utils;
import com.liftcore.android.displaytest.util.XmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


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
    private ArrayList<DisplayEntry> mEntries = null;

    private FrameLayout mContainView;

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
    private class DownloadXmlTask extends AsyncTask<String, Void, ArrayList<DisplayEntry>> {
        @Override
        protected ArrayList<DisplayEntry> doInBackground(String... urls) {
            //Log.i(TAG, Utils.getInnerSDCardPath());
            FileInputStream fIs = null;
            try {
                fIs = new FileInputStream(Constants.SDCardPath + mXmlChildPath);
                XmlParser xmlParser = new XmlParser();
                mEntries = xmlParser.parse(fIs);
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
            //视频排在前面，图片次之，文本最后
            Collections.sort(mEntries, new Comparator<DisplayEntry>() {

                @Override
                public int compare(DisplayEntry o1, DisplayEntry o2) {
                    return o2.getSortFlag() - o1.getSortFlag();
                }
            });
            return mEntries;
        }

        @Override
        protected void onPostExecute(ArrayList<DisplayEntry> result) {

            generateLayout(result);
        }
    }

    private void generateLayout(ArrayList<DisplayEntry> entries) {
        FrameLayout.LayoutParams lp = null;
        TextView tView = null;
        ImageView iView = null;
        VideoView vView = null;
        mContainView.removeViewAt(0);
        for (DisplayEntry entry : entries) {
            switch (entry.getSortFlag()) {
                case 1:
                    tView = new TextView(getActivity());
                    lp = new FrameLayout.LayoutParams(entry.getWidth(), entry.getHeight());
                    lp.setMargins(entry.getxLoc(), entry.getyLoc(), 0, 0);
                    tView.setLayoutParams(lp);
                    tView.setBackgroundColor(Color.GREEN);
                    tView.setText(entry.getPath());
                    mContainView.addView(tView);
                    break;
                case 2:
                    iView = new ImageView(getActivity());
                    lp = new FrameLayout.LayoutParams(entry.getWidth(), entry.getHeight());
                    lp.setMargins(entry.getxLoc(), entry.getyLoc(), 0, 0);
                    iView.setLayoutParams(lp);
                    mContainView.addView(iView);
                    break;
                case 3:
                    vView = new VideoView(getActivity());
                    lp = new FrameLayout.LayoutParams(entry.getWidth(), entry.getHeight());
                    lp.setMargins(entry.getxLoc(), entry.getyLoc(), 0, 0);
                    vView.setLayoutParams(lp);
                    mContainView.addView(vView);
                    break;
            }
        }

        //动态添加布局(java方式)
        /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout view = new LinearLayout(getActivity());
        view.setLayoutParams(lp);//设置布局参数
        view.setOrientation(LinearLayout.HORIZONTAL);*/

        //mContainView.setBackgroundColor(Color.BLUE);

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getSortFlag() == 3) {
                vView = (VideoView) mContainView.getChildAt(i);
                vView.setVideoPath(entries.get(i).getPath());
                vView.start();
            } else if (entries.get(i).getSortFlag() == 2) {
                new GetPicTask((ImageView) mContainView.getChildAt(i)).execute(entries.get(i));
            }
        }

    }

    // get pic from storage.
    private class GetPicTask extends AsyncTask<DisplayEntry, Void, Bitmap> {
        private ImageView iv;

        public GetPicTask(ImageView imageView) {
            this.iv = imageView;
        }

        @Override
        protected Bitmap doInBackground(DisplayEntry... urls) {
            //Log.i(TAG, Utils.getInnerSDCardPath());
            Bitmap bitmap = Utils.decodeSampledBitmapFromStorage(urls[0].getPath(), urls[0].getWidth(), urls[0].getHeight());

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (isVisible() && iv != null) {
                iv.setImageBitmap(result);
            }
        }
    }

}
