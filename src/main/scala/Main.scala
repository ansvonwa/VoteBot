import AlternativeVote.Candidate

import java.io.File
import scala.io.Source

object Main {

  def readVoteLists(file: File): Seq[Seq[Candidate]] = {
    val source = Source.fromFile(file)
    val res = source.getLines()
      .mkString("")
      .replaceAll(" +", "")
      .split("],\\[")
      .toVector
      .map(
        _.split(",")
          .toVector
          .map(_.replaceAll("[\\[\\]\"']", ""))
          .filter(_.nonEmpty)
          .map(AlternativeVote.candidate))
    source.close()
    res
  }

  def main(args: Array[String]): Unit = {
    val file = new File(if (args.size >= 1) args(0) else "vote_lists.json")
    val num = if (args.size >= 2) args(1).toInt else 5
    val voteLists = readVoteLists(file)
    println(voteLists)
    val vote = new IterativeAlternativeVote(voteLists)
    println(vote.winners.take(num).toVector)
  }
}
