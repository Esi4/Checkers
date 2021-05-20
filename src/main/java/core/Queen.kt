package core

class Queen(color: Color): Checker(color) {

    override fun eatIt(row: Int, column: Int): Triple<Boolean, List<Pair<Int, Int>>, List<Pair<Int, Int>>> {
        var search = false
        val enemy = mutableListOf<Pair<Int, Int>>()
        val freeCell = mutableListOf<Pair<Int, Int>>()
        val board = this.getBoard()!!
        val direction = listOf(-1 to -1, 1 to 1, -1 to 1, 1 to -1)

        for((dirX, dirY) in direction) {
            var n = 0
            var x = row + dirX
            var y = column + dirY
            while (x in 0..7 && y in 0..7 && opposite(board[x, y])) {
                if (x + dirX in 0..7 && y + dirY in 0..7) {
                    if (board[x + dirX, y + dirY]?.color != this.color
                        && isChecker(x + dirX, y + dirY) && isChecker(x, y) && this.color != board[x, y]?.color) {
                        break
                    }
                    if (board[x + dirX, y + dirY] == null
                        && isChecker(x, y) && board[x, y]?.color != this.color) {
                        n++
                        if (n > 1) break
                        enemy.add(x to y)
                    }
                    if (n == 1 && !isChecker(x + dirX, y + dirY)) {
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

    override fun allMoves(row: Int, column: Int): Pair<Boolean, List<Pair<Int, Int>>> {
        var result = false
        val coord = mutableListOf<Pair<Int, Int>>()
        val direction = listOf(1 to 1, -1 to -1, -1 to 1, 1 to -1)
        val board = this.getBoard()


        if(eatIt(row, column).first) {
            result = true
            return Pair(result, eatIt(row, column).third)
        }

        for((dirX, dirY) in direction) {
            var x = row + dirX
            var y = column + dirY
            while(x in 0..7 && y in 0..7 && opposite(board?.get(x, y))) {
                if(isChecker(x, y) && board?.get(x, y)?.color != board?.get(x, y)?.color) {
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