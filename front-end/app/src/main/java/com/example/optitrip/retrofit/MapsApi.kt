package com.example.optitrip.retrofit

import com.example.optitrip.entities.directions.Direction
import com.example.optitrip.entities.directions.GeocodedWaypoints
import com.example.optitrip.entities.distanceMatrix.DistanceMatrix
import com.example.optitrip.entities.mapSearch.SearchMapResult
import com.example.optitrip.entities.reverseGeocoding.GeoCodingResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query




/**
 * Interface to do Google map API call with retrofit
 *getDistanceMatrix(
 */
interface MapsAPI {

    /**
     * Reverse geocoding : get info from a point coordinate
     * Doc : https://developers.google.com/maps/documentation/geocoding/requests-reverse-geocoding
     * @param apiKey the api key
     * @param coordinate the coordinate of the point in format "lat,long"
     */
    @GET("geocode/json?result_type=street_address")
    fun reverseGeocoding(@Query("key") apiKey : String, @Query("latlng") coordinate : String) : Call<GeoCodingResult?>

    /**
     * Find a place info by textquery like "berlin"
     * Doc : https://developers.google.com/maps/documentation/places/web-service/search-find-place
     * @param apiKey the api key
     * @param searchText the request ex :"berlin"
     * @param inputType the type of input it can be text or phone number
     * @param fields
     * @return
     */
    @GET("place/findplacefromtext/json?")
    fun findPlaceByText(@Query("key") apiKey: String, @Query("input") searchText : String, @Query("inputtype") inputType : String = "textquery", @Query("fields") fields : String = "formatted_address,name,geometry,type,opening_hours" ) : Call<SearchMapResult?>

    /**
     * Get distance matrix with a set of origin and destination points
     * Doc : https://developers.google.com/maps/documentation/distance-matrix/distance-matrix
     *
     * @param apiKey the api key
     */
    @GET("distancematrix/json?")
    fun getDistanceMatrix(@Query("key") apiKey: String,@Query("origins") origins : String, @Query("destinations") destinations : String, @Query("mode") mode: String) : Call<DistanceMatrix?>

    /**
     * Get direction between two point
     * Doc : https://developers.google.com/maps/documentation/directions/get-directions
     */
    @GET("directions/json?units=metric")
    fun getDirection(@Query("key") apiKey: String,@Query("origin") origin : String, @Query("destination") destination : String, @Query("waypoints") waypoints: String ,@Query("mode") mode: String) : Call<Direction?>
}