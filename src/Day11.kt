fun main() {


  fun part1(input: List<String>): Long {
    val grid: MutableList<MutableList<Int>> =
      input.map { it.map { it.toString().toInt() }.toMutableList() }.toMutableList()

    val numOfCells = grid.sumOf { it.size }

    var total = 0L
    repeat(1000) { step ->
      for (y in 0 until grid.size) {
        for (x in 0 until grid.first().size) {
          grid[y][x] ++
        }
      }
      val flashed = HashSet<Pair<Int, Int>>()
      fun flash(x: Int, y: Int) {
        val p = x to y
        if (p in flashed) return

        println("flash $x $y")
        flashed += x to y
        grid[y][x] = 0


        for(dY in -1 .. +1) {
          for(dX in -1 .. +1) {
            if (dX != 0 || dY != 0) {
              val nY = y + dY
              val nX = x + dX
              if (nY >= 0 && nY < grid.size) {
                val row = grid[nY]
                if (nX >= 0 && nX < row.size) {
                  if (nX to nY !in flashed) {
                    row[nX] ++
                    if (row[nX] > 9) {
                      flash(nX, nY)
                    }
                  }
                }
              }
            }
          }
        }

      }

      for (y in 0 until grid.size) {
        for (x in 0 until grid.first().size) {
          if (grid[y][x] > 9) {
            flash(x, y)
          }
        }
      }

      if (flashed.size == numOfCells) {
        println("FOUND! $step")
        return step.toLong() + 1
      }

      total += flashed.size
    }

    TODO()
  }

  fun part2(input: List<String>): Int {
    return input.size
  }

  val input = readInput("Day11" +
    "")
  println(part1(input))
//  println(part2(input))
}
