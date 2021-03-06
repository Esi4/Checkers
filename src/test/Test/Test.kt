package tests

import controller.Logic
import core.*
import kotlin.test.assertEquals
import org.junit.Test
import tornadoFX.CheckersView
import tornadoFX.FieldGraphics
import kotlin.test.assertTrue

class CheckersTests {
    var board = Board()
    var logic = Logic()

    @Test
    fun eatItTest() {
        board[0, 0] = Checker(Color.BLACK)
        board[1, 1] = Checker(Color.WHITE)
        board[3, 3] = Checker(Color.WHITE)

        assertEquals(true, board[0, 0]!!.eatIt(0, 0).first)
        assertEquals(listOf(1 to 1), board[0, 0]!!.eatIt(0, 0).second)
        assertEquals(false, board[3, 3]!!.eatIt(3, 3).first)
    }

    @Test
    fun eatItQueenTest() {
        board[0, 0] = Queen(Color.BLACK)
        board[3, 4] = Checker(Color.BLACK)
        board[4, 4] = Checker(Color.WHITE)

        assertEquals(true, board[0, 0]!!.eatIt(0, 0).first)
        assertEquals(false, board[3, 4]!!.eatIt(3, 4).first)

    }

    @Test
    fun isCheckerTest() {
        board[0, 0] = Queen(Color.BLACK)

        assertEquals(true, board[0, 0]?.isChecker(0, 0))
        assertEquals(null, board[1, 0]?.isChecker(1, 0))
    }

    @Test
    fun allMoveTest() {
        board[4, 2] = Checker(Color.BLACK)
        board[5, 3] = Checker(Color.WHITE)
        board[1, 1] = Checker(Color.BLACK)
        board[7, 7] = Checker(Color.BLACK)
        board[5 ,0] = Queen(Color.BLACK)

        assertEquals(listOf(2 to 0, 2 to 2), board[1, 1]?.allMoves(1, 1)?.second)
        assertEquals(emptyList(), board[7, 7]?.allMoves(7, 7)?.second)
        assertEquals(listOf(6 to 1, 7 to 2, 4 to 1, 3 to 2, 2 to 3, 1 to 4, 0 to 5), board[5, 0]?.allMoves(5, 0)?.second)
    }

    //тест, если шашка-бот должна есть - она ест
    @Test
    fun compTest() {
        val field = Board()
        val cmp = ComputerPlayer()
        var count = 0

        field[1, 2] = Checker(Color.BLACK)
        field[2, 3] = Checker(Color.WHITE)

        val lists = cmp.wafer(2, Color.WHITE, field.allCheckers(field)).moves
        while (count < lists.size - 1) {
            field[lists[count + 1].first, lists[count + 1].second] = field[lists[count].first, lists[count].second]
            field[lists[count].first, lists[count].second] = null
            count++
        }
        val result = Pair(0, 1)
        assertTrue(field.allCheckers(field).containsKey(result))
    }
}