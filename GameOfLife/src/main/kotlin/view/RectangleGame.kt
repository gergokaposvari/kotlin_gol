package view

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import javafx.util.Duration
import model.GameOfLife
import model.GameOfLifeTriangle

class RectangleGame(private val born: List<Int>, private val survive: List<Int>) : Application() {

    private val gameOfLife = GameOfLife(born, survive, 75, 75)
    private val gridPane = GridPane()
    private var hasStarted = false
    private lateinit var timeline: Timeline
    private var simulationSpeed: Double = 500.0

    override fun start(primaryStage: Stage) {
        gridPane.hgap = 1.0
        gridPane.vgap = 1.0

        //Draws the grid
        for (i in 0 until 75) {
            for (j in 0 until 75) {
                val rect = Rectangle(10.0, 10.0)
                updateRectangleColor(rect, i, j)

                rect.setOnMouseClicked { event->
                    gameOfLife.changeState(i, j)
                    updateRectangleColor(rect, i, j)
                }
                gridPane.add(rect, i, j)
            }
        }

        //Buttons
        val buttonBox = VBox(10.0)
        buttonBox.alignment = Pos.CENTER
        val startButton = Button("Start")
        val stopButton = Button("Stop")
        val speedUpButton = Button("Speed Up")
        val slowDownButton = Button("Slow Down")
        val resetButton = Button("Reset")
        val randomizeButton = Button("Randomize")
        val toggleFadingButton = Button("Toggle Fading")

        startButton.isDisable = false
        stopButton.isDisable = true

        buttonBox.children.addAll(startButton, stopButton, speedUpButton, slowDownButton, resetButton, randomizeButton, toggleFadingButton)

        startButton.setOnAction {
            startSimulation()
            startButton.isDisable = true
            stopButton.isDisable = false
        }
        stopButton.setOnAction {
            stopSimulation()
            stopButton.isDisable = true
            startButton.isDisable = false
        }
        speedUpButton.setOnAction { speedUpSimulation() }
        slowDownButton.setOnAction { slowDownSimulation() }
        resetButton.setOnAction { gameOfLife.resetSimulation() }
        randomizeButton.setOnAction { randomizeGrid() }
        toggleFadingButton.setOnAction { toggleFading() }

        //Layout
        val hbox = HBox(20.0)
        hbox.children.addAll(gridPane, buttonBox)
        hbox.padding = Insets(10.0, 10.0, 10.0, 10.0)

        //Scene and stage
        val scene = Scene(hbox, 1000.0, 1000.0)
        primaryStage.title = "Game of Life"
        primaryStage.scene = scene
        primaryStage.show()
    }

    //Selects the color of the rectangle based on its value
    private fun updateRectangleColor(rect: Rectangle, x: Int, y: Int) {
        val cellState = gameOfLife.getCurrentState()[x][y]

        val deadColor = Color.color(243.0 / 255.0, 217.0 / 255.0, 151.0 / 255.0)
        val aliveColor = Color.color(173.0 / 255.0, 209.0 / 255.0, 245.0 / 255.0)

        val progress = cellState / 100.0
        rect.fill = deadColor.interpolate(aliveColor, progress)
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
        simulationSpeed = (simulationSpeed * 0.8).coerceAtLeast(100.0)
        restartSimulation()
    }

    private fun slowDownSimulation() {
        simulationSpeed *= 1.2
        restartSimulation()
    }

    private fun restartSimulation() {
        timeline.stop()
        startSimulation()
    }

    private fun randomizeGrid(){
        gameOfLife.randomizeGrid()
        refreshGrid()
    }

    private fun toggleFading(){
        gameOfLife.changeFading()
    }
    
    //Refreshes the visual grid to match the model
    private fun refreshGrid() {
        for (i in 0 until 75) {
            for (j in 0 until 75) {
                val rect = gridPane.children[i * 75 + j] as Rectangle
                updateRectangleColor(rect, i, j)
            }
        }
    }
}

