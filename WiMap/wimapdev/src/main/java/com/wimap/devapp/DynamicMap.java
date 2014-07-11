package com.wimap.devapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wimap.common.Router;
import com.wimap.common.math.Intersect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Austen on 7/8/2014.
 */
public class DynamicMap extends View {

    protected final double BASIC_DOT_SIZE = 0.25;
    protected final double BASIC_WALL_SIZE = 0.3; //Meters
    protected final double EDGE_WIDTH = 0.2; //Meters

    List<Router> routers;
    Intersect user;
    double max_pad_x;
    double max_pad_y;
    double scale; //PX per meter
    double max_x;
    double max_y;
    double max_z;
    double min_x;
    double min_y;
    double min_z;
    Paint router_distance_paint;
    Paint router_point_paint;
    Paint user_paint;
    Paint wall_paint;
    Paint text_paint;
    double projection_factor;

    protected int Compare(double a, double b)
    {
        if(a < b)
            return -1;
        else
            if(a > b)
                return 1;
            else
                return 0;
    }
    public DynamicMap(Context context)
    {
        super(context);
        Init();
    }
    public DynamicMap(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        Init();
    }
    public DynamicMap(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        Init();
    }

    private void Init() {
        routers = new ArrayList<Router>();
        user = new Intersect();
        scale = 5.0; //5 px per meter
        min_x = min_y = min_z = 0.0;
        max_x = max_y = max_z = 1.0;
        max_pad_x = max_pad_y = 1.0;
        router_distance_paint = new Paint();
        router_point_paint = new Paint();
        user_paint = new Paint();
        wall_paint = new Paint();
        text_paint = new Paint();
        router_point_paint.setARGB(255, 255, 0, 0);
        router_point_paint.setStyle(Paint.Style.FILL);
        router_distance_paint.setARGB(128, 255, 0, 0);
        router_distance_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        user_paint.setARGB(255, 0, 0, 255);
        user_paint.setStyle(Paint.Style.FILL);
        wall_paint.setARGB(255, 0, 0, 0);
        wall_paint.setStyle(Paint.Style.STROKE);
        text_paint.setARGB(255,0,0,0);
        text_paint.setTextSize(10);
        projection_factor = 1.0;
    }

    public void UpdateRouters(List<Router> routers)
    {
        if (routers.size() < 2) return;
        this.routers = routers;

        //Build a model of the space
        max_x = Collections.max(routers, new Comparator<Router>() {
            @Override
            public int compare(Router router, Router router2) {
                return Compare(router.x, router2.x);
            }
        }).x;
        max_y = Collections.max(routers, new Comparator<Router>() {
            @Override
            public int compare(Router router, Router router2) {
                return Compare(router.y, router2.y);
            }
        }).y;
        max_z = Collections.max(routers, new Comparator<Router>() {
            @Override
            public int compare(Router router, Router router2) {
                return Compare(router.z, router2.z);
            }
        }).z;
        min_x = Collections.min(routers, new Comparator<Router>() {
            @Override
            public int compare(Router router, Router router2) {
                return Compare(router.x, router2.x);
            }
        }).x;
        min_y = Collections.min(routers, new Comparator<Router>() {
            @Override
            public int compare(Router router, Router router2) {
                return Compare(router.y, router2.y);
            }
        }).y;
        min_z = Collections.min(routers, new Comparator<Router>() {
            @Override
            public int compare(Router router, Router router2) {
                return Compare(router.z, router2.z);
            }
        }).z;
        max_pad_x = BASIC_DOT_SIZE*scale;
        max_pad_y = BASIC_DOT_SIZE*scale;
        this.invalidate();
    }

    public void UpdatePosition(Intersect position)
    {
        this.user = position;
        max_x = Math.max(max_x, user.x+user.x_conf);
        max_y = Math.max(max_y, user.y+user.y_conf);
        min_y = Math.min(min_y, user.y-user.y_conf);
        min_x = Math.min(min_x, user.x-user.x_conf);
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawARGB(255,255,255,255);
        int w = this.getWidth();
        int h = this.getHeight();
        //Normalize so that min = zero + pad
        //Normalize so that max = height - pad
        double x_translation_factor = Math.ceil(-min_x);
        double y_translation_factor = Math.ceil(-min_y);

        double virtual_width =  (Math.ceil(max_x + x_translation_factor) + (max_pad_x*2));
        double virtual_height =  (Math.ceil(max_y + y_translation_factor) + (max_pad_y*2));

        double x_proj_factor = (virtual_width)/(double)w;
        double y_proj_factor = (virtual_height)/(double)h;

        projection_factor = Math.min(x_proj_factor,y_proj_factor) * scale;


        wall_paint.setStrokeWidth((float)(BASIC_WALL_SIZE*projection_factor));
        canvas.drawRect((float)(max_pad_x*projection_factor),(float) (max_pad_y*projection_factor), (float)(w-(max_pad_x*projection_factor)),(float) (h-(max_pad_y*projection_factor)), wall_paint);

        router_distance_paint.setStrokeWidth((float)(EDGE_WIDTH*projection_factor));
        for(Router r : routers)
        {
            projectCircle(canvas, r.x+x_translation_factor, r.y+y_translation_factor, r.GetFSPLDistance_d(r.power), router_distance_paint);
            projectCircle(canvas, r.x+x_translation_factor, r.y+y_translation_factor, (float) (BASIC_DOT_SIZE*projection_factor), router_point_paint);
            projectText(canvas, r.ssid + Double.toString(r.power), r.x + BASIC_DOT_SIZE*projection_factor, r.y - BASIC_DOT_SIZE * projection_factor, text_paint);
        }
        projectCircle(canvas, user.x, user.y, Math.min(user.x_conf, user.y_conf), user_paint);

    }

    protected void projectCircle(Canvas canvas, double cx, double cy, double radius, Paint paint)
    {
        float center_x = (float) (projection_factor * (max_pad_x + cx));
        float center_y = (float) (projection_factor * (max_pad_y + cy));
        float proj_radius = (float) (projection_factor* radius);
        Log.i("DynamicMap", "Router at " + Double.toString(cx) + ", " + Double.toString(cy));
        canvas.drawCircle(center_x, center_y, proj_radius, paint);
    }

    protected void projectText(Canvas canvas, String text, double x, double y, Paint paint)
    {
        float p_x = (float)(projection_factor * (max_pad_x + x));
        float p_y = (float)(projection_factor * (max_pad_y + y));
        canvas.drawText(text, p_x,p_y,paint);
    }
}
