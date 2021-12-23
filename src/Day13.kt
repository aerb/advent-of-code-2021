private typealias Grid = MutableList<MutableList<Boolean>>

fun main() {
  data class Point(val x: Int, val y: Int)
  data class Fold(val axis: String, val value: Int)

  fun part1(input: List<String>): Int {
    val points = input.takeWhile { it.isNotBlank() }.map { line ->
      val (x, y) = line.split(",").map { it.toInt() }
      Point(x, y)
    }

    val folds = input.dropWhile { it.isNotBlank() }.drop(1).map { line ->
      val (axis, value) = line.split(" ").last().split("=")
      Fold(axis, value.toInt())
    }

    val maxX = points.maxOf { it.x }
    val maxY = points.maxOf { it.y }

    val grid = MutableList(maxY + 1) { MutableList(maxX + 1) { false } }
    points.forEach { p ->
      grid[p.y][p.x] = true
    }

    fun Grid.printGrid() {
      forEach { row ->
        println(row.joinToString(separator = "") { if (it) "#" else "." } )
      }
      println()
    }

    fun assertSize(top: Grid, bottom: Grid) {
      require(top.size == bottom.size)
      require(top.zip(bottom).all { (a, b) -> a.size == b.size })
    }

    fun Grid.foldV(y: Int): Grid {
      require(size / 2 == y)
      require(this[y].all { !it }) { this[y] }

      val top = take(y).toMutableList()
      val bottom = drop(y + 1).padRight(y) { MutableList(first().size) { false } }

      assertSize(top, bottom)

      bottom.asReversed().forEachIndexed { rIndex, row ->
        row.forEachIndexed { cIndex, b ->
          top[rIndex][cIndex] = top[rIndex][cIndex].or(b)
        }
      }
      return top
    }

    fun Grid.foldH(x: Int): Grid {
      require(first().size % 2 == 1) { "$x ${first().size}" }
      require(all { it.size / 2 == x }) { "${first().size} $x"}
      require(all { !it[x] })

      val left = map { it.take(x).toMutableList() }.toMutableList()
      val right = map { it.drop(x + 1).padRight(x) { false } }.toMutableList()

      assertSize(left, right)

      right.forEachIndexed { rIndex, row ->
        row.asReversed().forEachIndexed { cIndex, b ->
          left[rIndex][cIndex] = left[rIndex][cIndex].or(b)
        }
      }
      return left
    }


    var curr = grid
    folds.forEachIndexed { i, fold ->
      println(fold)
      println(i)
      curr.printGrid()

      curr = when(fold.axis) {
        "y" -> curr.foldV(fold.value)
        "x" -> curr.foldH(fold.value)
        else -> TODO()
      }

    }

    curr.printGrid()

    return curr.sumOf { row -> row.count { it } }
  }

  fun part2(input: List<String>): Int {
    return input.size
  }

  val input = readInput("Day13")
  println(part1(input))
//  println(part2(input))
}

private fun <T> List<T>.padRight(y: Int, item: () -> T): MutableList<T> {
  val l = toMutableList()
  while (l.size < y) l += item()
  return l
}
