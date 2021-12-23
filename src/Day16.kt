import java.lang.Long.max
import java.lang.Long.min
import java.util.ArrayList

val hexToBinary = mapOf(
  '0' to "0000",
  '1' to "0001",
  '2' to "0010",
  '3' to "0011",
  '4' to "0100",
  '5' to "0101",
  '6' to "0110",
  '7' to "0111",
  '8' to "1000",
  '9' to "1001",
  'A' to "1010",
  'B' to "1011",
  'C' to "1100",
  'D' to "1101",
  'E' to "1110",
  'F' to "1111",
)

fun main() {

  fun Int.toBoolean(): Boolean = when (this) {
    0 -> false
    1 -> true
    else -> error(this)
  }

  fun part1(input: List<String>): Int {
    var sum = 0L

    fun parsePacket(bits: List<Int>, start: Int): Int {
      var i = start

      fun readBits(num: Int): List<Int> {
        val r = bits.subList(i, i + num)
        i += num
        return r
      }

      val version = bits.subList(i, i + 3).toInt()
      sum += version
      i += 3
      val type = bits.subList(i, i + 3).toInt()
      i += 3
      when (type) {
        4L -> {
          val read = ArrayList<Int>()
          while (true) {
            val cont = bits[i++].toBoolean()
            read += bits.subList(i, i + 4)
            i += 4
            if (!cont) return i
          }
        }
        else -> {
          val lenId = bits[i++]
          when (lenId) {
            0 -> {
              val len = readBits(15).toInt().toInt()
              val readStart = i
              while (true) {
                val end = parsePacket(bits, i)
                i = end
                val diff = end - readStart
                if (diff >= len) {
                  if (diff != len) {
                    println("Hmm $diff should be $len")
                  }
                  return i
                }
              }
            }
            1 -> {
              val subPackets = readBits(11).toInt()
              repeat(subPackets.toInt()) {
                val end = parsePacket(bits, i)
                i = end
              }
              return i
            }
            else -> error(lenId)
          }
        }
      }
    }

    val bits = input.map { line ->
      line to line.flatMap { hex ->
        hexToBinary[hex]!!.toList().map { it.digitToInt() }
      }
    }

    bits.forEach {
      sum = 0
      parsePacket(it.second, 0)
      println(it.first + " = " + sum)
    }

    return input.size
  }

  fun part2(input: List<String>): Long {
    data class Result(val value: Long, val end: Int)

    fun parsePacket(bits: List<Int>, start: Int): Result {
      var i = start

      fun readBits(num: Int): List<Int> {
        val r = bits.subList(i, i + num)
        i += num
        return r
      }

      val version = bits.subList(i, i + 3).toInt()
      i += 3
      val type = bits.subList(i, i + 3).toInt().toInt()
      i += 3
      when (type) {
        4 -> {
          val read = ArrayList<Int>()
          while (true) {
            val cont = bits[i++].toBoolean()
            read += bits.subList(i, i + 4)
            i += 4
            if (!cont) return Result(read.toInt(), i)
          }
        }
        else -> {
          val lenId = bits[i++]
          val packetResults: List<Result> = when (lenId) {
            0 -> {
              val len = readBits(15).toInt().toInt()
              val readStart = i
              val results = ArrayList<Result>()
              while (true) {
                val result = parsePacket(bits, i)
                results += result
                i = result.end
                val diff = result.end - readStart
                if (diff >= len) {
                  if (diff != len) {
                    println("Hmm $diff should be $len")
                  }
                  break
                }
              }
              results
            }
            1 -> {
              val subPackets = readBits(11).toInt()
              List(subPackets.toInt()) {
                val result = parsePacket(bits, i)
                i = result.end
                result
              }
            }
            else -> error(lenId)
          }

          return when(type) {
            0 -> { Result(packetResults.fold(0) { acc, result -> acc + result.value }, packetResults.last().end ) }
            1 -> { Result(packetResults.fold(1) { acc, result -> acc * result.value }, packetResults.last().end ) }
            2 -> { Result(packetResults.fold(Long.MAX_VALUE) { acc, result -> min(acc, result.value) }, packetResults.last().end ) }
            3 -> { Result(packetResults.fold(Long.MIN_VALUE) { acc, result -> max(acc, result.value) }, packetResults.last().end ) }
            5 -> { Result(packetResults.let { (first, second) -> if (first.value > second.value) 1 else 0 }, packetResults.last().end ) }
            6 -> { Result(packetResults.let { (first, second) -> if (first.value < second.value) 1 else 0 }, packetResults.last().end ) }
            7 -> { Result(packetResults.let { (first, second) -> if (first.value == second.value) 1 else 0 }, packetResults.last().end ) }
            else -> error(type)
          }
        }
      }
    }

    val bits = input.map { line ->
      line to line.flatMap { hex ->
        hexToBinary[hex]!!.toList().map { it.digitToInt() }
      }
    }

    bits.forEach {
      return parsePacket(it.second, 0).value
    }

    error("")
  }

  val input = readInput("Day16")
//  println(part1(input))

  println(part2(input))
}

private fun List<Int>.toInt(): Long = joinToString(separator = "").toLong(2)
