fun main() {
  data class Move(val dir: String, val num: Int)

  fun part1(input: List<String>): Int {
    val moves = input.map {
      val (dir, num) = it.split(" ")
      Move(dir, num.toInt())
    }

    var x = 0
    var y = 0
    for (it in moves) {
      when (it.dir) {
        "forward" -> { x += it.num }
        "down" -> { y += it.num }
        "up" -> { y -= it.num }
        else -> error("What is this ${it.dir}")
      }
    }

    return x * y
  }

  fun part2(input: List<String>): Int {
    val moves = input.map {
      val (dir, num) = it.split(" ")
      Move(dir, num.toInt())
    }

    var x = 0
    var y = 0
    var aim = 0
    for (it in moves) {
      when (it.dir) {
        "forward" -> {
          x += it.num
          y += it.num * aim
        }
        "down" -> { aim += it.num }
        "up" -> { aim -= it.num }
        else -> error("What is this ${it.dir}")
      }
    }

    return x * y
  }

  val input = readInput("Day02")
  println(part1(input))
  println(part2(input))
}
