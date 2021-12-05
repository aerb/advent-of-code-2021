fun main() {

  fun List<Int>.toDec(): Int {
    var total = 0
    reversed().forEachIndexed { index, bit ->
      total += bit * 1.shl(index)
    }
    return total
  }

  fun part1(lines: List<String>): Int {
    val width = lines.first().length
    val zeros = MutableList(width) { 0 }
    val ones = MutableList(width) { 0 }

    lines.forEach { line ->
      line.forEachIndexed { index, bit ->
        when (bit) {
          '0' -> zeros[index]++
          '1' -> ones[index]++
          else -> error("$bit ?")
        }
      }
    }

    val max = MutableList(width) { 0 }
    for (i in 0 until width) {
      require(zeros[i] != ones[i])
      max[i] = if (zeros[i] > ones[i]) 0 else 1
    }

    val gamma = max.toDec()
    val ep = max.map { if (it == 0) 1 else 0 }.toDec()

    return gamma * ep
  }

  fun part2(lines: List<String>): Int {
    fun List<String>.bitGroup(i: Int): List<String> {
      val groups = groupBy { line -> line[i] }
      val zeros = groups['0'] ?: emptyList()
      val ones = groups['1'] ?: emptyList()
      if (zeros.size > ones.size) return zeros
      else return ones
    }

    fun List<String>.bitGroupInv(i: Int): List<String> {
      val groups = groupBy { line -> line[i] }
      val zeros = groups['0'] ?: emptyList()
      val ones = groups['1'] ?: emptyList()
      if (zeros.size <= ones.size) return zeros
      else return ones
    }

    fun String.toDec(): Int {
      return map { it.toString().toInt() }.toDec()
    }

    var ox = lines.toList()
    var bit = 0
    while (ox.size != 1) {
      ox = ox.bitGroup(bit)
      bit ++
    }

    var co2 = lines.toList()
    bit = 0
    while (co2.size != 1) {
      co2 = co2.bitGroupInv(bit)
      bit ++
    }



    return (ox.single().toDec() * co2.single().toDec())
  }

  val input = readInput("Day03")
  println(part1(input))
  println(part2(input))
}
