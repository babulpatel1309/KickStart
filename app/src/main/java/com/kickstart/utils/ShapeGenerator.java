package com.kickstart.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

/**
 * Created by Harsh Patel on 15/5/17.
 */

public class ShapeGenerator {

    public ShapeGenerator() {
    }


    public Drawable circleShape(int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(backgroundColor);
        return shape;
    }


    public Drawable roundedRectWithBorder(View v, int backgroundColor, int borderColor, int borderRadius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius((float) borderRadius);
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        return shape;
    }

    public Drawable rectWithBorder(int backgroundColor, int borderColor, int borderWidth) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(backgroundColor);
        shape.setStroke(borderWidth, borderColor);
        return shape;
    }

    public Drawable roundedRect(View v, int backgroundColor, int borderRadius, int paddingLR, int paddingTB) {

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius((float) borderRadius);
        shape.setColor(backgroundColor);
        v.setPadding(paddingLR, paddingTB, paddingLR, (paddingTB - 3));
        return shape;
    }

    public Drawable roundedRect(Context context, View v, int backgroundColor, int borderRadius, int paddingLR, int paddingTB) {

        backgroundColor = context.getResources().getColor(backgroundColor);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius((float) borderRadius);
        shape.setColor(backgroundColor);
        v.setPadding(paddingLR, paddingTB, paddingLR, (paddingTB - 3));
        return shape;
    }

    public Drawable roundedRectWithBorder(Context context, View v, int backgroundColor, int borderColor, int borderRadius) {
//        backgroundColor = context.getResources().getColor(backgroundColor);
//        borderColor = context.getResources().getColor(borderColor);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius((float) borderRadius);
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        return shape;
    }


}
