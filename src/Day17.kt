import kotlin.math.max
import kotlin.math.min

fun main() {


  fun part1(input: List<String>): Int {

    // target area: x=20..30, y=-10..-5
    val (xRange: IntRange, yRange: IntRange) =
      input.single().removePrefix("target area: ").split(", ")
        .map { it.removePrefix("x=").removePrefix("y=") }
        .map { it.split("..").map(String::toInt).let { (a, b) -> IntRange(a, b) } }

    fun launch(initialDx: Int, initialDy: Int): Pair<Boolean, Int> {
      var maxY = 0
      var x = 0
      var y = 0
      var dX = initialDx
      var dY = initialDy
      while (true) {
        maxY = max(maxY, y)
        when {
          x in xRange && y in yRange -> return true to maxY
          dX > 0 && x > xRange.last -> return false to maxY
          dX < 0 && x < xRange.first -> return false to maxY
          dX == 0 && x !in xRange -> return false to maxY
          y < yRange.first -> return false to maxY
        }

        x += dX
        y += dY
        dX = run {
          if (dX > 0) (dX - 1).coerceAtLeast(0)
          else (dX + 1).coerceAtMost(0)
        }
        dY -= 1
      }
    }

    val maxX = xRange.last
    val maxY = -yRange.first

    val hits = ArrayList<Int>()
    for (x in 0..maxX) {
      for (y in -maxY..maxY) {
        val (hit, maxY) = launch(x, y)
        if (hit) {
          hits += maxY
        }
      }
    }
    println(hits)

    return hits.size
  }

  fun part2(input: List<String>): Int {
    return input.size
  }

  val input = readInput("Day17")
  println(part1(input))
//  println(part2(input))
}
