package core

import controller.Logic

open class Board {
    private val field = MutableList(8) { MutableList<Checker?>(8) {null} }

    private val logic = Logic()

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

    //возвращает координаты всех шашек на текущей доске
    fun allCheckers(board: Board): Map<Pair<Int, Int>, Checker?> {
        val result = mutableMapOf<Pair<Int, Int>, Checker>()
        for (x in 0..7) {
            for (y in 0..7) {
                val checker = board[x, y]
                if (checker?.isChecker(x ,y) == true) result[(x to y)] = checker
            }
        }
        return result
    }
}