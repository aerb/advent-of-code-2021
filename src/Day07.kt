import kotlin.math.abs

fun main() {
  fun part1(input: List<String>): Int {
    val positions = input.first().split(",").map { it.toLong() }

    val fuel = ArrayList<Pair<Int, Long>>()
    for (i in positions.minOrNull()!! .. positions.maxOrNull()!!) {
      fuel.add(Pair(i.toInt(), positions.sumOf { value -> abs(value - i) }))
    }

    println(fuel.sortedBy { it.second })

    return fuel.minByOrNull { it.second }!!.second.toInt()
  }

  fun sumUpTo(n: Long): Long {
    var sum = 0L
    for (i in 0..abs(n)) {
      sum += i
    }
    return sum
  }

  fun part2(input: List<String>): Int {
    val positions = input.first().split(",").map { it.toLong() }

    val fuel = ArrayList<Pair<Int, Long>>()
    for (i in positions.minOrNull()!! .. positions.maxOrNull()!!) {
      fuel += i.toInt() to positions.sumOf { value ->
        sumUpTo(value - i)
      }
    }

    println(fuel.sortedBy { it.second })

    return fuel.minByOrNull { it.second }!!.second.toInt()
  }

  val input = readInput("Day07")
  println(part1(input))
  println(part2(input))
}
