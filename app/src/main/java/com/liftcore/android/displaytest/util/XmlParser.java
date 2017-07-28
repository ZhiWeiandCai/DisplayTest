package com.liftcore.android.displaytest.util;

import android.util.Log;
import android.util.Xml;

import com.liftcore.android.displaytest.model.DisplayEntry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Harry on 7/26/2017.
 */

public class XmlParser {
    private static final String TAG = "XmlParser";
    // We don't use namespaces
    private static final String ns = null;

    public ArrayList<DisplayEntry> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private ArrayList<DisplayEntry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<DisplayEntry> entries = null;
        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the entry tag
            if (name.equals("entry")) {

                entries = readEntry(parser);
            } else {
                skip(parser);
            }
        }

        return entries;
    }

    // Parses the contents of an entry.
    private ArrayList<DisplayEntry> readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<DisplayEntry> entries = new ArrayList<>();
        DisplayEntry entry = null;
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        int vX = 0;
        int vY = 0;
        int vW = 0;
        int vH = 0;
        String vPath = null;
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
                    entry = new DisplayEntry();
                    entry.setSortFlag(3);
                    entry.setxLoc(vX);
                    entry.setyLoc(vY);
                    entry.setWidth(vW);
                    entry.setHeight(vH);
                    entry.setPath(vPath);
                    entries.add(entry);
                    parser.nextTag();
                }
                parser.require(XmlPullParser.END_TAG, ns, "vedio");
            } else if (name.equals("pic")) {
                parser.require(XmlPullParser.START_TAG, ns, "pic");
                String tag = parser.getName();
                if (tag.equals("pic")) {
                    vX = Integer.parseInt(parser.getAttributeValue(null, "x"));
                    vY = Integer.parseInt(parser.getAttributeValue(null, "y"));
                    vW = Integer.parseInt(parser.getAttributeValue(null, "width"));
                    vH = Integer.parseInt(parser.getAttributeValue(null, "height"));
                    vPath = parser.getAttributeValue(null, "path");
                    Log.i(TAG, vPath);
                    entry = new DisplayEntry();
                    entry.setSortFlag(2);
                    entry.setxLoc(vX);
                    entry.setyLoc(vY);
                    entry.setWidth(vW);
                    entry.setHeight(vH);
                    entry.setPath(vPath);
                    entries.add(entry);
                    parser.nextTag();
                }
                parser.require(XmlPullParser.END_TAG, ns, "pic");
            } else if (name.equals("Text")) {
                parser.require(XmlPullParser.START_TAG, ns, "Text");
                String tag = parser.getName();
                if (tag.equals("Text")) {
                    vX = Integer.parseInt(parser.getAttributeValue(null, "x"));
                    vY = Integer.parseInt(parser.getAttributeValue(null, "y"));
                    vW = Integer.parseInt(parser.getAttributeValue(null, "width"));
                    vH = Integer.parseInt(parser.getAttributeValue(null, "height"));
                    vPath = parser.getAttributeValue(null, "text");
                    Log.i(TAG, vPath);
                    entry = new DisplayEntry();
                    entry.setSortFlag(1);
                    entry.setxLoc(vX);
                    entry.setyLoc(vY);
                    entry.setWidth(vW);
                    entry.setHeight(vH);
                    entry.setPath(vPath);
                    entries.add(entry);
                    parser.nextTag();
                }
                parser.require(XmlPullParser.END_TAG, ns, "Text");

            } else {
                skip(parser);
            }
        }
        return entries;
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
