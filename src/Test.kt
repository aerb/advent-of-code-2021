
fun main() {
  val lines = readInput("Day08")
  val entries = lines.map { line ->
    val sections = line.split("|").map { section -> section.split(" ").filter { it.isNotBlank() } }
    sections[0] to sections[1]
  }
  val uniques = map.values.groupingBy { it.size }.eachCount().filterValues { it == 1 }.keys
  println(entries.flatMap { (_, output) -> output }.count { it.length in uniques })
  println(entries.sumOf { (input, output) -> getOutputDigit(input, output) })
}

private fun getOutputDigit(input: List<String>, output: List<String>): Int {
  val codes = (input + output).toSet()
  var candidates = symbols.associateWith { symbols }
  for (code in codes) {
    val possible = lengthToPossibleSegments.getValue(code.length)
    val mustHave = lengthToMustHaveSegments.getValue(code.length)
    candidates = candidates.mapValues { (wire, candidate) ->
      if (wire in code) (candidate intersect possible) else (candidate - mustHave)
    }
  }
  while (!candidates.all { it.value.size == 1 }) {
    val uniques = candidates.values.filter { it.size == 1 }.flatten().toSet()
    candidates =
      candidates.mapValues { (_, candidates) -> if (candidates.size == 1) candidates else candidates - uniques }
        .toMutableMap()
  }
  val dict = candidates.mapValues { it.value.first() }
  return output.map { code ->
    val segments = code.map { dict.getValue(it) }.toSet()
    reverseMap.getValue(segments)
  }.joinToString("").toInt()
}

private val symbols = ('a'..'g').toSet()
private val map = mapOf(
  0 to "abcefg",
  1 to "cf",
  2 to "acdeg",
  3 to "acdfg",
  4 to "bcdf",
  5 to "abdfg",
  6 to "abdefg",
  7 to "acf",
  8 to "abcdefg",
  9 to "abcdfg"
).mapValues { it.value.toSet() }
private val reverseMap = map.map { (k, v) -> v to k }.toMap()
private val lengthToPossibleSegments =
  map.values.groupBy { it.size }.mapValues { segments -> segments.value.flatten().toSet() }
private val lengthToMustHaveSegments =
  map.values.groupBy { it.size }.mapValues { segments -> segments.value.reduce { a, b -> a intersect b } }