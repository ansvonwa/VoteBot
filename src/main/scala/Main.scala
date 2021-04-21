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
    val num = if (args.size >= 2) args(1).toInt match {
      case x if x >= 0 => x
      case _ => Int.MaxValue
    } else 5
    if (!file.exists()) {
      println(
        "usage: votebot <vote_lists> [num]\n" +
          "\n" +
          "vote_lists  A .json-file containing a list of lists of names of votes.\n" +
          "            Format: [['candidateA', ...], ...]\n" +
          "num         The number of winners to print. Default: 5")
      System.exit(1)
    }
    val voteLists = readVoteLists(file)
    val vote = new IterativeAlternativeVote(voteLists)
    println(vote.winners.take(num).toVector)
  }
}
