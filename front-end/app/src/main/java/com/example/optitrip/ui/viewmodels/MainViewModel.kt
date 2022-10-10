package com.example.optitrip.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.example.optitrip.R
import com.example.optitrip.entities.Client
import com.example.optitrip.entities.Comment
import com.example.optitrip.entities.CommentDB
import com.example.optitrip.entities.TripBasic
import com.example.optitrip.entities.directions.Direction
import com.example.optitrip.entities.distanceMatrix.DistanceMatrix
import com.example.optitrip.entities.mapSearch.SearchMapResult
import com.example.optitrip.entities.reverseGeocoding.GeoCodingResult
import com.example.optitrip.entities.trip.Point
import com.example.optitrip.entities.trip.Step
import com.example.optitrip.entities.trip.Trip
import com.example.optitrip.retrofit.*
import com.example.optitrip.room.AppDatabase
import com.example.optitrip.room.daos.TripDao
import com.example.optitrip.room.entities.TripDB
import com.example.optitrip.utils.ApiCall
import com.example.optitrip.utils.Resource
import com.example.optitrip.utils.TSP.TSP
import com.example.optitrip.utils.enums.Mode
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.launch
import java.lang.StringBuilder

/**
 * Main view model linked to all activities exception connection ones
 *
 * LiveDat naming :
 *  _name for private mutable [LiveData]
 *  name for accessible version that can be observed
 *
 * @property context
 * @property _points the points put by the user to create a trip
 * @property _createdTripId the id of the newly created trip
 * @property _tripPoints   the set of points that indicates the directions to follow for a trip
 * @property _deletedTrip  [Boolean] indicated if a trip was successfully deleted or not
 * @property _selectedTrip  the trip selected in home fragment to be displayed
 * @property _tripsClient  the set of trips of the current logged client
 * @property _tripsSearch  the set of trips returned by a client search
 * @property _comments the set of comments on a trip
 *
 */
class MainViewModel(private val context: Context) : ViewModel() {


    var tspStartIndex: Int = -1
    var tspEndIndex: Int = -1

    private val tripDao : TripDao = AppDatabase.instance(context).TripDao()

    private val _geoCoding: MutableLiveData<Resource<GeoCodingResult>> = MutableLiveData()
    val geoCoding : LiveData<Resource<GeoCodingResult>> = _geoCoding

    private val _mapSearch: MutableLiveData<Resource<SearchMapResult>> = MutableLiveData()
    val mapSearch : LiveData<Resource<SearchMapResult>> = _mapSearch

    private val _points : MutableLiveData<MutableList<Point>> = MutableLiveData<MutableList<Point>>()
    val points: LiveData<MutableList<Point>> get() = _points

    private val _createdTripId : MutableLiveData<Resource<Int>> = MutableLiveData<Resource<Int>>()
    val createdTripId : LiveData<Resource<Int>> = _createdTripId

    private val _selectedTrip : MutableLiveData<Resource<Trip>> = MutableLiveData<Resource<Trip>>()
    val selectedTrip : LiveData<Resource<Trip>> = _selectedTrip

    private val _selectedScheduleTrip : MutableLiveData<Resource<Trip>> = MutableLiveData<Resource<Trip>>()
    val selectedScheduleTrip : LiveData<Resource<Trip>> = _selectedScheduleTrip

    private val _selectedEditTrip : MutableLiveData<Trip?> = MutableLiveData<Trip?>()
    val editTrip : LiveData<Trip?> = _selectedEditTrip

    private val _tripPoints : MutableLiveData<Array<String>> = MutableLiveData()
    val tripPoints : LiveData<Array<String>> = _tripPoints

    private val _tripsClient : MutableLiveData<Resource<Array<TripBasic>>> = MutableLiveData<Resource<Array<TripBasic>>>()
    val tripsClient : LiveData<Resource<Array<TripBasic>>> = _tripsClient

    private val _tripsSearch : MutableLiveData<Resource<Array<TripBasic>>> = MutableLiveData<Resource<Array<TripBasic>>>()
    val tripsSearch : LiveData<Resource<Array<TripBasic>>> = _tripsSearch

    private val _deletedTrip : MutableLiveData<Resource<Trip>> = MutableLiveData()
    val deletedTrip : LiveData<Resource<Trip>> = _deletedTrip

    private val _comments : MutableLiveData<Resource<Array<Comment>>> = MutableLiveData<Resource<Array<Comment>>>()
    val comments : LiveData<Resource<Array<Comment>>> = _comments



    private val key = context.resources.getString(R.string.MAP_API_KEY)

    private val tripApi : TripAPI       = RetrofitClient.clientLocal.create(TripAPI::class.java)
    private val mapApi: MapsAPI         = RetrofitClient.clientMap.create(MapsAPI::class.java)
    private val commentAPI : CommentAPI = RetrofitClient.clientLocal.create(CommentAPI::class.java)
    private val clientAPI : ClientAPI   = RetrofitClient.clientLocal.create(ClientAPI::class.java)



    /**
     * API call to find info on a coordinate point such as its address.
     * Result in [_geoCoding]
     *
     * @param point the coordinates to search for
     */
    fun findInfo(point : LatLng) {
        val apiCall = ApiCall<GeoCodingResult>()
        apiCall.makeCall(mapApi.reverseGeocoding(key,"${point.latitude},${point.longitude}"),this._geoCoding)
    }

    /**
     * Api call to find a place coordinates from a text search
     * result in [_mapSearch]
     *
     * @param searchInput the place the user is looking for
     */
    fun searchMap(searchInput : String) {

        val apiCall = ApiCall<SearchMapResult>()
        apiCall.makeCall(mapApi.findPlaceByText(key,searchInput),this._mapSearch)
    }

    /**
     * API call to start create a trip from data from fragment
     *This function serves to compute distances in order to compute the best itinerary in [insertTrip]
     * @param points point of the trip
     * @param name trip name
     * @param selectedMode mode of transport
     */
    fun createTrip(points: MutableList<Point>, name: String, selectedMode: String) {

        val origins : String = points.map { p -> "place_id:${p.placeId}" }.joinToString("|")
        val mode : Mode = Mode.values().find { it.mode == selectedMode }!!
        val apiCall = ApiCall<DistanceMatrix>()
        apiCall.postProcessing = {
            insertTrip(points,name,mode.mapQuery,it)
        }
        apiCall.makeCall(mapApi.getDistanceMatrix(key,origins,origins,mode.mapQuery),null)

    }

    /**
     * Insert the generated trip into the DB
     *
     * @param points
     * @param name
     * @param selectedMode
     * @param distanceMatrix
     */
    private fun insertTrip(points: MutableList<Point>, name: String, selectedMode: String, distanceMatrix: DistanceMatrix?) {
        val  sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val clientId = sharedPreferences.getInt("clientId",0)

        //TSP reordering
        val travelingSalesman = TSP(points,distanceMatrix!!,tspStartIndex,tspEndIndex)
        val newPoints = travelingSalesman.getPath()

        val trip = Trip(null,name,clientId, grade = 0, points = ArrayList(newPoints))
        val steps = ArrayList<Step>()
        for( i in 0..points.size-2) {
            val element = distanceMatrix!!.rows[i].elements[i+1]
            steps.add( Step(null,null, stepOrder = i+1, stepMode = selectedMode, stepLength = element.distance!!.value!!.toDouble(), stepTime = element.duration!!.value!!) )
        }
        trip.steps = steps
        trip.tripId = this._selectedEditTrip.value?.tripId

        val apiCall = ApiCall<Int>()
        apiCall.postProcessing = {
            _selectedEditTrip.value = null
            tspStartIndex = -1
            tspEndIndex = -1
            resetPoints()
        }
        apiCall.makeCall(tripApi.postTrip(trip),_createdTripId)

    }



    /**
     * formatting the points of the trip to a format for the external API [MapsAPI]
     *
     * @param points
     * @return
     */
    private fun formatPoints(vararg points : Point) : String{
        var formattedString = StringBuilder("")
        for( i in 0..points.lastIndex) {
            formattedString.append("place_id:").append(points[i].placeId)
            if(i!= points.lastIndex) formattedString.append("|")
        }
        return formattedString.toString()
    }

    /**
     * Get a trip from its id vie api call for [ScheduleFragment].
     * Result stores in [_selectedScheduleTrip]
     *
     * @param tripId
     */
    fun getTripById(tripId : Int) {
        val apiCall = ApiCall<Trip>()
        apiCall.makeCall(tripApi.getTrip(tripId),_selectedScheduleTrip)
    }

    /**
     * Request from a trip id to the trip as live data and then call for computing directions
     *
     * @param tripId the id of the trip
     */
    fun getTripDirection(tripId: Int) {
        val apiCall = ApiCall<Trip>()
        apiCall.postProcessing = {
            if(it!=null) computeDirectionPoints(it)
        }
        apiCall.makeCall(tripApi.getTrip(tripId),_selectedTrip)
    }

    /**
     * Compute direction points or a trip via a google map api request
     *
     * @param trip the trip to compute direction
     */
    private fun computeDirectionPoints(trip : Trip) {

        //GET INFO
        val mode = trip.steps[0].stepMode!!
        val points = trip.points
        val origin = formatPoints(points[0])
        val destination = formatPoints(points[points.lastIndex])
        val intermediatePoints = points.filter { it!=points[0] }.filter { it!=points[points.lastIndex] }
        val waypoints = formatPoints( *intermediatePoints.toTypedArray())

        val apiCall = ApiCall<Direction>()
        apiCall.postProcessing = {
            if(it!=null ) {
                val polylines : MutableList<String> = mutableListOf()
                val legs = it.routes[0].legs
                legs.forEach{it.steps.forEach { step -> polylines.add(step.polyline?.points!!) }}
                _tripPoints.value = polylines.toTypedArray()
            }
        }

        apiCall.makeCall(mapApi.getDirection(key,origin,destination,waypoints,mode),MutableLiveData<Resource<Direction>>())

    }



    /**
     * Api call to delete a [Trip] in DB
     *
     * @param tripId the id of the trip to delete
     */
    fun deleteTrip(tripId : Int) {
        val apiCall = ApiCall<Trip>()
        apiCall.makeCall(tripApi.deleteTrip(tripId),_deletedTrip)
    }

    /**
     * Api call to get all trips of a [Client].
     * Result in [_tripsClient]
     *
     * @param clientId the id of the [Client]
     */
    fun getTripsByClient(clientId : Int) {
        val apiCall = ApiCall<Array<TripBasic>>()
        apiCall.postProcessing  = {
            viewModelScope.launch {
                val tripsDB = it?.map { t -> TripDB(t.tripId,t.tripName,t.grade,t.clientId) }?.toTypedArray()
                tripDao.deleteAll()
                tripDao.insert(*tripsDB!!)
            }
        }
        apiCall.makeCall(tripApi.getTripsByClient(clientId),_tripsClient)
    }

    /**
     * Api call to get all trips from a search text.
     * Result in [_tripsSearch]
     *
     * @param query the search text
     */
    fun getTripBySearch(query: String) {
        val apiCall = ApiCall<Array<TripBasic>>()
        apiCall.makeCall(tripApi.getTripsByText(query),_tripsSearch)
    }

    /**
     * Get comments by trip id
     *
     * @param tripId
     */
    fun getCommentsByTrip(tripId : Int) {
        val call = ApiCall<Array<Comment>>()
        call.makeCall(commentAPI.getTripComments(tripId),_comments)
    }

    /**
     * Api call to upload a comment on a trip
     *
     * @param comment the comment
     * @param username the username of the [Client] that post it
     */
    fun uploadComment(comment: CommentDB,username : String) {
        val call = ApiCall<String>()
        call.postProcessing  = {
            val newComment = mutableListOf<Comment>()
            if(_comments.value!!.status == Resource.Status.SUCCESS) newComment.addAll(_comments.value!!.data!!)
            newComment.add(Comment(comment.comment,username))
            _comments.value = Resource.success(newComment.toTypedArray())
        }
        call.makeCall(commentAPI.postComment(comment),null)

    }

    /**
     * Set the index for marker that start/end a [Trip] if they exist
     *
     * @param startMarker the marker (point) that starts the trip
     * @param endMarker the marker(point)  that ends the trip
     */
    fun setMarkerIndex(startMarker: Marker?, endMarker: Marker?) {
        this.tspStartIndex = -1
        this.tspEndIndex = -1
        for( i in 0 until this.points.value!!.size) {

            if(startMarker!=null && points.value!![i].placeId == startMarker?.tag as String ) {
                this.tspStartIndex = i
            }
            if(endMarker!=null && points.value!![i].placeId == endMarker?.tag as String ) {
                this.tspEndIndex = i
            }
        }
        Log.d("INDEX","${this.tspStartIndex} ${this.tspEndIndex}")


    }


    /**
     * Api call to delete a client
     *
     * @param clientId the id of the client to delete
     */
    fun deleteClient(clientId : Int) {
        val apiCall = ApiCall<String>()
        apiCall.makeCall(clientAPI.deleteClient(clientId),null)
    }

    /**
     * Api call to change a client username
     *
     * @param clientId the id of the client to update
     * @param username the new username
     */
    fun changeUsername(clientId : Int,username: String) {
        val apiCall = ApiCall<String>()
        apiCall.makeCall(clientAPI.changeUsername(clientId,username),null)
    }

    /**
     * API call to change a client password
     *
     * @param clientId the id of the client to update
     * @param password the new password
     */
    fun changePassword(clientId : Int,password : String) {
        val apiCall = ApiCall<String>()
        apiCall.makeCall(clientAPI.changePassword(clientId,password),null)
    }


    /**
     * reset [_tripPoints] to neutral value in order to not trigger observer action
     *
     */
    fun resetTripPoints() {
        this._tripPoints.value = arrayOf()
    }

    /**
     * reset [_deletedTrip] to neutral value in order to not trigger observer action
     *
     */
    fun resetDeletedTrip() {
        this._deletedTrip.value = Resource.success(null)
    }

    /**
     * reset [_selectedTrip] to neutral value in order to not trigger observer action
     *
     */
    fun resetSelectedTrip() {
        this._selectedTrip.value = Resource.success(null)
    }

    /**
     * Reset [_createdTrip] in order to not call observer
     *
     */
    fun resetCreatedTripId() {
        _createdTripId.value = Resource.loading(null)
    }

    /**
     * Delete stored points to clear the map
     *
     */
    private fun resetPoints() {
        this._points.value = mutableListOf()
    }

    /**
     * Set points to save markers from [MapsFragment]
     *
     * @param points
     */
    fun setPoints(points : MutableList<Point>) {
        this._points.value = points
    }

    /**
     * TODODelete a point which represents a marker on the map
     *
     * @param pos the position ofthe point to delete in [_points]
     */
    fun deletePoint(pos : Int) {
        points.value!!.removeAt(pos)
        _points.value = _points.value
    }

    /**
     * Set rip to edit as [trip]
     *
     * @param trip the trip to edit
     */
    fun setEditTrip(trip : Trip?) {
        _selectedEditTrip.value = trip
    }


}