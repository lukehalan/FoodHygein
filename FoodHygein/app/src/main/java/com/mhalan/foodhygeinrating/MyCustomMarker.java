/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.content.Context;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public interface MyCustomMarker {
    void onEvent(Establishment establishment);
}
