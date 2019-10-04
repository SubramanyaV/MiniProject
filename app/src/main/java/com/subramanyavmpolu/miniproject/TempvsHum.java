package com.subramanyavmpolu.miniproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TempvsHum extends AppCompatActivity {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
    SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm:ss");
    ArrayList<Model> modelArrayList = new ArrayList<>();
    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>();
    PointsGraphSeries<DataPoint> seriesp = new PointsGraphSeries<>();
    PointsGraphSeries<DataPoint> seriesp2 = new PointsGraphSeries<>();
    GraphView graph;
    Viewport viewport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempvs_hum);

        graph = findViewById(R.id.graph_view);
        graph.addSeries(series);
        graph.addSeries(series2);
        graph.addSeries(seriesp);
        graph.addSeries(seriesp2);
        seriesp.setSize(9);
        seriesp2.setColor(Color.RED);
        seriesp2.setSize(9);
        series2.setColor(Color.RED);
        viewport = graph.getViewport();
        viewport.setScalable(true);
        viewport.setScrollable(true);
        viewport.setScalableY(true);
        viewport.setScrollableY(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(60);
        graph.getGridLabelRenderer().setPadding(60);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if(isValueX) {
                    return sdf.format(new Date((long) value));
                } else {
                    return super.formatLabel(value, false);
                }
            }
        });

        requestJSON();
    }

    private void requestJSON() {
        String URLstring = "https://api.thingspeak.com/channels/865160/feeds.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLstring,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("JSON", ">>" + response);
                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            JSONArray dataArray = obj.getJSONArray("feeds");
                            for (int i = 0; i < dataArray.length(); i++) {

                                Model model = new Model();
                                JSONObject dataobj = dataArray.getJSONObject(i);

                                model.setField1(dataobj.getString("field1"));
                                model.setField2(dataobj.getString("field2"));
                                model.setDate(dataobj.getString("created_at"));

                                modelArrayList.add(model);
                            }
                            for (int j = 0; j < modelArrayList.size(); j++) {

                                Date date  = format.parse(modelArrayList.get(j).getDate());
                                assert date != null;
                                double z = Double.parseDouble(modelArrayList.get(j).getField2()) ;
                                double y = Double.parseDouble(modelArrayList.get(j).getField1());
                                series.appendData(new DataPoint(date.getTime(),z),true,20);
                                seriesp.appendData(new DataPoint(date.getTime(),z),true,20);
                                series2.appendData(new DataPoint(date.getTime(),y),true,20);
                                seriesp2.appendData(new DataPoint(date.getTime(),y),true,20);
                                viewport.setMaxX(series2.getHighestValueX());
                                viewport.setMinX(series2.getLowestValueX());
                                viewport.setMaxY(100);
                                viewport.setMinY(-100);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Log.d("ERROR", Objects.requireNonNull(error.getMessage()));
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
