package com.example.optitrip.utils.TSP

import android.util.Log
import com.example.optitrip.entities.distanceMatrix.DistanceMatrix
import com.example.optitrip.entities.trip.Point

/**
 * Class solving the traveling salesman problem
 *
 * @property points the points to go through
 * @property distanceMatrix the distance between eahc points
 * @property startIndex the index of the strat point if one exist or -1
 * @property endIndex the index of the last points if one exist or -1
 */
class TSP(
    private val points : MutableList<Point>,
    private val distanceMatrix: DistanceMatrix,
    private val startIndex: Int = -1,
    private val endIndex: Int = -1) {

    fun getPath() : MutableList<Point> {

        //convert distance matrix to matrix with number representing time
        val n = this.points.size
        val matrix = MutableList<MutableList<Int>>(n) { MutableList<Int>(n) {  -1 } }
        for((i,row) in distanceMatrix.rows.withIndex()) {
            for( (j,element) in row.elements.withIndex()) {
                if(i==j) matrix[i][j] = -1
                else matrix[i][j] = element.duration!!.value!!
            }
        }

        //Get first first though Nearest neighbour
        val initialPath = NearestNeighBor(matrix,startIndex, endIndex).algoNN()
        initialPath.forEach { Log.d("PATH","$it ") }
        val opt = TourOptimisation(initialPath,matrix,startIndex, endIndex)
        //optimize route via 2-opt
        opt.twoOpt()
        val newPath = opt.path
        newPath.forEach { Log.d("NEW PATH","$it ") }
        if( (startIndex == -1 && endIndex ==-1) || startIndex != endIndex) return MutableList<Point>(n) { points[newPath[it]]}
        else {
            //if path is a loop close the loop by adding a point to start
            newPath.add(startIndex)
            return MutableList<Point>(n+1) { points[newPath[it]]}
        }
    }
}