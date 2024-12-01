package model

class GameOfLifeTriangle(born: List<Int>, survive: List<Int>, height: Int, width: Int) : GameOfLife(born, survive, height, width) {



    //Overriding countAliveNeighbors/2 for a triangle, every vertex adjacent triangle is a neighbor.
    public override fun countAliveNeighbors(x: Int, y: Int): Int {
        var aliveCount = 0
        val flippedOrientation = if (x % 2 == 0) y % 2 == 0 else y % 2 == 1

        val neighbors = if (flippedOrientation) {
            listOf(
                //Neighbors for an upward pointing triangle
                Pair(1, -1), Pair(1, 0), Pair(1, 1),
                Pair(0, -2), Pair(0, -1), Pair(0, 1), Pair(0, 2),
                Pair(-1, -2), Pair(-1, -1), Pair(-1, 0), Pair(-1, 1), Pair(-1, 2)
            )

        } else {
            //Neighbors for a downward pointing triangle
            listOf(
                Pair(1, -2), Pair(1, -1), Pair(1, 0), Pair(1, 1), Pair(1, 2),
                Pair(0, -2), Pair(0, -1), Pair(0, 1), Pair(0, 2),
                Pair(-1, -1), Pair(-1, 0), Pair(-1, 1)
            )
        }

        //Count the alive neighbors
        for ((dx, dy) in neighbors) {
            val nx = x + dx
            val ny = y + dy

            if (nx in 0 until height && ny in 0 until width) {
                if (getCellState(nx, ny) == 100) {
                    aliveCount++
                }
            }
        }

        return aliveCount
    }
}