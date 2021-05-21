package tornadoFX

import javafx.scene.control.ButtonBar
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch


class CheckersApp : App(CheckersView::class) {

    private var whiteHuman = true

    private var blackHuman = true

    override fun start(stage: Stage) {
        val dialog = ChoosePlayerDialog()
        val result = dialog.showAndWait()
        if (result.isPresent && result.get().buttonData == ButtonBar.ButtonData.OK_DONE) {
            whiteHuman = !dialog.whiteComputer
            blackHuman = !dialog.blackComputer
            super.start(stage)
        }
    }
}

fun main(args: Array<String>) {
    launch<CheckersApp>(args)
}