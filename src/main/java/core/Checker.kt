package core


open class Checker(val color: Color) {
    private var board: Board? = null

    fun getBoard() = board

    fun setBoard(board: Board) {
        this.board = board
    }


    open fun eatIt(x: Int, y: Int): Triple<Boolean, List<Pair<Int, Int>>, List<Pair<Int, Int>>> {
        var search = false
        val enemy = mutableListOf<Pair<Int, Int>>()
        val freeCell = mutableListOf<Pair<Int, Int>>()
        val direction = listOf(-1 to 1, -1 to -1, 1 to 1, 1 to -1)
        val board = this.getBoard()!!

        for ((dirX, dirY) in direction) {
            if (x + 2 * dirX in 0..7 && y + 2 * dirY in 0..7) {
                if (isChecker(x + dirX, y + dirY) && board[x + (2 * dirX), y + (2 * dirY)] == null
                    && this.color != board[x + dirX, y + dirY]?.color) {
                        search = true
                    freeCell.add(x + 2 * dirX to y + 2 * dirY)
                    enemy.add(x + dirX to y + dirY)
                }
            }
        }
        return Triple(search, enemy, freeCell)
    }

    open fun allMoves(row: Int, column: Int): Pair<Boolean, List<Pair<Int, Int>>> {
        var result = false
        val list = mutableListOf<Pair<Int, Int>>()
        val direction = if(this.color == Color.WHITE) {
            listOf(-1 to 1, -1 to -1)
        } else listOf(1 to -1, 1 to 1)


        if(eatIt(row, column).first) {
            result = true
            return Pair(result, eatIt(row, column).third)
        }

        for((dirX, dirY) in direction) {
            val x = row + dirX
            val y = column + dirY
            if(x in 0..7 && y in 0..7 && !isChecker(x, y)
                && opposite(board?.get(x, y))) {
                result = true
                list.add(x to y)
            }
        }
        return Pair(result, list)
    }

    fun opposite(checker: Checker?) = (checker?.color ?: false) != this.color

    fun isChecker(x: Int, y: Int): Boolean {
        return board?.get(x, y) is Checker
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Checker

        if (color != other.color) return false
        if (board != other.board) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + (board?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Checker(color=$color, board=$board)"
    }
}