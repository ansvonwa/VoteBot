import AlternativeVote.{Candidate, candidate}

import java.io.File
import scala.io.Source
import scala.util.Random

object Main {

  def readVoteLists(file: File): Seq[Seq[Candidate]] =
    Source.fromFile(file)
      .getLines()
      .mkString("")
      .replaceAll(" +", "")
      .split("\\],\\[")
      .toVector
      .map(
        _.split(",")
          .toVector
          .map(_.replaceAll("[\\[\\]\\\"]", ""))
          .filter(_.nonEmpty)
          .map(AlternativeVote.candidate))

  def main(args: Array[String]): Unit = {
    val file = new File(if (args.size >= 1) args(0) else "vote_lists.json")
    val num = if (args.size >= 2) args(1).toInt else 5
    val voteLists = readVoteLists(file)
    println(voteLists)
    val vote = new IterativeAlternativeVote(voteLists)
    println(vote.winners.take(num).toVector)
  }
}
