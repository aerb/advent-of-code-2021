private val errorScore = mapOf(
  ')' to 3,
  ']' to 57,
  '}' to 1197,
  '>' to 25137,
)

private val repairScore = mapOf(
')' to 1,
']' to 2,
'}' to 3,
'>' to 4,
)


private sealed class Result {
  data class Error(val char: Char, val pos: Int) : Result()
  data class Repair(val completion: String) : Result()
}

fun main() {

  val def = mapOf(
    '(' to ')',
    '[' to ']',
    '<' to '>',
    '{' to '}',
  )

  val reverse = def.entries.associate { it.value to it.key }




  fun eval(line: String): Result {
    val stack = ArrayList<Char>()
    var i = 0
    while (true) {
      if (i >= line.length) return Result.Repair(stack.joinToString(separator = "") { def[it]!!.toString() } )

      val next = line[i]
      if (next in def) {
        stack.add(0, next)
        i++
      } else {
        val out = stack.removeAt(0)
        if (def[out] != next) {
          return Result.Error(next, i)
        }
        i++
      }
    }
  }

  fun part1(input: List<String>): Int {
    return input.mapNotNull { line -> eval(line) as? Result.Error }.sumOf { error ->
      errorScore.getValue(error.char)
    }
  }

  fun score(completion: String): Long {
    var score = 0L
    completion.forEach { char ->
      score *= 5
      score += repairScore[char]!!
    }
    return score
  }

  fun part2(input: List<String>): Long {
    val scores =  input.mapNotNull { line -> eval(line) as? Result.Repair }.map { repair ->
      score(repair.completion)
    }.sorted()

    println(scores)
    return scores[(scores.size / 2)]
  }

  val input = readInput("Day10")
//  println(part1(listOf("<<>><>")))
//  println(part1(input))
  println(part2(input))
}
