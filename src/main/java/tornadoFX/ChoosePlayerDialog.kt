package tornadoFX

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.layout.Priority
import tornadofx.*
import java.rmi.Naming.bind

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
            content = hbox {
                vbox {
                    label("White")
                    togglegroup {
                        bind(whitePlayer)
                        radiobutton("Human") {
                            isSelected = true

                        }
                        radiobutton("Computer") {

                        }
                    }
                }
                spacer(Priority.ALWAYS)
                vbox {
                    label("Black")
                    togglegroup {
                        bind(blackPlayer)
                        radiobutton("Human")
                        radiobutton("Computer") {
                            isSelected = true
                        }
                    }
                }
            }
        }
    }
}