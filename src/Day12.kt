fun main() {
  fun part1(input: List<String>): Int {
    val connections = input.map { it.split("-") }.flatMap { (a, b) -> listOf(a to b, b to a) }
      .groupBy(
        keySelector = { it.first },
        valueTransform = { it.second }
      )
    println(connections)
    fun explore(key: String, visited: List<String>): List<List<String>> {
      println(visited)
      val paths = connections.getValue(key)
      val nextVisited = visited + key

      if (key == "end") {
        return listOf(nextVisited.toList())
      }

      return paths.mapNotNull { next ->
        if (next.isSmall()) {
          if (next !in nextVisited) {
            explore(next, nextVisited)
          } else {
            null
          }
        } else {
          explore(next, nextVisited)
        }
      }.flatten()
    }

    val found = explore("start", emptyList()).distinct()

    return found.size
  }

  fun part2(input: List<String>): Int {
    val connections = input.map { it.split("-") }.flatMap { (a, b) -> listOf(a to b, b to a) }
      .groupBy(
        keySelector = { it.first },
        valueTransform = { it.second }
      )
    println(connections)
    fun explore(key: String, visited: List<String>, visitedTwice: Boolean): List<List<String>> {
      val paths = connections.getValue(key)
      val nextVisited = visited + key

      if (key == "end") {
        return listOf(nextVisited.toList())
      }

      return paths.mapNotNull { next ->
        if (next == "start") {
          null
        } else if (next.isSmall()) {
          val hasVisited = next in visited
          if (!hasVisited) {
            explore(next, nextVisited, visitedTwice)
          } else if (!visitedTwice) {
            explore(next, nextVisited, true)
          } else {
            null
          }
        } else {
          explore(next, nextVisited, visitedTwice)
        }
      }.flatten()
    }

    val found = explore("start", emptyList(), false).distinct()

    found.forEach {
      println(it.joinToString(separator = ","))
    }

    return found.size
  }

  val input = readInput("Day12")
//  println(part1(input))
  println(part2(input))
}

private fun String.isSmall(): Boolean = all { it.isLowerCase() }
