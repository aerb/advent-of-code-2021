@file:Suppress("PackageDirectoryMismatch")

package ca.aerb.aoc2021.day18.part1

import readInput
import java.io.File
import java.lang.Long.max

private sealed class Node {

  fun isLiteral(): Boolean = this is Literal
  fun asLiteral(): Literal = this as Literal
  fun asAddition(): Addition = this as Addition

  data class Literal(var value: Int) : Node() {
    override fun toString(): String = value.toString()
  }

  data class Addition(var left: Node, var right: Node) : Node() {

    fun children(): List<Node> = listOf(left, right)

    fun replace(curr: Node, with: Node) {
      when {
        left === curr -> left = with
        right === curr -> right = with
        else -> error("$with is not a part of $this")
      }
    }

    override fun toString(): String = "[$left,$right]"
  }
}

private data class ParseResult(val node: Node, val end: Int)

fun main() {

  var debug = false

  fun debug(message: () -> Any?) {
    if (debug) {
      println(message.invoke())
    }
  }

  fun log(message: () -> Any?) {
    if (debug) {
      println(message.invoke())
    }
  }

  fun parseExpression(text: String, start: Int): ParseResult {
    val curr = text[start]
    return when (curr) {
      '[' -> {
        val left = parseExpression(text, start + 1)
        check(text[left.end] == ',') { "Expected , but got ${text[left.end]}" }
        val right = parseExpression(text, left.end + 1)
        check(text[right.end] == ']') { "Expected ] but got ${text[left.end]}" }
        ParseResult(
          Node.Addition(left.node, right.node),
          right.end + 1
        )
      }
      in '0'..'9' -> ParseResult(Node.Literal(curr.digitToInt()), start + 1)
      else -> error("Unexpected $curr")
    }
  }

  fun findToExplode(node: Node, chain: List<Node.Addition>): List<Node.Addition>? {
    return when (node) {
      is Node.Literal -> null
      is Node.Addition -> {
        if (node.left.isLiteral() && node.right.isLiteral() && chain.size >= 4) {
          return chain + node
        }
        node.children().firstNotNullOfOrNull { findToExplode(it, chain + node) }
      }
    }
  }

  fun incDescend(node: Node.Addition, value: Int, selector: Node.Addition.() -> Node): Boolean {
    return if (node.selector().isLiteral()) {
      debug { "inc ${node.selector()} by $value" }
      node.selector().asLiteral().value += value
      true
    } else {
      incDescend(node.selector().asAddition(), value, selector)
    }
  }

  fun backoutAndInc(
    path: List<Node.Addition>,
    selector: Node.Addition.() -> Node,
    descendSelector: Node.Addition.() -> Node,
  ): Boolean {
    val value = path.last().selector().asLiteral().value

    for (i in path.lastIndex - 1 downTo 0) {
      val curr = path[i]
      val last = path[i + 1]
      if (curr.selector().isLiteral()) {
        debug { "inc ${curr.selector()} by $value" }
        curr.selector().asLiteral().value += value
        return true
      } else if (curr.selector() !== last) {
        if (incDescend(curr.selector().asAddition(), value, descendSelector)) {
          return true
        }
      }
    }
    return false
  }

  fun findToSplit(node: Node, chain: List<Node>): List<Node>? {
    return when (node) {
      is Node.Literal -> {
        if (node.value > 9) chain + node
        else null
      }
      is Node.Addition -> {
        findToSplit(node.left, chain + node)
          ?: findToSplit(node.right, chain + node)
      }
    }
  }

  fun additionNode(left: Int, right: Int): Node.Addition = Node.Addition(Node.Literal(left), Node.Literal(right))

  fun split(node: Node): Boolean {
    val canSplit = findToSplit(node, emptyList())
    if (canSplit != null) {
      val toSplit = canSplit.last().asLiteral()

      val left = toSplit.value / 2
      val right = (toSplit.value + 1) / 2

      debug { "split $toSplit to $left $right" }

      val parent = canSplit[canSplit.lastIndex - 1] as Node.Addition
      parent.replace(toSplit, additionNode(left, right))
      return true
    }
    return false
  }

  fun explode(node: Node): Boolean {
    val toExplode = findToExplode(node, listOf())
    if (toExplode != null) {
      val remove = toExplode.last()

      debug { "explode $remove" }

      backoutAndInc(toExplode, Node.Addition::left, Node.Addition::right)
      backoutAndInc(toExplode, Node.Addition::right, Node.Addition::left)

      toExplode[toExplode.lastIndex - 1].replace(remove, Node.Literal(0))
      return true
    }
    return false
  }

  fun testExplode(start: String, end: String) {
    val n = parseExpression(start, 0).node
    explode(n)
    check(n.toString() == end) { "$n vs.\n$end" }
  }

  testExplode("[[[[[9,8],1],2],3],4]", "[[[[0,9],2],3],4]")
  testExplode("[7,[6,[5,[4,[3,2]]]]]", "[7,[6,[5,[7,0]]]]")
  testExplode("[[6,[5,[4,[3,2]]]],1]", "[[6,[5,[7,0]]],3]")
  testExplode("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]", "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
  testExplode("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", "[[3,[2,[8,0]]],[9,[5,[7,0]]]]")

  fun addLines(parts: List<Node>): Node {
    var sum: Node.Addition? = null
    parts.forEach { next ->
      if (sum == null) sum = next.asAddition()
      else sum = Node.Addition(sum!!, next)

      log { sum }
      log { "" }
      while (true) {
        val exploded = explode(sum!!)
        if (exploded) {
          log { "exploded $sum" }
          continue
        }

        val wasSplit = split(sum!!)
        if (wasSplit) {
          log { "split $sum" }
        }

        if (!exploded && !wasSplit) break
      }
    }
    return sum!!
  }

  fun addLines(text: String): Node {
    val parts = text.reader().readLines()
      .takeWhile { it.isNotBlank() }
      .map { line ->
        parseExpression(line, 0).node
      }
    return addLines(parts)
  }

  fun testAdd(text: String, expect: String) {
    val result = addLines(text).toString()
    check(result == expect) {
      "\nactual   $result vs.\nexpected $expect"
    }
  }

  testAdd(
    """
      [1,1]
      [2,2]
      [3,3]
      [4,4]
      [5,5]
      [6,6]
    """.trimIndent(),
    "[[[[5,0],[7,4]],[5,5]],[6,6]]"
  )

  testAdd(
    """
    [[[[4,3],4],4],[7,[[8,4],9]]]
    [1,1]
  """.trimIndent(),
    "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"
  )

  testAdd(
    """
      [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
      [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
    """.trimIndent(),
    "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]"
  )

  testAdd(
    """
      [[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]
      [[[[4,2],2],6],[8,7]]
    """.trimIndent(),
    "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"
  )


  testAdd(
    """
      [[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]
      [2,9]
    """.trimIndent(),
    "[[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]"
  )

  testAdd(
    """
      [[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]
      [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
    """.trimIndent(),
    "[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]"
  )

  testAdd(
    """
      [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
      [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
      [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
      [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
      [7,[5,[[3,8],[1,4]]]]
      [[2,[2,2]],[8,[8,1]]]
      [2,9]
      [1,[[[9,3],9],[[9,0],[0,7]]]]
      [[[5,[7,4]],7],1]
      [[[[4,2],2],6],[8,7]]
    """.trimIndent(),
    "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"
  )

  fun evalMag(node: Node): Long {
    return when (node) {
      is Node.Literal -> node.value.toLong()
      is Node.Addition -> {
        val left = evalMag(node.left)
        val right = evalMag(node.right)
        return 3 * left + 2 * right
      }
    }
  }

  fun List<String>.toNodes(): List<Node> = map { line -> parseExpression(line, 0).node }

  fun mag(text: String): Long {
    val result = addLines(text)
    return evalMag(result)
  }

  fun maxMag(text: String): Long {
    val parts = text.reader().readLines()
      .takeWhile { it.isNotBlank() }

    var max = Long.MIN_VALUE
    for (i in parts.indices) {
      for (j in i + 1 until parts.size) {
        val lines = listOf(parts[i], parts[j])

        max = max(max, evalMag(addLines(lines.toNodes())))
        max = max(max, evalMag(addLines(lines.reversed().toNodes())))
      }
    }
    return max
  }

  maxMag(
    """
      [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
      [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
      [[[5,[2,8]],4],[5,[[9,9],0]]]
      [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
      [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
      [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
      [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
      [[[[5,4],[7,7]],8],[[8,3],8]]
      [[9,3],[[9,9],[6,[4,9]]]]
      [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
    """.trimIndent()
  )

  maxMag(
    File("src/Day18.txt").readText()
  ).also { println(it) }
}




