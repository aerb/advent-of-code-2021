fun main() {
  fun part1(input: List<String>): Int {
    val fishes = input.first().split(",").map { it.toInt() }.toMutableList()
    repeat(80) {
      println(fishes)
      var newFish = 0
      for (i in fishes.indices) {
        fishes[i] = fishes[i] - 1
        if (fishes[i] < 0) {
          fishes[i] = 6
          newFish++
        }
      }
      fishes += MutableList(newFish) { 8 }
    }
    return fishes.size
  }

  fun part2(input: List<String>): Long {
    data class Generation(val count: Long, var days: Int)

    var generations: MutableList<Generation> =
      input.first().split(",").groupBy { it.toInt() }.map { Generation(it.value.size.toLong(), it.key) }
        .toMutableList()

    repeat(256) {
      var nextGen: Generation? = null
      for (generation in generations) {
        generation.days --
        if (generation.days < 0) {
          generation.days = 6
          require(nextGen == null)
          nextGen = Generation(generation.count, 8)
        }
      }

      if (nextGen != null) {
        generations += nextGen
      }

      generations = generations.groupBy { it.days }
        .map {
          Generation(it.value.sumOf { it.count }, it.key)
        }.toMutableList()

      println(generations)
      println(generations.sumOf { it.count })
    }
    return generations.sumOf { it.count }
  }

  val input = readInput("Day06")
  //println(part1(input))
  println(part2(input))

}
