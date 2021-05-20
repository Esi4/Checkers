package core

open class Board {
    private val field = List(8) { MutableList<Checker?>(8) {null} }


    operator fun set(x: Int, y:Int, checker: Checker?) {
        if(x in 0..7 && y in 0..7) {
            field[x][y] = checker
            checker?.setBoard(this)
        }
    }

    operator fun get(x:Int, y:Int) = field[x][y]

    fun allMoves(row: Int, column: Int): List<Pair<Int, Int>>{
        val list = mutableListOf<Pair<Int, Int>>()
        return if(field[row][column]?.allMoves(row, column)!!.first) {
            field[row][column]?.allMoves(row, column)!!.second
        } else list
    }
}