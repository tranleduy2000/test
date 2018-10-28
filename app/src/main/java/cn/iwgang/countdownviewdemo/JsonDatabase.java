package cn.iwgang.countdownviewdemo;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonDatabase implements IDatabase<RecyclerViewActivity.Schedule> {
    private static final String FILE_NAME = "data.json";
    private static final String ROOT_KEY = "data";

    private SharedPreferences mDatabase;
    private Context mContext;

    public JsonDatabase(Context context) {
        mContext = context;
        createDatabase();
    }

    private void createDatabase() {
        File jsonFile = getJsonFile();
        if (jsonFile.exists()){
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ROOT_KEY, new JSONArray());
            write(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private File getJsonFile(){
        return new File(mContext.getFilesDir(), FILE_NAME);
    }

    private JSONObject getDatabase() {
        File file = getJsonFile();
        try {
            String content = FileUtils.readFileToString(file);
            return new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    public List<RecyclerViewActivity.Schedule> readAll() {
        JSONObject database = getDatabase();
        ArrayList<RecyclerViewActivity.Schedule> schedules = new ArrayList<>();
        try {
            JSONArray array = database.getJSONArray(ROOT_KEY);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String title = item.getString("title");
                String desc = item.getString("desc");
                long time = item.getLong("time");
                RecyclerViewActivity.Schedule schedule = new RecyclerViewActivity.Schedule(
                        1, title, desc, time
                );
                schedules.add(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedules;
    }

    @Override
    public void add(RecyclerViewActivity.Schedule schedule) {
        JSONObject database = getDatabase();
        try {
            JSONArray jsonArray = database.getJSONArray(ROOT_KEY);

            JSONObject item = new JSONObject();
            item.put("title", schedule.getTitle());
            item.put("desc", schedule.getDescription());
            item.put("time", schedule.getEndTime());

            jsonArray.put(item);

            write(database);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(JSONObject jsonObject) throws IOException, JSONException {
        String content = jsonObject.toString(2);
        FileUtils.write(getJsonFile(), content);
    }

    @Override
    public void remove(RecyclerViewActivity.Schedule toBeRemoved) {
        List<RecyclerViewActivity.Schedule> schedules = readAll();
        schedules.remove(toBeRemoved);

        JSONObject database = getDatabase();
        try {
            JSONArray jsonArray = new JSONArray();

            for (RecyclerViewActivity.Schedule schedule : schedules) {
                JSONObject item = new JSONObject();
                item.put("title", schedule.getTitle());
                item.put("desc", schedule.getDescription());
                item.put("time", schedule.getEndTime());

                jsonArray.put(item);
            }

            database.put(ROOT_KEY, jsonArray);

            write(database);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(RecyclerViewActivity.Schedule schedule) {

    }


}
