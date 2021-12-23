// 1 (2), 4 (4), 7 (3), and 8 (7)
//
fun main() {
  val segs = listOf(
    "abcefg", //0
    "cf", //1
    "acdeg", //2
    "acdfg",//3
    "bdcf",//4
    "abdfg",//5
    "abdfge",//6
    "acf",//7
    "abcdefg",//8
    "abcdfg",//9
  ).map { it.toSet() }

  // len(2) = 1
  // len(4) = 4
  // len(3) = 7
  // len(7) = 8
  // 7 - 1 = A
  // single(all - (7 + 4)) = G
  // (7 + 4 + G) = 9
  // 8 - 9 = E
  // single(all - (7 + 1 + G)) = D
  // 7 + D + G = 3
  // 8 - D = 0
  fun resolve(tokens: List<Set<Char>>): Map<Set<Char>, Int> {
    val map = HashMap<String, Set<Char>>()
    fun len(size: Int): Set<Char> = tokens.single { it.size == size }
    fun set(name: String): Set<Char> = map.getValue(name)
    fun single(transform: (Set<Char>) -> Set<Char>): Set<Char> =
      tokens.map(transform).filter { it.size == 1 }.distinct().let {
        if (it.size > 1) error("multiple left $it")
        it.single()
      }
    fun singles(transform: (Set<Char>) -> Set<Char>): List<Set<Char>> =
      tokens.map(transform).filter { it.size == 1 }.distinct()

    fun sum(keys: String): Set<Char> {
      return keys.map { c -> set(c.toString()) }.reduce { acc, next -> acc + next }
    }

    fun render(set: Set<Char>): String = set.joinToString(separator = "")

    map["1"] = len(2)
    map["4"] = len(4)
    map["7"] = len(3)
    map["8"] = len(7)
    map["A"] = set("7") - set("1")
    map["G"] = single { it - (set("7") + set("4")) }
    map["9"] = set("7") + set("4") + set("G")
    map["E"] = set("8") - set("9")
    map["D"] = single { it - (set("7") + set("1") + set("G")) }
    map["3"] = set("7") + set("D") + set("G")
    map["0"] = set("8") - set("D")
    map["C"] = singles { set("8") - it }.filter { it != set("D") && it != set("E") }.single()
    map["6"] = set("8") - set("C")
    map["5"] = set("8") - set("C") - set("E")
    map["2"] = sum("ACDEG")

    map.entries.toList().sortedBy { it.key }.forEach { println(it) }
    return map.toList().filter { it.first.toIntOrNull() != null }.associate { it.second to it.first.toInt() }
  }

  resolve(segs)

  fun part1(input: List<String>): Int {
    val words: List<List<String>> = input.map { it.split("|").last().split(" ").filter { it.isNotBlank() } }
    return words.flatten().count {
      it.length in setOf(2, 4, 3, 7)
    }
  }

  fun part2(input: List<String>): Long {
    data class Observations(val seen: List<Set<Char>>, val code: List<Set<Char>>)

    val obs: List<Observations> = input.map {
      val (first, second) = it.split("|").map { it.split(" ").filter { it.isNotBlank() }.map { it.toSet() } }
      Observations(first, second)
    }

    val total = obs.sumOf {
      val resolved = resolve(it.seen)

      val num = it.code.map { resolved.getValue(it).toString() }.joinToString(separator = "").toLong()
      println(num)
      num
    }

    return total
  }

  val input = readInput("Day08")
//  println(part1(input))
  println(part2(input))
}
