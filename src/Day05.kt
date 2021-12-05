fun main() {
  fun part1(input: List<String>): Int {
    fun absRange(i0: Int, i1: Int): IntRange {
      return if (i0 > i1) i1 .. i0
      else i0 .. i1
    }

    data class Point(val x: Int, val y: Int)
    data class Line(val p0: Point, val p1: Point)

    val lines = input.map { line ->
      val points = line.split(" -> ").map { part ->
        val (x, y) = part.split(",")
        Point(x.toInt(), y.toInt())
      }
      Line(points[0], points[1])
    }

    val points = lines.flatMap { listOf(it.p0, it.p1) }
    val maxX = points.maxOf { it.x } + 1
    val maxY = points.maxOf { it.y } + 1

    val grid = MutableList(maxY) { MutableList(maxX) { 0 } }

    for (line in lines) {
      if (line.p0.x == line.p1.x) {
        for (y in absRange(line.p0.y, line.p1.y)) {
          grid[y][line.p0.x]++
        }
      }

      if (line.p0.y == line.p1.y) {
        for (x in absRange(line.p0.x, line.p1.x)) {
          grid[line.p0.y][x]++
        }
      }
    }

//    grid.forEach { row -> println(row.joinToString(separator = "")) }

    val count = grid.sumBy { row -> row.sumBy { if (it >= 2) 1 else 0 } }
    return (count)
  }

  fun part2(input: List<String>): Int {
    data class Point(val x: Int, val y: Int)
    data class Line(val p0: Point, val p1: Point)

    fun absRange(i0: Int, i1: Int): IntRange {
      return if (i0 > i1) i1..i0
      else i0..i1
    }

    fun lin(a: Int, b: Int): List<Int> {
      return if (a < b) {
        (a..b).toList()
      } else {
        (a downTo b).toList()
      }
    }

    val lines = input.map { line ->
      val points = line.split(" -> ").map { part ->
        val (x, y) = part.split(",")
        Point(x.toInt(), y.toInt())
      }
      Line(points[0], points[1])
    }

    val points = lines.flatMap { listOf(it.p0, it.p1) }
    val maxX = points.maxOf { it.x } + 1
    val maxY = points.maxOf { it.y } + 1

    val grid = MutableList(maxY) { MutableList(maxX) { 0 } }

    for (line in lines) {
      if (line.p0.x == line.p1.x) {
        for (y in absRange(line.p0.y, line.p1.y)) {
          grid[y][line.p0.x]++
        }
      } else if (line.p0.y == line.p1.y) {
        for (x in absRange(line.p0.x, line.p1.x)) {
          grid[line.p0.y][x]++
        }
      } else {
        val zip = lin(line.p0.x, line.p1.x).zip(lin(line.p0.y, line.p1.y))

        for ((x,y) in zip) {
          grid[y][x]++
        }
      }
    }

//    grid.forEach { row -> println(row.joinToString(separator = "")) }

    val count = grid.sumBy { row -> row.sumBy { if (it >= 2) 1 else 0 } }
    return (count)
  }

  val input = readInput("Day05")
  println(part1(input))
  println(part2(input))
}
