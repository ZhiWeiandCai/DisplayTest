package com.liftcore.android.displaytest.util;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Harry on 7/26/2017.
 */

public class XmlParser {
    private static final String TAG = "XmlParser";
    // We don't use namespaces
    private static final String ns = null;

    public Entry parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private Entry readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Entry entry = null;
        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the entry tag
            if (name.equals("entry")) {

                entry = readEntry(parser);
            } else {
                skip(parser);
            }
        }

        return entry;
    }

    public static class Entry {
        public int vX;
        public int vY;
        public int vW;
        public int vH;
        public String vPath;
        public int pX;
        public int pY;
        public int pW;
        public int pH;
        public String pPath;
        public int tX;
        public int tY;
        public int tW;
        public int tH;
        public String text;

        private Entry(int vX, int vY, int vW, int vH, String vPath,
                      int pX, int pY, int pW, int pH, String pPath,
                      int tX, int tY, int tW, int tH, String text) {
            this.vX = vX;
            this.vY = vY;
            this.vW = vW;
            this.vH = vH;
            this.vPath = vPath;
            this.pX = pX;
            this.pY = pY;
            this.pW = pW;
            this.pH = pH;
            this.pPath = pPath;
            this.tX = tX;
            this.tY = tY;
            this.tW = tW;
            this.tH = tH;
            this.text = text;
        }
    }

    // Parses the contents of an entry.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        int vX = 0;
        int vY = 0;
        int vW = 0;
        int vH = 0;
        String vPath = null;
        int pX = 0;
        int pY = 0;
        int pW = 0;
        int pH = 0;
        String pPath = null;
        int tX = 0;
        int tY = 0;
        int tW = 0;
        int tH = 0;
        String text = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("vedio")) {

                parser.require(XmlPullParser.START_TAG, ns, "vedio");
                String tag = parser.getName();
                if (tag.equals("vedio")) {
                    vX = Integer.parseInt(parser.getAttributeValue(null, "x"));
                    vY = Integer.parseInt(parser.getAttributeValue(null, "y"));
                    vW = Integer.parseInt(parser.getAttributeValue(null, "width"));
                    vH = Integer.parseInt(parser.getAttributeValue(null, "height"));
                    vPath = parser.getAttributeValue(null, "path");
                    Log.i(TAG, vPath);
                    parser.nextTag();
                }
                parser.require(XmlPullParser.END_TAG, ns, "vedio");
            } else if (name.equals("pic")) {
                parser.require(XmlPullParser.START_TAG, ns, "pic");
                String tag = parser.getName();
                if (tag.equals("pic")) {
                    pX = Integer.parseInt(parser.getAttributeValue(null, "x"));
                    pY = Integer.parseInt(parser.getAttributeValue(null, "y"));
                    pW = Integer.parseInt(parser.getAttributeValue(null, "width"));
                    pH = Integer.parseInt(parser.getAttributeValue(null, "height"));
                    pPath = parser.getAttributeValue(null, "path");
                    Log.i(TAG, pPath);
                    parser.nextTag();
                }
                parser.require(XmlPullParser.END_TAG, ns, "pic");
            } else if (name.equals("Text")) {
                parser.require(XmlPullParser.START_TAG, ns, "Text");
                String tag = parser.getName();
                if (tag.equals("Text")) {
                    tX = Integer.parseInt(parser.getAttributeValue(null, "x"));
                    tY = Integer.parseInt(parser.getAttributeValue(null, "y"));
                    tW = Integer.parseInt(parser.getAttributeValue(null, "width"));
                    tH = Integer.parseInt(parser.getAttributeValue(null, "height"));
                    text = parser.getAttributeValue(null, "text");
                    Log.i(TAG, text);
                    parser.nextTag();
                }
                parser.require(XmlPullParser.END_TAG, ns, "Text");

            } else {
                skip(parser);
            }
        }
        return new Entry(vX, vY, vW, vH, vPath, pX, pY, pW, pH, pPath, tX, tY, tW, tH, text);
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
