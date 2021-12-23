import java.io.File
import kotlin.math.abs

private typealias Distance = Set<Int>

fun main() {
  data class Point(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String = "$x,$y,$z"
  }

  data class Scanner(val beacons: List<Point>)

  data class DistanceProfile(val point: Point, val distances: Set<Distance>)

  data class Match(val a: DistanceProfile, val b: DistanceProfile)

  val scanners: List<Scanner> =
    File("src/Day19_test.txt").readText()
      .split(Regex("--- scanner [0-9] ---"))
      .filter { it.isNotBlank() }
      .map { group ->
        Scanner(
          group.reader()
            .readLines()
            .filter { it.isNotBlank() }
            .map { line ->
              line.split(",").map(String::toInt).let { (x, y, z) -> Point(x, y, z) }
            }
        )
      }

  fun process(s: Scanner): List<DistanceProfile> {
    fun diff(a: Point, b: Point): Distance =
      setOf(
        abs(a.x - b.x),
        abs(a.y - b.y),
        abs(a.z - b.z),
      )

    fun distanceProfile(reference: Point): DistanceProfile =
      s.beacons
        .filter { it !== reference }
        .map { diff(reference, it) }
        .let { DistanceProfile(reference, it.toSet()) }


    return s.beacons.map { beacon -> distanceProfile(beacon) }
  }

  fun compare(a: List<DistanceProfile>, b: List<DistanceProfile>): List<Match> {
    val matching = ArrayList<Match>()
    for (i in a.indices) {
      for (j in b.indices) {
        val left = a[i]
        val right = b[j]
        val common = left.distances intersect right.distances
        if (common.size > 5) {
          matching += Match(left, right)
        }
      }
    }
    return matching
  }

  fun Point.rotations(): Set<Point> {
    fun cos(by: Int): Int = when (by) {
      0 -> 1
      1 -> 0
      2 -> -1
      3 -> 0
      else -> error(by)
    }

    fun sin(by: Int): Int = when (by) {
      0 -> 0
      1 -> 1
      2 -> 0
      3 -> -1
      else -> error(by)
    }

    fun Point.rotateX(angle: Int): Point = copy(
      y = y * cos(angle) + -z * sin(angle),
      z = y * sin(angle) + z * cos(angle)
    )

    fun Point.rotateY(angle: Int): Point = copy(
      x = x * cos(angle) + z * sin(angle),
      z = -x * sin(angle) + z * cos(angle)
    )

    fun Point.rotateZ(angle: Int): Point = copy(
      x = x * cos(angle) + -y * sin(angle),
      y = x * sin(angle) + y * cos(angle)
    )

    fun Point.rotateBy(x: Int, y: Int, z: Int): Point = rotateX(x).rotateY(y).rotateZ(z)

    val points = ArrayList<Point>()

    for (x in 0..3)
      for (y in 0..3)
        for (z in 0..3)
          points += rotateBy(x, y, z)

    return points.toSet()
  }

  fun diff(p0: Point, p1: Point) = Point(
    x = p0.x - p1.x,
    y = p0.y - p1.y,
    z = p0.z - p1.z,
  )

  fun add(p0: Point, p1: Point) = Point(
    x = p0.x + p1.x,
    y = p0.y + p1.y,
    z = p0.z + p1.z,
  )


  data class Translation(val mapping: Pair<Int, Int>, val translation: Point)

  val translations = ArrayList<Translation>()

  val processedScanner = scanners.map { it to process(it) }
  for (i in processedScanner.indices) {
    for (j in i + 1 until processedScanner.size) {
      val beaconA = processedScanner[i].second
      val beaconB = processedScanner[j].second
      val matching = compare(beaconA, beaconB)

      if (matching.isNotEmpty()) {
        val commonPositions: Set<Point> = matching.map { match ->
          match.b.point.rotations().map { point ->
            diff(match.a.point, point)
          }.toSet()
        }.reduce { acc, points ->
          points intersect acc
        }
        translations += Translation(i to j, commonPositions.single())
      }
    }
  }

  translations.printEach()

  var i = 0
  val results: MutableMap<Int, Point?> = scanners.indices.associateWith { null }.toMutableMap()
  results[0] = Point(0, 0, 0)
  while (true) {
    val toResolve = results.toList().filter { it.second == null }
    if (toResolve.isEmpty()) break

    toResolve.forEach { res ->
      val resolves = translations.firstOrNull {
        it.mapping.second == res.first && it.mapping.second in results
      }
      println("$res $resolves")

      if (resolves != null) {

        results[res.first] = add(resolves.translation, results[resolves.mapping.first]!!)

        println("${results[res.first]} = ${resolves.translation} + ${results[resolves.mapping.first]!!}")
      }
    }

    if (i++ > 100) error(results)

  }
}

private fun <E> List<E>.printEach(function: (E) -> Any? = { it }): List<E> {
  return onEach {
    println(function(it))
  }
}
