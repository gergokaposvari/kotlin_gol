package view

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.math.sqrt
import model.GameOfLife
import model.GameOfLifeTriangle


class TriangleGame(private val born: List<Int>, private val survive: List<Int>) : Application() {

    private val gridRows = 32
    private val gridCols = 95

    private val gameOfLife = GameOfLifeTriangle(born, survive, gridRows, gridCols)
    private val pane = Pane()
    private lateinit var timeline: Timeline
    private var simulationSpeed: Double = 500.0

    companion object {
        //Triangle parameters
        private const val TRIANGLE_SIZE = 12.0
        private const val TRIANGLE_HEIGHT = TRIANGLE_SIZE * 2
        private val TRIANGLE_SIDE = TRIANGLE_HEIGHT * sqrt(3.0) / 2
    }




    override fun start(primaryStage: Stage) {
        pane.padding = Insets(10.0)
        val gap = 1.03
        //This makes the triangle grid
        for (row in 0 until gridRows) {
            for (col in 0 until gridCols) {
                val x = (col * TRIANGLE_SIDE / 2 + TRIANGLE_SIDE /2)*gap
                val y = (row * TRIANGLE_HEIGHT + TRIANGLE_HEIGHT)*gap

                val flipped = if (row % 2 == 0) col % 2 == 0 else col % 2 == 1

                val triangle = createTriangle(x, y, flipped)
                pane.children.add(triangle)

                triangle.setOnMouseClicked {
                    gameOfLife.changeState(row, col)
                    updateTriangleColor(triangle, gameOfLife.getCellState(row, col))
                }

                updateTriangleColor(triangle, gameOfLife.getCellState(row, col))
            }
        }

        //The buttons which control the game
        val buttonBox = VBox(10.0)
        buttonBox.alignment = Pos.CENTER
        val startButton = Button("Start")
        val stopButton = Button("Stop")
        val speedUpButton = Button("Speed Up")
        val slowDownButton = Button("Slow Down")
        val resetButton = Button("Reset")
        val randomizeButton = Button("Randomize")
        val toggleFadingButton = Button("Toggle Fading")

        buttonBox.children.addAll(
            startButton, stopButton, speedUpButton,
            slowDownButton, resetButton, randomizeButton,
            toggleFadingButton
        )

        startButton.setOnAction { startSimulation() }
        stopButton.setOnAction { stopSimulation() }
        speedUpButton.setOnAction { speedUpSimulation() }
        slowDownButton.setOnAction { slowDownSimulation() }
        resetButton.setOnAction { gameOfLife.resetSimulation() }
        randomizeButton.setOnAction { randomizeGrid() }
        toggleFadingButton.setOnAction { toggleFading() }

        val hbox = HBox(20.0)
        hbox.children.addAll(pane, buttonBox)
        hbox.padding = Insets(10.0)

        val scene = Scene(hbox, 1200.0, 900.0)
        primaryStage.title = "Game of Life - Packed Triangles"
        primaryStage.scene = scene
        primaryStage.show()
    }

    //Draws a triangle at x,y with the triangle parameters at the top of the class
    private fun createTriangle(x: Double, y: Double, flipped: Boolean): Polygon {
        return if (flipped) {
            Polygon(
                x, y + TRIANGLE_HEIGHT / 2,
                x + TRIANGLE_SIDE / 2, y - TRIANGLE_HEIGHT / 2,
                x - TRIANGLE_SIDE / 2, y - TRIANGLE_HEIGHT / 2
            )
        } else {
            Polygon(
                x, y - TRIANGLE_HEIGHT / 2,
                x + TRIANGLE_SIDE / 2, y + TRIANGLE_HEIGHT / 2,
                x - TRIANGLE_SIDE / 2, y + TRIANGLE_HEIGHT / 2
            )
        }
    }

    //Calculates the color of the triangle
    private fun updateTriangleColor(triangle: Polygon, state: Int) {
        val deadColor = Color.color(243.0 / 255.0, 217.0 / 255.0, 151.0 / 255.0)
        val aliveColor = Color.color(173.0 / 255.0, 209.0 / 255.0, 245.0 / 255.0)

        val progress = state / 100.0
        triangle.fill = deadColor.interpolate(aliveColor, progress)
    }


    private fun startSimulation() {
        timeline = Timeline(
            KeyFrame(Duration.millis(simulationSpeed), {
                gameOfLife.simulateGeneration()
                refreshGrid()

            })
        )
        timeline.cycleCount = Timeline.INDEFINITE
        timeline.play()
    }

    private fun stopSimulation() {
        timeline.stop()
    }

    private fun speedUpSimulation() {
        simulationSpeed = (simulationSpeed * 0.5).coerceAtLeast(100.0)
        restartSimulation()
    }

    private fun slowDownSimulation() {
        simulationSpeed *= 2
        restartSimulation()
    }

    private fun restartSimulation() {
        timeline.stop()
        startSimulation()
    }

    private fun randomizeGrid() {
        gameOfLife.randomizeGrid()
        refreshGrid()
    }

    private fun toggleFading() {
        gameOfLife.changeFading()
    }

    //Matches the visual grid to the current state of game of life
    private fun refreshGrid() {
        for ((index, node) in pane.children.withIndex()) {
            val col = index % gridCols
            val row = index / gridCols

            if (row in 0 until gridRows && col in 0 until gridCols) {
                val triangle = node as Polygon
                updateTriangleColor(triangle, gameOfLife.getCellState(row, col))
            }
        }
    }
}