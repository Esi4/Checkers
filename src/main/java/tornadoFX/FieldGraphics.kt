package tornadoFX


import javafx.scene.image.ImageView
import core.*
import javafx.scene.image.Image

class FieldGraphics(private val listImage: List<List<ImageView>>): Board() {

    fun getListImage(x: Int, y: Int): Image? {
        return listImage[x][y].image
    }

    fun setListImage(x: Int, y: Int, image: Image?) {
        listImage[x][y].image = image
    }

   fun spawnChecker(q: Boolean,x : Int, y: Int, checker: Checker) {
       this[x, y] = checker
       if (!q) {
           if (checker.color == Color.WHITE) {
               setListImage(x, y, Image("file:src/main/resources/white.png"))
           } else setListImage(x, y, Image("file:src/main/resources/black.png"))
       } else {
           if (checker.color == Color.WHITE) {
               setListImage(x, y, Image("file:src/main/resources/White_Queen.png"))
           } else setListImage(x, y, Image("file:src/main/resources/Black_Queen.png"))
       }
   }

   fun dispawnChecker(x: Int, y: Int) {
       this[x, y] = null
       setListImage(x, y, null)
   }

   fun dispawnChecker(x: Int, y: Int, newX: Int, newY: Int, enemy: List<Pair<Int, Int>>) {
       val direction = when {
           y < newY && x < newX -> 1 to 1
           y > newY && x > newX -> -1 to -1
           y > newY && x < newX -> 1 to -1
           else -> -1 to 1
       }

       var dirX = x
       var dirY = y
       while(dirY != newY && dirX != newX) {
           dirY += direction.second
           dirX += direction.first
           if(dirY in 0..7 && dirX in 0..7) {
               if(enemy.contains(dirX to dirY)) {
                   dispawnChecker(dirX, dirY)
               }
           }
       }
   }

   fun loser(color: Color): Boolean {
       for(x in 0..7) {
           for(y in 0..7) {
               if(color == this[x, y]?.color && this[x, y] is Checker
                   && allMoves(x ,y).isNotEmpty()) {
                       return false
               }
           }
       }
       return true
   }

   fun move(x: Int, y: Int, newX: Int, newY: Int) {
       this[newX, newY] = this[x, y]
       this[x, y] = null
       setListImage(newX, newY, getListImage(x, y))
       setListImage(x, y, null)

       checkerToQueen(newX, newY)
   }

   private fun checkerToQueen(newX: Int, newY: Int) {
       if(this[newX, newY]!!.color == Color.BLACK && newX == 7) {
           dispawnChecker(newX, newY)
           spawnChecker(true, newX, newY, Queen(Color.BLACK))
       } else {
           if(this[newX, newY]!!.color == Color.WHITE && newX == 0) {
               dispawnChecker(newX, newY)
               spawnChecker(true, newX, newY, Queen(Color.WHITE))
           }
       }
   }
}