import AlternativeVote.{Candidate, candidate}

import scala.util.Random

object Main {

  def voteLists(s: String): Seq[Seq[Candidate]] =
    s.split(",").toSeq
      .map(candidates)

  def candidates(s: String): Seq[Candidate] =
    s.map((c: Char) => candidate(c.toString)).toSeq

  def main(args: Array[String]): Unit = {
    println(AlternativeVote(voteLists("a,ab")).getWinner(tieBreaking = _ => throw new Error))
    println(AlternativeVote(voteLists("a,a,ab,abc")).getWinner(tieBreaking = _ => throw new Error))
    println(AlternativeVote(voteLists("a,a,ba,bac")).getWinner())

    val s = Random(3).alphanumeric.map {
      case c if c <= '9' => ','
      case c => c.toLower
    }.take(500).mkString("")
    println(s)
    println(AlternativeVote(voteLists(s)).getWinner())

    println(IterativeAlternativeVote(voteLists(s)).winners.toVector)
  }

  def msg = "I was compiled by dotty :)"

}
