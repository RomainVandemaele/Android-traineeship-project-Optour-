package com.example.optitrip.utils.TSP

import java.security.SecureRandom

/**
 * Class implementing the nearest neighbour algorithm for the traveling salesman.
 *
 * Take a random/first point then take the closest to it add it to the  our.
 * Then update the distances and get the closest to the two and so on until a full tour is completed
 *
 * @property matrix the distance matrix
 * @property startIndex the index of the strat point if one exist or -1
 * @property endIndex the index of the last points if one exist or -1
 */
class NearestNeighBor(private val matrix : MutableList<MutableList<Int>>, private val startIndex: Int = -1, private val endIndex: Int = -1) {

    private val distanceToVertices = HashMap<Int,Int>()
    private val path = MutableList<Int>(0) {0}
    private val N = matrix.size

    init {
        for(i in 0..N-1 ) distanceToVertices[i] = 100000
        if(endIndex!=-1) distanceToVertices.remove(endIndex)
    }

    private fun getClosestVertex() : Int {
        var min = 10000000
        var minKey = -1

        for(key in distanceToVertices.keys) {
            if(distanceToVertices.get(key)!! in 0 until min) {
                min = distanceToVertices.get(key)!!
                minKey = key
            }
        }
        return minKey
    }


    fun algoNN(): MutableList<Int> {

        var cityIndex = if(startIndex!=-1) startIndex else SecureRandom().nextInt(N)
        var nTurn = N-1
        if(endIndex != -1 && startIndex!=endIndex) nTurn--
        //while there is another city to visit : go to the nearest one
        while(path.size < nTurn) {
            path.add(cityIndex)
            // cityVertex visited so remove from distance
            distanceToVertices.remove(cityIndex)
            for(distance in matrix[cityIndex].withIndex()) {
                val col = distance.index
                if(distance.value!=-1 && distanceToVertices.containsKey(col)) {
                    distanceToVertices[col] = Math.min( distanceToVertices[col]!! , distance.value)
                }
            }
            cityIndex = getClosestVertex()
        }
        path.add(cityIndex)
        if(endIndex != -1 && endIndex != startIndex ) path.add(endIndex)
        return path
    }
}