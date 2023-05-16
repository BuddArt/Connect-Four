package connectfour
import kotlin.system.exitProcess

const val mark1 = "║o"
const val mark2 = "║*"
var row = 0
var column = 0
var trueOrFalse = true
var score1 = 0
var score2 = 0

class Game {
    private var checkRepeatGame: Boolean = true
    private val regRepeatGame = Regex("\\d\\d?\\d?")
    private val regEx = Regex("\\d\\d?\\d?\\d?\\d?\\d?\\d?\\d?")
    private var x = 0
    private var name = ""
    private var name1 = ""
    private var name2 = ""
    private var list = MutableList(row) { MutableList(column) { " " } }
    private var turn = ""
    private var repeatGame = ""
    init {
        println("Connect Four")
        println("First player's name:")
        name1 = readln()
        println("Second player's name:")
        name2 = readln()

        setBoard()

        list = MutableList(row) { MutableList(column) { "║ " } }

        do {
            println("Do you want to play single or multiple games?")
            println("For a single game, input 1 or press Enter")
            println("Input a number of games:")
            repeatGame = readln()
            checkRepeatGame = if (regRepeatGame.matches(repeatGame) && repeatGame.toInt() > 0 || repeatGame.isEmpty()) {
                false
            } else {
                println("Invalid input")
                true
            }
        } while (checkRepeatGame)
        if (repeatGame == "1" || repeatGame.isEmpty()) {
            singleGame()
        } else multipleGame()
    }
    private fun singleGame() {
        println("$name1 VS $name2")
        println("$row X $column board")
        println("Single game")
        board(list, column)
        while (trueOrFalse) {
            if (x == 0) {
                name = name1
                turnResult(1, mark1)
            }
            if (x == 1) {
                name = name2
                turnResult(0, mark2)
            }
        }
        println("Game Over!")
    }
    private fun multipleGame() {
        var countTime = 1
        println("$name1 VS $name2")
        println("$row X $column board")
        println("Total $repeatGame games")
        repeat(repeatGame.toInt()) {
            println("Game #$countTime")
            board(list, column)
            while (trueOrFalse) {
                if (x == 0) {
                    name = name1
                    turnResult(1, mark1)
                }
                if (x == 1) {
                    name = name2
                    turnResult(0, mark2)
                }
            }
            println("Score")
            println("$name1: $score1 $name2: $score2")
            x = if (x == 0) 1 else 0
            trueOrFalse = true
            list = MutableList(row) { MutableList(column) { "║ " } }
            countTime++
        }
        println("Game Over!")
    }

    private fun turnResult(y: Int, mark: String) {
        println("$name's turn:")
        turn = readln()
        if (turn == "end") {
            println("Game over!")
            exitProcess(-1)
        } else if (!regEx.matches(turn)) {
            println("Incorrect column number")
        } else if (turn.toInt() !in 1..column && regEx.matches(turn)) {
            println("The column number is out of range (1 - $column)")
        } else if (turn.toInt() in 1..column && regEx.matches(turn)) {
            if (list[0][turn.toInt() - 1] !== "║ ") {
                println("Column $turn is full")
            }
            for (z in row - 1 downTo 0) {
                if (list[z][turn.toInt() - 1] == "║ ") {
                    list[z][turn.toInt() - 1] = mark
                    board(list, column)
                    dRight(list, row, column, name, x)
                    dLeft(list, row, column, name, x)
                    horizon(list, row, column, name, x)
                    vertical(list, row, column, name, x)
                    draw(list, row, column)
                    if (trueOrFalse) x = y
                    break
                }
            }
        }
    }
}

fun setBoard() {
    var x = 0
    val reg = Regex("\t?X\t?")
    val regValid = Regex("\\d\\d? ?X ?\\d\\d?")
    while (x == 0) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        var d = "6X7"
        val board = readln().replace(" ", "").replace("\t", "")
        if (board.isNotEmpty()) {
            d = board.uppercase().trim()
        }
        if (regValid.matches(d)) {
            val spl = d.split(reg)
            row = spl[0].toInt()
            column = spl[1].toInt()
            if (row in 5..9 && column in 5..9) {
                x++
            } else if (row !in 5..9) {
                println("Board rows should be from 5 to 9")
            } else if (column !in 5..9) {
                println("Board columns should be from 5 to 9")
            }
        } else println("Invalid input")
    }
}

fun board(mutList: MutableList<MutableList<String>>, col: Int) {
    for (topNum in 1..col) print(" $topNum")
    println()
    for (i in mutList.indices) {
        println("${mutList[i].joinToString("")}║")
    }
    print("╚")
    for (downWall in 1 until col) print("═╩")
    println("═╝")
}

fun draw(mutList: MutableList<MutableList<String>>, rs: Int, col: Int) {
    var checkSpace = 0
    for (i in rs - 1 downTo 0) {
        for (j in 0 until col) {
            if (mutList[i][j] == "║ ") checkSpace = 1
        }
    }
    if (checkSpace == 0) {
        println("It is a draw")
        score1++
        score2++
        trueOrFalse = false
        return
    }
}

fun vertical(mutList: MutableList<MutableList<String>>, rs: Int, col: Int, nm: String, x: Int) {
    for (i in rs - 1 downTo 3) {
        for (j in 0 until col) {
            if (mutList[i][j] == mutList[i - 1][j] &&
                mutList[i - 1][j] == mutList[i - 2][j] &&
                mutList[i - 2][j] == mutList[i - 3][j] && mutList[i][j] != "║ ") {
                println("Player $nm won")
                if (x == 0) score1 += 2 else if (x == 1) score2 += 2
                trueOrFalse = false
                return
            }
        }
    }
}

fun horizon(mutList: MutableList<MutableList<String>>, rs: Int, col: Int, nm: String, x: Int) {
    for (i in 0 until rs) {
        for (j in 0 until col - 3) {
            if (mutList[i][j] == mutList[i][j + 1] &&
                mutList[i][j + 1] == mutList[i][j + 2] &&
                mutList[i][j + 2] == mutList[i][j + 3] && mutList[i][j] != "║ ") {
                println("Player $nm won")
                if (x == 0) score1 += 2 else if (x == 1) score2 += 2
                trueOrFalse = false
                return
            }
        }
    }
}

fun dLeft(mutList: MutableList<MutableList<String>>, rs: Int, col: Int, nm: String, x: Int) {
    for (i in rs - 1 downTo 3) {
        for (j in col - 1 downTo 3) {
            if (mutList[i][j] == mutList[i - 1][j - 1] &&
                mutList[i - 1][j - 1] == mutList[i - 2][j - 2] &&
                mutList[i - 2][j - 2] == mutList[i - 3][j - 3] && mutList[i][j] != "║ ") {
                println("Player $nm won")
                if (x == 0) score1 += 2 else if (x == 1) score2 += 2
                trueOrFalse = false
                return
            }
        }
    }
}

fun dRight(mutList: MutableList<MutableList<String>>, rs: Int, col: Int, nm: String, x: Int) {
    for (i in rs - 1 downTo 3) {
        for (j in 0 until col - 3) {
            if (mutList[i][j] == mutList[i - 1][j + 1] &&
                mutList[i - 1][j + 1] == mutList[i - 2][j + 2] &&
                mutList[i - 2][j + 2] == mutList[i - 3][j + 3] && mutList[i][j] != "║ "
            ) { println("Player $nm won")
                if (x == 0) score1 += 2 else if (x == 1) score2 += 2
                trueOrFalse = false
                return
            }
        }
    }
}

fun main() {
    Game()
}