package tornadoFX

import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import tornadofx.*
import core.*
import controller.*
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.shape.Rectangle
import kotlin.concurrent.timer

class CheckersView : View() {
    private var blackComputer = !(app as CheckersApp).blackHuman
    private var whiteComputer = !(app as CheckersApp).whiteHuman

    override val root = BorderPane()
    private lateinit var board: FieldGraphics
    private var logic = Logic()
    private val listCells = List(8) { MutableList(8) { Rectangle() } }
    private lateinit var statusLabel: Label
    private val listImage = List(8) { MutableList(8) { ImageView() } }
    private var inGame = true



    init {
        title = "Checkers"
        with(root) {
            top {
                vbox {
                    menubar {
                        menu("Game") {
                            item("Restart").action {
                                restart()
                            }
                            separator()
                            item("Exit").action {
                                this@CheckersView.close()
                            }
                        }
                    }
                }
            }
            center {
                gridpane {
                    val colorTan = Color.TAN
                    val colorCornsilk = Color.CORNSILK
                    var pair: Pair<Int?, Int?> = null to null
                    for(row in 0..7) {
                        row {
                            for (column in 0..7) {
                                stackpane {

                                    listCells[row][column] = rectangle().apply {
                                        fill = if((column + row) % 2 == 1) {
                                            colorTan
                                        } else colorCornsilk
                                        width = 64.0
                                        height = 64.0
                                    }

                                    listImage[row][column] = imageview {
                                        fitHeightProperty().bind(
                                            listCells[row][column].heightProperty() / 11 * 10
                                        )
                                        fitWidthProperty().bind(fitHeightProperty())
                                    }

                                    onDragDetected = EventHandler { event ->
                                        val db: Dragboard = startDragAndDrop(TransferMode.MOVE)
                                        val content = ClipboardContent()
                                        content.putImage(board.getListImage(row, column))
                                        db.setContent(content)
                                        board.setListImage(row, column, null)
                                        event.consume()
                                    }

                                    onDragOver = EventHandler { event ->
                                        if (event.gestureSource != this && event.dragboard.hasImage()) {
                                            event.acceptTransferModes(*TransferMode.ANY)
                                        }
                                        event.consume()
                                    }

                                    onDragDropped = EventHandler { event ->
                                        val db = event.dragboard
                                        var done = false
                                        if(db. hasImage()) {
                                            done = true
                                            pair = row to column
                                        }
                                        event.isDropCompleted = done
                                        event.consume()
                                    }

                                    onDragDone = EventHandler { event ->
                                        val db = event.dragboard
                                        board.setListImage(row, column, db.image)
                                        if(TransferMode.MOVE == event.transferMode) {
                                            logic.moveOrEat(row, column, pair.first, pair.second)
                                        }
                                        pair = null to null
                                        textTurn()
                                        event.consume()
                                    }
                                }
                            }
                        }
                    }
                    board = FieldGraphics(listImage)
                    logic.setBoard(board)
                }
            }
            bottom {
                statusLabel = label("")
            }
        }
        spawnField()
        time()
        textTurn()
    }

    private fun time() {
        if (inGame) {
            timer(daemon = true, period = 1500) {
                computerGame()
            }
        }
    }

    private fun computerGame() {
        val cmp = ComputerPlayer()

        if (logic.getTurn() == core.Color.WHITE && whiteComputer) {
            botTurn(cmp, core.Color.WHITE)
        }
        if (logic.getTurn() == core.Color.BLACK && blackComputer) {
            botTurn(cmp, core.Color.BLACK)
        }
    }

    private fun botTurn(cmp: ComputerPlayer, color: core.Color) {
        var counter = 0
        val list = cmp.wafer(2, color, board.allCheckers(board))
        val moves = list.moves

        println("Оценка хода - ${list.evaluation}")
        for (move in moves) {
            println(move)
        }
        println("___________")

        while (counter < moves.size - 1) {
            logic.moveOrEat(moves[counter].first, moves[counter].second, moves[counter + 1].first, moves[counter + 1].second)
            counter++
        }
        if (board.loser(core.Color.BLACK) || board.loser(core.Color.WHITE)) {
            inGame = false
        }
    }

    private fun textTurn() {
        val enemyColor = logic.getTurn().oppositen()
        statusLabel.text = if (!board.loser(logic.getTurn())) {
            "Game in process: ${logic.getTurn()} turn"
        } else {
            inGame = false
            "Game finished: $enemyColor win"
        }
    }

    private fun spawnField() {
        with(board) {
            for(row in 0 until 8 step 2) {
                spawnChecker(false, 1, row, Checker(core.Color.BLACK))
                spawnChecker(false, 5, row, Checker(core.Color.WHITE))
                spawnChecker(false, 7, row, Checker(core.Color.WHITE))
            }

            for(row in 1 until 8 step 2) {
                spawnChecker(false, 0, row, Checker(core.Color.BLACK))
                spawnChecker(false, 2, row, Checker(core.Color.BLACK))
                spawnChecker(false, 6, row, Checker(core.Color.WHITE))
            }
        }
    }

    private fun restart() {
        clear()
        spawnField()
        time()
        logic.turn = core.Color.WHITE
    }

    private fun clear() {
        for(x in 0..7) {
            for(y in 0..7) {
                board.dispawnChecker(x, y)
            }
        }
    }

}