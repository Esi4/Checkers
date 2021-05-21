package controller

import core.*
import tornadoFX.FieldGraphics

class Logic {
    var turn = Color.WHITE
    private lateinit var board: FieldGraphics

    @JvmName("getTurn1")
    fun getTurn(): Color {
        return turn
    }

    fun setBoard(x: FieldGraphics) {
        board = x
    }

    private fun allEat(): Pair<Boolean, List<Pair<Int, Int>>> {
        var result = false
        val list = mutableListOf<Pair<Int, Int>>()

        for(x in 0..7) {
            for(y in 0..7) {
                if(board[x, y]?.color == getTurn() && board[x, y] != null
                    && board[x, y]?.eatIt(x, y)!!.first) {
                    list.add(x to y)
                    result = true
                }
            }
        }
        return Pair(result, list)
    }

    private fun allMove(): Pair<Boolean, List<Pair<Int, Int>>> {
        var result = false
        val list = mutableListOf<Pair<Int, Int>>()

        for(i in 0..7) {
            for(j in 0..7) {
                if(isChecker(i, j) && board[i, j]?.allMoves(i, j)!!.first
                    && board[i, j]!!.color == getTurn()) {
                        list.add(i to j)
                    result = true
                }
            }
        }
        return Pair(result, list)
    }

    fun moveOrEat(x: Int, y: Int, newX: Int?, newY: Int?) {

        if(board[x, y]?.color == getTurn() && isChecker(x ,y)
            && newX != null && newY != null) {
            if (x to y in allVista()) {
                if (!board[x, y]?.eatIt(x, y)!!.first) {
                    if (newX to newY in board.allMoves(x, y)) {
                        board.move(x, y, newX, newY)
                        turn = turn.oppositen()
                    }
                } else {
                    if(newX to newY in board[x, y]?.eatIt(x, y)?.third!!) {
                        board.dispawnChecker(x, y, newX, newY, board[x, y]?.eatIt(x, y)?.second!!)
                        board.move(x, y, newX, newY)
                        if(!board[newX, newY]?.eatIt(newX, newY)?.first!!) {
                            turn = turn.oppositen()
                        }
                    }
                }
            }
        } else return
    }

    private fun allVista() :List<Pair<Int, Int>> {
        return if(allEat().first) {
            allEat().second
        } else allMove().second
    }

    private fun isChecker(x: Int, y: Int): Boolean {
        return board[x, y] is Checker
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Logic

        if (turn != other.turn) return false
        if (board != other.board) return false

        return true
    }

    override fun hashCode(): Int {
        var result = turn.hashCode()
        result = 31 * result + board.hashCode()
        return result
    }
}