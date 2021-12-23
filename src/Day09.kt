fun main() {
  fun part1(input: List<String>): Long {
    val grid: List<List<Int>> = input.map { it.toList().map { it.toString().toInt() } }
    val h = grid.size
    val w = grid.first().size

    val risk = ArrayList<Long>()
    for (y in 0 until h) {
      for (x in 0 until w) {
        val cell = grid[y][x]
        fun lowerThan(dX: Int, dY: Int): Boolean {
          val n = grid.getOrNull(y + dY)?.getOrNull(x + dX) ?: return true
          return cell < n
        }
        val isLow =
          lowerThan(-1, 0) &&
          lowerThan(+1, 0) &&
          lowerThan(0, -1) &&
          lowerThan(0, +1)
        if (isLow) {
          risk += cell.toLong() + 1
        }
      }
    }

    println(risk)
    return risk.sum()
  }

  fun part2(input: List<String>): Int {
    val grid: List<List<Int>> = input.map { it.toList().map { it.toString().toInt() } }
    val h = grid.size
    val w = grid.first().size

    val risk = ArrayList<Pair<Int, Int>>()
    for (y in 0 until h) {
      for (x in 0 until w) {
        val cell = grid[y][x]
        fun lowerThan(dX: Int, dY: Int): Boolean {
          val n = grid.getOrNull(y + dY)?.getOrNull(x + dX) ?: return true
          return cell < n
        }
        val isLow =
          lowerThan(-1, 0) &&
            lowerThan(+1, 0) &&
            lowerThan(0, -1) &&
            lowerThan(0, +1)
        if (isLow) {
          risk += x to y
        }
      }
    }

    fun explore(point: Pair<Int, Int>, visited: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> {
      if (point in visited) {
        return emptySet()
      }

      val value = grid.getOrNull(point.second)?.getOrNull(point.first)
      if (value == null || value == 9) {
        return emptySet()
      }
      fun inc(dX: Int, dY: Int): Pair<Int, Int> = point.first + dX to point.second + dY

      var seen = visited + point
      seen = seen + explore(inc(+1, 0), seen)
      seen = seen + explore(inc(-1, 0), seen)
      seen = seen + explore(inc(0, +1), seen)
      seen = seen + explore(inc(0, -1), seen)
      return seen
    }

    val sizes = risk.map { explore(it, emptySet()).size }.sorted()
    println(sizes)
    return  sizes.takeLast(3).reduce { acc, value -> acc * value }
  }

  val input = readInput("Day09")
//  println(part1(input))
  println(part2(input))
}


