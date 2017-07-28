/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adriens.oracle.jutils.exiftest;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import java.io.File;
import java.util.Collection;

/**
 *
 * @author salad74
 */
public class CleanReader {

    public static void main(String[] args) {
        try {
            String fileName = "photo.jpg";
            Metadata metadata = ImageMetadataReader.readMetadata(new File(fileName));
            Collection<GpsDirectory> gpsDirectories = metadata.getDirectoriesOfType(GpsDirectory.class);
            for (GpsDirectory gpsDirectory : gpsDirectories) {
                // Try to read out the location, making sure it's non-zero
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null && !geoLocation.isZero()) {
                    // Add to our collection for use below
                    // we have a geolocaation here !
                    System.out.println("longitude : " + geoLocation.getLongitude());
                    System.out.println("latitude : " + geoLocation.getLatitude());

                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
