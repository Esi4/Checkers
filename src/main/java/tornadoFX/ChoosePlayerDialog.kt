package tornadoFX

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

class ChoosePlayerDialog : Dialog<ButtonType>() {
    private val whitePlayer = SimpleStringProperty()
    val whiteComputer: Boolean get() = whitePlayer.value == "Computer"

    private val blackPlayer = SimpleStringProperty()
    val blackComputer: Boolean get() = blackPlayer.value == "Computer"

    init {
        title = "Checkers"
        with (dialogPane) {
            headerText = "Checkers"
            buttonTypes.add(ButtonType("Start Game", ButtonBar.ButtonData.OK_DONE))
            buttonTypes.add(ButtonType("Quit", ButtonBar.ButtonData.CANCEL_CLOSE))
        }
    }
}