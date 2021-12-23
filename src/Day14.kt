fun main() {
  fun part1(input: List<String>): Int {
    val translations = input.drop(2).associate { line ->
      val (from, to) = line.split(" -> ")
      from to to
    }

    var seq = input.first()
    repeat(10) {
      seq = seq.windowed(2).map { pair ->
        val insertion = translations[pair]
        if (insertion != null) {
          "${pair[0]}$insertion${pair[1]}"
        } else {
          pair
        }
      }.let { list ->
        list.mapIndexed { index, s ->
          if (index == list.lastIndex) s
          else s.dropLast(1)
        }
      }.joinToString(separator = "")

      println("$it ${seq.length} $seq")
    }

    val charCount = seq.groupingBy { it }.eachCount()

    return charCount.maxOf { it.value } - charCount.minOf { it.value }
  }

  fun part2(input: List<String>): Long {
    val translations = input.drop(2).associate { line ->
      val (from, to) = line.split(" -> ")
      from to to
    }

    var pairs: Map<String, Long> = input.first().windowed(2).groupingBy { it }.eachCount().mapValues { it.value.toLong() }
    val charCount: MutableMap<Char, Long> = input.first().groupingBy { it }.eachCount().mapValues { it.value.toLong() }.toMutableMap()
//    var seq = input.first()
    repeat(40) {
//      println(seq)
      val next = pairs.toMutableMap()

      fun inc(key: String, by: Long) {
        next[key] = (next[key] ?: 0) + by
        println("inc $key ${next[key]}")
      }

      fun dec(key: String, by: Long) {
        next[key] = next[key]!! - by
        println("dec $key ${next[key]}")
      }

      translations.forEach { (pair, insert) ->
        if (pair in pairs) {
          println("$pair -> $insert")
          val num = pairs[pair]!!
          dec(pair, num)
          inc(pair.first() + insert, num)
          inc(insert + pair.last(), num)
          charCount[insert.single()] = (charCount[insert.single()] ?: 0) + num
        }
      }
      pairs = next.filterValues { it != 0L }

//      seq = seq.windowed(2).map { pair ->
//        val insertion = translations[pair]
//        if (insertion != null) {
//          "${pair[0]}$insertion${pair[1]}"
//        } else {
//          pair
//        }
//      }.let { list ->
//        list.mapIndexed { index, s ->
//          if (index == list.lastIndex) s
//          else s.dropLast(1)
//        }
//      }.joinToString(separator = "")

      println(pairs.filterValues { it != 0L }.toSortedMap())
      println(charCount.toSortedMap())

//      println(seq)
//      val actualpairs = seq.windowed(2).groupingBy { it }.eachCount().toSortedMap()
//      println("actual " + actualpairs)
//      println("actual " + seq.groupingBy { it }.eachCount().toSortedMap())

//      check(actualpairs == pairs)
    }

    return charCount.maxOf { it.value } - charCount.minOf { it.value }
  }

  val input = readInput("Day14")
//  println(part1(input))
  println(part2(input))
}
