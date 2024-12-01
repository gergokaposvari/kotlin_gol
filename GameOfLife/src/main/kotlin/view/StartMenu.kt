package view

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.Stage

class StartMenu : Application() {

    //predefined rules for a grid made out of squares
    private val squareRules = listOf(
        "B3/S23", "B3678/S34678", "B36/S23", "B2/S", "B3/S012345678",
        "B1/S1", "B1357/S1357", "B4678/S35678", "B234/S", "B3/S12345",
        "B37/S12345", "B678/S345678"
    )

    //predefined rules for a grid made out of triangles
    private val triangleRules = listOf(
        "B2/S23", "B3/S23", "B2/S123", "B1/S"
    )

    //The start of the program/main menu
    override fun start(primaryStage: Stage) {
        val titleLabel = Label("Game of Life - Start Menu").apply {
            style = "-fx-font-size: 20px; -fx-font-weight: bold;"
        }

        //Grid selector
        val gridTypeLabel = Label("Select Grid Type:")
        val gridTypeToggleGroup = ToggleGroup()

        val triangleGridOption = RadioButton("Triangles").apply { toggleGroup = gridTypeToggleGroup }
        val squareGridOption = RadioButton("Squares").apply { toggleGroup = gridTypeToggleGroup }


        //Rule selector
        val ruleLabel = Label("Select Rule:")
        val ruleComboBox = ComboBox<String>().apply {
            isDisable = true
            promptText = "Choose grid first"
        }

        val startButton = Button("Start Game").apply {
            isDisable = true
        }

        gridTypeToggleGroup.selectedToggleProperty().addListener { _, _, newToggle ->
            if (newToggle != null) {
                when (newToggle) {
                    squareGridOption -> {
                        ruleComboBox.items.setAll(squareRules)
                    }
                    triangleGridOption -> {
                        ruleComboBox.items.setAll(triangleRules)
                    }
                }
                ruleComboBox.isDisable = false // Enable the ComboBox
                ruleComboBox.promptText = "Choose a rule"
            }
        }

        gridTypeToggleGroup.selectedToggleProperty().addListener { _, _, _ -> updateStartButton(startButton, gridTypeToggleGroup, ruleComboBox) }
        ruleComboBox.valueProperty().addListener { _ -> updateStartButton(startButton, gridTypeToggleGroup, ruleComboBox) }

        //Start button, only active when grid and rule are selected
        startButton.setOnAction {
            val selectedGridType = (gridTypeToggleGroup.selectedToggle as RadioButton).text
            val selectedRule = ruleComboBox.value

            if(selectedGridType == "Squares") {
                openSquareGameOfLifeWindow(primaryStage, selectedRule)
            }else if(selectedGridType == "Triangles") {
                openTriangleGameOfLifeWindow(primaryStage, selectedRule)
            }
        }

        //Layout
        val gridTypeBox = VBox(5.0, gridTypeLabel, triangleGridOption, squareGridOption)
        val ruleBox = VBox(5.0, ruleLabel, ruleComboBox)
        val root = VBox(15.0, titleLabel, gridTypeBox, ruleBox, startButton).apply {
            alignment = Pos.CENTER
            spacing = 20.0
            prefWidth = 400.0
        }
        root.padding = Insets(5.0, 5.0, 5.0, 5.0)

        // Scene and Stage
        val scene = Scene(root)
        primaryStage.scene = scene
        primaryStage.title = "Game of Life"
        primaryStage.show()
    }

    //Enables start button when rule and grid are selected
    private fun updateStartButton(
        startButton: Button,
        gridTypeToggleGroup: ToggleGroup,
        ruleComboBox: ComboBox<String>
    ) {
        startButton.isDisable = gridTypeToggleGroup.selectedToggle == null || ruleComboBox.value == null
    }

    //Starts a game of life with a square grid
    private fun openSquareGameOfLifeWindow(startMenuStage: Stage, ruleString: String) {
        startMenuStage.close()
        val pair = parseRules(ruleString)
        val gameOfLifeApp = RectangleGame(pair.first, pair.second)
        val gameOfLifeStage = Stage()

        gameOfLifeApp.start(gameOfLifeStage)
    }


    //Starts a game of life with a triangle grid
    private fun openTriangleGameOfLifeWindow(startMenuStage: Stage, ruleString: String) {
        startMenuStage.close()
        val pair = parseRules(ruleString)
        val gameOfLifeApp = TriangleGame(pair.first, pair.second)
        val gameOfLifeStage = Stage()

        gameOfLifeApp.start(gameOfLifeStage)
    }

    //Helper for parsing the rules out of the standard string
    private fun parseRules(ruleString: String): Pair<List<Int>, List<Int>> {
        val regex = Regex("B(\\d*)/S(\\d*)")
        val match = regex.matchEntire(ruleString)

        if (match != null) {
            val born = match.groupValues[1].mapNotNull { it.digitToIntOrNull() }
            val survive = match.groupValues[2].mapNotNull { it.digitToIntOrNull() }
            return Pair(born, survive)
        } else {
            throw IllegalArgumentException("Invalid rule format")
        }
    }
}

fun main() {
    Application.launch(StartMenu::class.java)
}