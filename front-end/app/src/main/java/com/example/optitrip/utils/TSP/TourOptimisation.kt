package com.example.optitrip.utils.TSP

/**
 * Class containing techniques to improve a base tour of the TSP problem :
 * 2-opt : takes 2 non-adjacent edges A-B and C-D to become A-C D-B if the new path is better
 *
 * @property path the initial path
 * @property matrix the distance matrix
 * @property startIndex the index where the path has to start if there is one otherwise -1
 * @property endIndex the index where the path has to end if there is one otherwise -1
 *
 *  [startIndex] can be the same as [endIndex]
 */
class TourOptimisation(
    var path : MutableList<Int>,
    private val matrix : MutableList<MutableList<Int>>,
    private val startIndex: Int,
    private val endIndex : Int) {

    private var N : Int = path.size

    init {
        //ADD a new node with zero distance to every other nodes to simulate full tour
        if(endIndex != startIndex) {
            N++
            matrix.forEach{ it.add(0)}
            matrix.add( MutableList<Int>(N) {0})
            matrix[N-1][N-1] = -1
            path.add(N-1)
            computeDistance()

        }

    }

    private fun computeDistance(path : MutableList<Int> = this.path) : Int {
        var pathDistance = 0
        for( i in 0..N-2) {
            pathDistance += matrix[path[i]][path[i+1]]
        }
        //println("Distance : $pathDistance")
        return pathDistance
    }




    private fun switch(i: Int, j:Int) : MutableList<Int> {
        val newPath = MutableList<Int>(0) {0}
        newPath.addAll(path.subList(0,i+1))
        for(k in  j downTo i+1) newPath.add(path[k])
        newPath.addAll(path.subList(j+1,path.size))

        return newPath
    }

    fun twoOpt() {
        var canBeBetter = true
        while (canBeBetter) {
            canBeBetter = false
            for((i, point) in path.withIndex()) {
                var j= i + 2
                var limit = N-1
                if(endIndex != -1) limit--
                while(j < limit) {
                    val oldDistance = computeDistance()
                    val newPath = switch(i,j)
                    val newDistance = computeDistance(newPath)
                    if(newDistance < oldDistance) {
                        canBeBetter = true
                        this.path = newPath
                    }
                    j++
                }
            }
        }


    }

}