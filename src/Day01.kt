fun main() {
  fun part1(input: List<String>): Int {
    var count = 0
    var last: Int? = null
    for (line in input) {
      if (last != null) {
        if (line.trim().toInt() > last) {
          count++
        }
      }
      last = line.trim().toInt()
    }
    return count
  }

  fun part2(input: List<String>): Int {
    val nums = input.map { it.toInt() }
    var count = 0
    var lastWindow: Int? = null
    for (i in nums.indices) {
      if (i - 2 >= 0) {
        val window = nums[i] + nums[i - 1] + nums[i - 2]
        if (lastWindow != null) {
          if (window > lastWindow) {
            count ++
          }
        }
        lastWindow = window
      }
    }
    return count
  }

  val input = readInput("Day01")
  println(part1(input))
  println(part2(input))
}
