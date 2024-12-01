package model

open class GameOfLife(private val born: List<Int>, private val survive: List<Int>, val height: Int, val width: Int) {

    //The two states
    companion object {
        const val DEAD = 0
        const val ALIVE = 100
    }


    //The grid
    private var currentState: Array<Array<Int>> = Array(height) { Array(width) { DEAD } }
    private var nextState: Array<Array<Int>> = Array(height) { Array(width) { DEAD } }

    //Variable for changing between fading out and instant death
    private var isFading = false

    public fun getCurrentState(): Array<Array<Int>> {
        return currentState
    }

    public fun getBorn(): List<Int> {
        return born
    }

    public fun getSurvive(): List<Int> {
        return survive
    }

    //Randomizes the grid, for some rules this is needed
    fun randomizeGrid() {
        val random = kotlin.random.Random

        for (i in 0 until height) {
            for (j in 0 until width) {
                currentState[i][j] = if (random.nextBoolean()) ALIVE else DEAD
            }
        }
    }



    public fun getCellState(
        x: Int,
        y: Int
    ): Int {
        return currentState[x][y]
    }

    //Counts how many alive neighbors the cell at x,y has
    //When creating a new game of life for a different shape,
    //this is the only function one has to override, everything else is applicable
    open fun countAliveNeighbors(x: Int, y: Int): Int {
        val neighbors = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1),               Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )

        var aliveCount = 0

        for ((dx, dy) in neighbors) {
            val nx = x + dx
            val ny = y + dy

            if (nx in 0 until height && ny in 0 until width) {
                //Only a fully alive cell is considered alive, even when fading out
                // the cell is considered dead
                if (currentState[nx][ny] == 100) {
                    aliveCount++
                }
            }
        }

        return aliveCount
    }

    //Calculates the next state based on the current state
    private fun calculateNextState() {
        //For instant death we only toggle between ALIVE and DEAD
        if (!isFading) {
            for (i in 0 until height) {
                for (j in 0 until width) {
                    if (currentState[i][j] == DEAD) {
                        if (born.contains(countAliveNeighbors(i, j))) {
                            nextState[i][j] = ALIVE
                        }
                    } else if (currentState[i][j] == ALIVE) {
                        if (survive.contains(countAliveNeighbors(i, j))) {
                            nextState[i][j] = ALIVE
                        } else {
                            nextState[i][j] = DEAD
                        }
                    }
                }
            }
        } else {
            //If fading is turned on a cell is slowly going from alive (=100)
            //to dead (=0) in increments of 10
            for (i in 0 until height) {
                for (j in 0 until width) {
                    if (currentState[i][j] == ALIVE) {
                        if (survive.contains(countAliveNeighbors(i, j))) {
                            nextState[i][j] = ALIVE
                        } else {
                            nextState[i][j] = 90
                        }
                    } else {
                        if (born.contains(countAliveNeighbors(i, j))) {
                            nextState[i][j] = ALIVE
                        } else {
                            nextState[i][j] = if (currentState[i][j] == DEAD) DEAD else currentState[i][j] - 10
                        }
                    }
                }
            }
        }

    }

    //This toggles the cells state between alive and dead
    public fun changeState(x: Int, y: Int) {
        if(currentState[x][y] == 100) {
            currentState[x][y] = 0
            nextState[x][y] = 0
        }else{
            currentState[x][y] = 100
            nextState[x][y] = 100
        }
    }

    //After turning off fading there might be some values that arent DEAD nor ALIVE
    // so they have to be erased
    private fun deleteNonBinary() {
        for(i in 0 until height) {
            for (j in 0 until width) {
                if(currentState[i][j] != ALIVE && currentState[i][j] != DEAD) {
                    currentState[i][j] = DEAD
                    nextState[i][j] = DEAD
                }
            }
        }
    }

    //Simulates one turn
    public fun simulateGeneration(){
        calculateNextState()
        currentState = nextState.copyOf()
        nextState = Array(height) { Array(width) { DEAD } }
    }

    public fun resetSimulation(){
        currentState = Array(height) { Array(width) { DEAD } }
        nextState = Array(height) { Array(width) { DEAD } }
    }

    public fun changeFading(){
        isFading = !isFading
        if(!isFading){
            deleteNonBinary()
        }
    }

    //For debugging purposes, writes the game of life to console
    override fun toString(): String {
        val builder = StringBuilder()

        for (i in 0 until height) {
            for (j in 0 until width) {
                builder.append(
                    this.countAliveNeighbors(i, j)
                )
            }
            builder.append("\n")
        }

        return builder.toString()
    }

}