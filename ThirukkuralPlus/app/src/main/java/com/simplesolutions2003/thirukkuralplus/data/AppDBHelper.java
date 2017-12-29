package com.simplesolutions2003.thirukkuralplus.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.audiofx.BassBoost;
import android.util.Log;

import com.simplesolutions2003.thirukkuralplus.data.AppContract.SectionsEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.GroupsEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.ChaptersEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.KuralsEntry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by SuriyaKumar on 8/16/2016.
 */
public class AppDBHelper extends SQLiteOpenHelper  {
    private final String LOG_TAG = AppDBHelper.class.getSimpleName();
    private Context context;

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "thirukkuralplus.db";

    public AppDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.v(LOG_TAG,"AppDBHelper");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG,"onCreate");

        final String SQL_CREATE_SECTIONS_TABLE = "CREATE TABLE " + AppContract.SectionsEntry.TABLE_NAME + " (" +
                SectionsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SectionsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                SectionsEntry.COLUMN_NAME_ENG + " TEXT NOT NULL, " +
                SectionsEntry.COLUMN_ICON + " TEXT, " +
                " UNIQUE (" + SectionsEntry._ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_GROUPS_TABLE = "CREATE TABLE " + AppContract.GroupsEntry.TABLE_NAME + " (" +
                GroupsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GroupsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                GroupsEntry.COLUMN_NAME_ENG + " TEXT NOT NULL, " +
                GroupsEntry.COLUMN_ICON + " TEXT, " +
                " UNIQUE (" + GroupsEntry._ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_CHAPTERS_TABLE = "CREATE TABLE " + AppContract.ChaptersEntry.TABLE_NAME + " (" +
                ChaptersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ChaptersEntry.COLUMN_SECTION_ID + " INTEGER, " +
                ChaptersEntry.COLUMN_GROUP_ID + " INTEGER, " +
                ChaptersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ChaptersEntry.COLUMN_NAME_ENG + " TEXT NOT NULL, " +
                ChaptersEntry.COLUMN_ICON + " TEXT, " +
                " UNIQUE (" + ChaptersEntry._ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_KURALS_TABLE = "CREATE TABLE " + KuralsEntry.TABLE_NAME + " (" +
                KuralsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KuralsEntry.COLUMN_CHAPTER_ID + " INTEGER, " +
                KuralsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                KuralsEntry.COLUMN_NAME_ENG + " TEXT NOT NULL, " +
                KuralsEntry.COLUMN_EXP_MUVA + " TEXT, " +
                KuralsEntry.COLUMN_EXP_SOLO + " TEXT, " +
                KuralsEntry.COLUMN_EXP_MUKA + " TEXT, " +
                KuralsEntry.COLUMN_COUPLET + " TEXT, " +
                KuralsEntry.COLUMN_TRANS + " TEXT, " +
                KuralsEntry.COLUMN_FAVORITE + " INTEGER, " +
                KuralsEntry.COLUMN_READ + " INTEGER, " +

                " UNIQUE (" + KuralsEntry._ID + ") ON CONFLICT REPLACE);";

        Log.v(LOG_TAG, "SQL_CREATE_MENU_TABLE" + SQL_CREATE_SECTIONS_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_ARTICLE_TABLE" + SQL_CREATE_GROUPS_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_ARTICLE_DETAIL_TABLE" + SQL_CREATE_CHAPTERS_TABLE);
        Log.v(LOG_TAG, "SQL_CREATE_FAVORITE_TABLE" + SQL_CREATE_KURALS_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_SECTIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GROUPS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CHAPTERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_KURALS_TABLE);

// TO DO
        oneTimeInsertSections(sqLiteDatabase);
        oneTimeInsertGroups(sqLiteDatabase);
        oneTimeInsertChapters(sqLiteDatabase);
        oneTimeInsertKurals(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.v(LOG_TAG,"onUpgrade");
        onCreate(sqLiteDatabase);
    }

    private void oneTimeInsertSections(SQLiteDatabase sqLiteDatabase){
        Log.v(LOG_TAG,"oneTimeInsertSections");

        try {
            InputStream is = context.getAssets().open("data_Sections.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("record");

            Log.v(LOG_TAG,"data_Sections Count - " + nList.getLength());

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;

                    ContentValues cv = new ContentValues();
                    cv.put(SectionsEntry._ID, getValue("_id", element2));
                    cv.put(SectionsEntry.COLUMN_NAME, getValue("name", element2));
                    cv.put(SectionsEntry.COLUMN_NAME_ENG, getValue("name_eng", element2));
                    cv.put(SectionsEntry.COLUMN_ICON, "");
                    sqLiteDatabase.insert(SectionsEntry.TABLE_NAME, null, cv);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void oneTimeInsertGroups(SQLiteDatabase sqLiteDatabase){
        Log.v(LOG_TAG,"oneTimeInsertGroups");

        try {
            InputStream is = context.getAssets().open("data_Groups.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("record");

            Log.v(LOG_TAG,"data_Groups Count - " + nList.getLength());

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;

                    ContentValues cv = new ContentValues();
                    cv.put(GroupsEntry._ID, getValue("_id", element2));
                    cv.put(GroupsEntry.COLUMN_NAME, getValue("name", element2));
                    cv.put(GroupsEntry.COLUMN_NAME_ENG, getValue("name_eng", element2));
                    cv.put(GroupsEntry.COLUMN_ICON, "");
                    sqLiteDatabase.insert(GroupsEntry.TABLE_NAME, null, cv);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void oneTimeInsertChapters(SQLiteDatabase sqLiteDatabase){
        Log.v(LOG_TAG,"oneTimeInsertChapters");

        try {
            InputStream is = context.getAssets().open("data_Chapters.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("record");

            Log.v(LOG_TAG,"data_Chapters Count - " + nList.getLength());

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;

                    ContentValues cv = new ContentValues();
                    cv.put(ChaptersEntry._ID, getValue("_id", element2));
                    cv.put(ChaptersEntry.COLUMN_SECTION_ID, getValue("section_id", element2));
                    cv.put(ChaptersEntry.COLUMN_GROUP_ID, getValue("group_id", element2));
                    cv.put(ChaptersEntry.COLUMN_NAME, getValue("name", element2));
                    cv.put(ChaptersEntry.COLUMN_NAME_ENG, getValue("name_eng", element2));
                    cv.put(ChaptersEntry.COLUMN_ICON, "");
                    sqLiteDatabase.insert(ChaptersEntry.TABLE_NAME, null, cv);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void oneTimeInsertKurals(SQLiteDatabase sqLiteDatabase){
        Log.v(LOG_TAG,"oneTimeInsertKurals");

        try {
            InputStream is = context.getAssets().open("data_Kurals.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("record");

            Log.v(LOG_TAG,"data_Kurals Count - " + nList.getLength());

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;

                    ContentValues cv = new ContentValues();
                    cv.put(KuralsEntry._ID, getValue("_id", element2));
                    cv.put(KuralsEntry.COLUMN_CHAPTER_ID, getValue("chapter_id", element2));
                    cv.put(KuralsEntry.COLUMN_NAME, getValue("kural_name", element2));
                    cv.put(KuralsEntry.COLUMN_NAME_ENG, getValue("kural_english", element2));
                    cv.put(KuralsEntry.COLUMN_EXP_MUVA, getValue("kural_exp_muva", element2));
                    cv.put(KuralsEntry.COLUMN_EXP_SOLO, getValue("kural_exp_solo", element2));
                    cv.put(KuralsEntry.COLUMN_EXP_MUKA, getValue("kural_exp_muka", element2));
                    cv.put(KuralsEntry.COLUMN_COUPLET, getValue("kural_couplet", element2));
                    cv.put(KuralsEntry.COLUMN_TRANS, getValue("kural_transliteration", element2));
                    cv.put(KuralsEntry.COLUMN_READ, "0");
                    cv.put(KuralsEntry.COLUMN_FAVORITE, "0");
                    sqLiteDatabase.insert(KuralsEntry.TABLE_NAME, null, cv);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private static String getValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }catch (NullPointerException e){
            e.printStackTrace();
            return "";
        }
    }

}
