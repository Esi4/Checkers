package core

import controller.Logic

class ComputerPlayer() {

    private var logic = Logic()
    private val getTurn = logic.getTurn()


    private fun evaluation(color: Color = Color.BLACK, board: Map<Pair<Int, Int>, Checker?>): Int {
        if (color == Color.WHITE) return -evaluation(Color.BLACK, board)

        var result = 0

        for (checker in board) {
            when (checker.value?.color) {
                Color.WHITE -> {
                    result -= if (checker.value is Queen) 3 else 1
                    result += (allEat(Color.BLACK, board).size * 2)
                }
                Color.BLACK -> {
                    result += if (checker.value is Queen) 3 else 1
                    result -= (allEat(Color.BLACK, board).size * 2)
                }
            }
        }
        return result
    }

    data class EvaluatedTurn(val moves: List<Pair<Int, Int>>, val evaluation: Int)


    fun wafer(depth: Int, color: Color, board: Map<Pair<Int, Int>, Checker?>): EvaluatedTurn{
        if (victory(color.oppositen(), board)) return EvaluatedTurn(emptyList(), -depth - 10)
        else if (victory(color, board)) return EvaluatedTurn(emptyList(), depth + 10)
        if (depth <= 0) return EvaluatedTurn(emptyList(), -evaluation(getTurn, board))

        var result = EvaluatedTurn(emptyList(), -100)
        val checkList = allEat(color, board).ifEmpty { board.keys.toList() }

        for (checker in board) {
            if (!checkList.contains(checker.key)
                || checker.value?.color != color) continue
            val x = checker.key.first
            val y = checker.key.second
            val checkMoves = moveCheck(color, x, y, board).second
            for (move in checkMoves) {
                val canEat = eatCheck(color, x, y, board).first
                val newDesk = board.toMutableMap()
                moveBot(color, x, y, move.first, move.second, canEat, newDesk)
                var newMove = move
                val moveResult = mutableListOf(move)
                
                while (eatCheck(color, newMove.first, newMove.second, newDesk).first) {
                    val chainMove = eatCheck(color, newMove.first, newMove.second, newDesk).third.first()
                    moveBot(color, newMove.first, newMove.second, chainMove.first, chainMove.second, true, newDesk)
                    newMove = chainMove
                    moveResult.add(newMove)
                }
                val point = wafer(depth - 1, color.oppositen(), newDesk).evaluation
                if (point > result.evaluation) {
                    moveResult.add(0, checker.key.first to checker.key.second)
                    result = EvaluatedTurn(moveResult, point)
                }
            }
        }
        return result
    }


    fun victory(color: Color, board: Map<Pair<Int, Int>, Checker?>): Boolean {
        var winner = true
        board.forEach{
            if (it.value?.color == color.oppositen()) {
                winner = false
            }
        }
        return winner
    }

    private fun eatCheck(color: Color, x: Int, y: Int, board: Map<Pair<Int, Int>, Checker?>): Triple<Boolean, List<Pair<Int, Int>>, List<Pair<Int, Int>>>{
        if (board[x to y] is Queen) return eatQueen(color, x, y, board)
        var result = false
        val enemy = mutableListOf<Pair<Int, Int>>()
        val freeCell = mutableListOf<Pair<Int, Int>>()
        val direction = listOf(-1 to 1, -1 to -1, 1 to 1, 1 to -1)

        for ((dirX, dirY) in direction) {
            if (x + 2 * dirX in 0..7 && y + 2 * dirY in 0..7) {
                if (board[x + dirX to y + dirY] != null
                    && board[x + (2 * dirX) to y + (2 * dirY)] == null
                    && board.getValue(x to y)?.color !=
                    board[x + dirX to y + dirY]?.color) {
                    result = true
                    freeCell.add(x + (2 * dirX) to y + (2 * dirY))
                    enemy.add(x + dirX to y + dirY)
                }
            }
        }
        return Triple(result, enemy, freeCell)
    }

    private fun moveCheck(color: Color, row: Int, column: Int, board: Map<Pair<Int, Int>, Checker?>): Pair<Boolean, List<Pair<Int, Int>>> {
        if (board[row to column] is Queen) return moveQueen(color, row, column, board)
        var result = false
        val checker = board.getValue(row to column)
        val list = mutableListOf<Pair<Int, Int>>()
        val direction = if(checker?.color == Color.WHITE) {
            listOf(-1 to 1, -1 to -1)
        } else listOf(1 to -1, 1 to 1)


        if(eatCheck(color, row, column, board).first) {
            result = true
            return Pair(result, eatCheck(color, row, column, board).third)
        }

        for((dirX, dirY) in direction) {
            val x = row + dirX
            val y = column + dirY
            if (x in 0..7 && y in 0..7 && board[x to y] == null
                && board[x to y]?.color != color) {
                result = true
                list.add(x to y)
            }
        }
        return Pair(result, list)
    }

    private fun allEat(color: Color, board: Map<Pair<Int, Int>, Checker?>): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        val enemy = mutableListOf<List<Pair<Int, Int>>>()
        for (x in board) {
            val checker = x.value
            if (checker?.color == color && eatCheck(color, x.key.first, x.key.second, board).first) {
                list.add(x.key.first to x.key.second)
                enemy.add(eatCheck(color, x.key.first, x.key.second, board).second)
            }
        }
        return list
    }

    private fun moveBot(color: Color, fromX: Int, fromY: Int, inX: Int, inY: Int, mbEat: Boolean, board: MutableMap<Pair<Int, Int>, Checker?>) {
        if (board[inX to inY] is Queen) board[inX to inY] = Queen(color)
        else board[inX to inY] = board[fromX to fromY]
        if (mbEat) board.remove((fromX + inX) / 2 to (fromY + inY) / 2)
        board.remove(fromX to fromY)
    }

    private fun eatQueen(color: Color, row: Int, column: Int, board: Map<Pair<Int, Int>, Checker?>): Triple<Boolean, List<Pair<Int, Int>>, List<Pair<Int, Int>>> {
        var search = false
        val enemy = mutableListOf<Pair<Int, Int>>()
        val freeCell = mutableListOf<Pair<Int, Int>>()
        val direction = listOf(-1 to -1, 1 to 1, -1 to 1, 1 to -1)

        for((dirX, dirY) in direction) {
            var n = 0
            var x = row + dirX
            var y = column + dirY
            while (x in 0..7 && y in 0..7 && board[x to y]?.color != color) {
                if (x + dirX in 0..7 && y + dirY in 0..7) {
                    if (board[x + dirX to y + dirY]?.color != color
                        && board[x to y] != null && board[x + dirX to y + dirY] != null
                        && color != board[x to y]?.color) {
                        break
                    }
                    if (board[x + dirX to y + dirY] == null
                        && board[x to y] != null && board[x to y]?.color != color) {
                        n++
                        if (n > 1) break
                        enemy.add(x to y)
                    }
                    if (n == 1 && board[x + dirX to y + dirY] == null) {
                        freeCell.add(x + dirX to y + dirY)
                        search = true
                    }
                }
                x += dirX
                y += dirY
            }
        }
        return Triple(search, enemy, freeCell)
    }

    private fun moveQueen(color: Color, row: Int, column: Int, board: Map<Pair<Int, Int>, Checker?>): Pair<Boolean, List<Pair<Int, Int>>> {
        var result = false
        val coord = mutableListOf<Pair<Int, Int>>()
        val direction = listOf(1 to 1, -1 to -1, -1 to 1, 1 to -1)


        if(eatQueen(color, row, column, board).first) {
            result = true
            return Pair(result, eatQueen(color, row, column, board).third)
        }

        for((dirX, dirY) in direction) {
            var x = row + dirX
            var y = column + dirY
            while(x in 0..7 && y in 0..7 && board[x to y]?.color != color) {
                if(board[x to y] != null && board[x to y]?.color != color) {
                    break
                }
                result = true
                coord.add(x to y)
                x += dirX
                y += dirY
            }
        }
        return Pair(result, coord)
    }
}