package core

enum class Color {
    BLACK,
    WHITE;

    fun oppositen(): Color {
        return if(this == WHITE) BLACK
        else WHITE
    }
}