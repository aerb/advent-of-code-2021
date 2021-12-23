import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.min

fun main() {

  fun part1(input: List<String>): Long {
    data class Point(val x: Int, val y: Int)

    val grid = input.map { row -> row.map { it.digitToInt() } }

    fun Point.value(): Int = grid[y][x]

    fun Point.move(dX: Int, dY: Int): Point? {
      val nX = x + dX
      val nY = y + dY
      return if (
        nX < 0 || nX >= grid.first().size ||
        nY < 0 || nY >= grid.size
      ) {
        null
      } else {
        Point(nX, nY)
      }
    }

    val start = Point(0, 0)
    val end = Point(grid.lastIndex, grid.last().lastIndex)

    val visited = HashSet<Point>()
    val minCost = hashMapOf(start to 0L)

    fun Point.cost(): Long = minCost.getValue(this)

    fun updateCost(p: Point, cost: Long) {
      val currCost = minCost[p]
      if (currCost == null) {
        minCost[p] = cost
      } else {
        minCost[p] = min(cost, currCost)
      }
    }

    fun visit(now: Point) {
      visited += now

      val currCost = minCost[now] ?: error("No cost $now")

      val moves = listOfNotNull(
        now.move(+1, +0),
        now.move(-1, +0),
        now.move(+0, +1),
        now.move(+0, -1),
      ).map { point ->
        point to point.value()
      }

      for ((point, cost) in moves) {
        if (point !in visited) {
          updateCost(point, currCost + cost)
        }
      }
    }

    while (true) {
      val next = minCost.filterKeys { it !in visited }.minByOrNull { it.value }?.key!!
      if (next == end) break
      else visit(next)
    }

    for (y in grid.indices) {
      grid[y].indices.map { x -> Point(x, y).cost() }.joinToString("") { it.toString().padStart(4)}
        .also { println(it) }
    }

    return minCost[end]!!
  }

  fun part2(input: List<String>): Long {
    data class Point(val x: Int, val y: Int)

    val template = input.map { row -> row.map { it.digitToInt() } }


    fun inc(grid: List<List<Int>>, by: Int): List<List<Int>> {
      return grid.map { row ->
        row.map {
          var next = it
          repeat(by) {
            next ++
            if (next > 9) next = 1
          }
          next
        }
      }
    }

    val tempHeight = template.size
    val tempWidth = template.first().size
    val grid = MutableList(tempHeight * 5) { MutableList(tempWidth * 5) { -1 } }

    println()
    println(grid.size)
    println(grid.first().size)

    for(row in 0 until 5) {
      for(col in 0 until 5) {
        val innerGrid = inc(template, row + col)
        for (iR in innerGrid.indices) {
          for (iC in innerGrid[iR].indices) {
            val value = innerGrid[iR][iC]
            grid[row * tempHeight + iR][col * tempWidth + iC] = value
          }
        }

      }
    }

    fun Point.value(): Int = grid[y][x]

    fun print(grid: List<List<Int>>) {
      for (y in grid.indices) {
        grid[y].indices.map { x -> Point(x, y).value() }.joinToString("") { it.toString().padStart(1)}
          .also { println(it) }
      }
    }


    val count = grid.sumOf { it.size }
//    print(grid)

    fun Point.move(dX: Int, dY: Int): Point? {
      val nX = x + dX
      val nY = y + dY
      return if (
        nX < 0 || nX >= grid.first().size ||
        nY < 0 || nY >= grid.size
      ) {
        null
      } else {
        Point(nX, nY)
      }
    }

    val start = Point(0, 0)
    val end = Point(grid.lastIndex, grid.last().lastIndex)

    val visited = HashSet<Point>()
    val minCost = hashMapOf(start to 0L)

    fun Point.cost(): Long = minCost.getValue(this)

    val priorityQ = PriorityQueue<Point>(compareBy { it.cost() })
    priorityQ.add(start)

    fun updateCost(p: Point, cost: Long): Boolean {
      val currCost = minCost[p]
      if (currCost == null) {
        minCost[p] = cost
        return true
      } else {
        val next = min(cost, currCost)
        if (next != currCost) {
          minCost[p] = next
          return true
        } else {
          return false
        }
      }
    }

    fun visit(now: Point) {
      visited += now

      val currCost = minCost[now] ?: error("No cost $now")

      val moves = listOfNotNull(
        now.move(+1, +0),
        now.move(-1, +0),
        now.move(+0, +1),
        now.move(+0, -1),
      ).map { point ->
        point to point.value()
      }

      for ((point, cost) in moves) {
        if (point !in visited) {
          if (updateCost(point, currCost + cost)) {
            priorityQ.remove(point)
            priorityQ.add(point)
          }

        }
      }
    }

    var progress = 0
    while (true) {

      val next = priorityQ.poll()
      if (next == end) break
      else visit(next)

      val curr = (visited.size.toFloat() * 100 / count).toInt()
      if (curr > progress) {
        progress = curr
        println(LocalDateTime.now().toString() + " " + progress)
      }
    }

    return minCost[end]!!
  }

  val input = readInput("Day15")
//  println(part1(input))
  println(part2(input))
}
