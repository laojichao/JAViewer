package io.github.javiewer.adapter.item;

import android.util.Log;

import java.io.Serializable;

/**
 * Project: JAViewer
 */
public class MagnetLink implements Serializable {

    protected String magnetLink;

    public static MagnetLink create(String magnetLink) {
        MagnetLink magnet = new MagnetLink();
        if (magnetLink != null) {
            //分隔
            if (magnetLink.indexOf("&") != -1) {
                magnet.magnetLink = magnetLink.substring(0, magnetLink.indexOf("&"));
            } else {
                magnet.magnetLink = magnetLink;
            }
        }
        return magnet;
    }

    public String getMagnetLink() {
        return magnetLink;
    }

    @Override
    public String toString() {
        return "MagnetLink{" +
                "magnetLink='" + magnetLink + '\'' +
                '}';
    }
}
