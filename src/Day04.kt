@file:Suppress("PackageDirectoryMismatch")
package day4

import noSolution
import readInput

private data class Cell(val num: Int, var marked: Boolean = false)
private typealias Grid = MutableList<MutableList<Cell>>

fun main() {

  fun part1(input: List<String>): Int {
    fun calc(num: Int, board: MutableList<MutableList<Cell>>): Int {
      val unmarked = board.sumOf { row ->
        row.sumOf { cell ->
          if (!cell.marked) cell.num
          else 0
        }
      }
      return unmarked * num
    }

    fun eval(num: Int, board: MutableList<MutableList<Cell>>): Boolean {
      if (board.any { row -> row.all { it.marked } }) return true
      for (i in board.indices) {
        if (board.all { row -> row[i].marked }) return true
      }
      return false
    }

    fun mark(num: Int, board: MutableList<MutableList<Cell>>): Boolean {
      for (row in board) {
        for (cell in row) {
          if (num == cell.num) {
            cell.marked = true
            return true
          }
        }
      }
      return false
    }

    val nums = input.first().split(",").filter { it.isNotBlank() }.map { it.trim().toInt() }

    val boards = ArrayList<Grid>()

    var board: Grid? = null
    for (line in input.drop(1)) {
      if (line.isBlank()) {
        if (board != null) {
          require(board.size == 5)
          boards += board
        }
        board = ArrayList()
      } else {
        val row = line.split(" ").filter { it.isNotBlank() }.map { Cell(it.toInt()) }.toMutableList()
        require(row.size == 5)
        board!!.add(row)
      }
    }

    for (num in nums) {
      boards.forEach { board ->
        if (mark(num, board)) {
          if (eval(num, board)) {
            return calc(num, board)
          }
        }
      }
    }
    noSolution()
  }

  fun part2(lines: List<String>): Int {
    fun calc(num: Int, board: MutableList<MutableList<Cell>>): Int {
      val unmarked = board.sumOf { row ->
        row.sumOf { cell ->
          if (!cell.marked) cell.num
          else 0
        }
      }
      return unmarked * num
    }

    fun eval(num: Int, board: MutableList<MutableList<Cell>>): Boolean {
      if (board.any { row -> row.all { it.marked } }) return true
      for (i in board.indices) {
        if (board.all { row -> row[i].marked }) return true
      }
      return false
    }

    fun mark(num: Int, board: MutableList<MutableList<Cell>>): Boolean {
      for (row in board) {
        for (cell in row) {
          if (num == cell.num) {
            cell.marked = true
            return true
          }
        }
      }
      return false
    }

    val nums = lines.first().split(",").filter { it.isNotBlank() }.map { it.trim().toInt() }

    val boards = ArrayList<Grid>()

    var board: Grid? = null
    for (line in lines.drop(1)) {
      if (line.isBlank()) {
        if (board != null) {
          require(board!!.size == 5)
          boards += board!!
        }
        board = ArrayList()
      } else {
        val row = line.split(" ").filter { it.isNotBlank() }.map { Cell(it.toInt()) }.toMutableList()
        require(row.size == 5)
        board!!.add(row)
      }
    }


    val won = HashMap<Grid, Boolean>()

    for (num in nums) {
      for (board in boards) {
        if (won[board] == true) continue

        if (mark(num, board)) {
          if (eval(num, board)) {
            if (boards.filter { it !in won }.size == 1) {
              return calc(num, board)
            }
            won[board] = true

          }
        }
      }
    }
    noSolution()
  }

  val input = readInput("Day04")
  println(part1(input))
  println(part2(input))
}
