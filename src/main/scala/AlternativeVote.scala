import AlternativeVote.Candidate

import scala.annotation.tailrec
import scala.util.Random

class AlternativeVote(var originalVoteLists: Seq[Seq[Candidate]]) {
  originalVoteLists = originalVoteLists
    .map(_.distinct) // should not have same candidate multiple times on list!
    .sortBy(_.hashCode) // order should never depend on input order!
  val rnd = new Random(originalVoteLists.map(_.toString).sorted.toString.hashCode) // let the seed depend on the input

  def invalidCandidates(validCandidates: Set[Candidate]): Set[Candidate] =
    originalVoteLists.flatten.toSet -- validCandidates

  def randomTieBreaking(set: Set[Candidate]): Set[Candidate] = {
    dbg("WARN: Random tie breaking!")
    set - (set.toSeq.sortBy(_.toString).toSeq(rnd.nextInt(set.size)))
  }

  def tieBreakingByOverallVotes(criticalCandidates: Set[Candidate]): Set[Candidate] =
    originalVoteLists
      .flatMap(_.filter(criticalCandidates))
      .groupBy(identity)
      .view.mapValues(_.size)
      .groupBy(_._2)
      .view.mapValues(_.map(_._1))
      .toMap
    match {
      case byNumOfVotes if byNumOfVotes.sizeIs > 1 =>
        dbg(s"tieBreaking: There were different numbers of overall votes: ${byNumOfVotes.view.mapValues(_.toSeq).toMap}")
        byNumOfVotes.maxBy(_._1)._2.toSet
      case _ =>
        randomTieBreaking(criticalCandidates)
    }

  def getCurVotes(voteLists: Seq[Seq[Candidate]]): Map[Candidate, Int] =
    voteLists.toSeq
      .flatMap(_.headOption)
      .groupBy(identity)
      .view.mapValues(_.size)
      .toMap.withDefaultValue(0)

  def dbg(s: => String) = () // println(s)

  @tailrec
  final def getWinner(voteLists: Seq[Seq[Candidate]] = originalVoteLists,
                      tieBreaking: Set[Candidate] => Set[Candidate] = tieBreakingByOverallVotes
                     ): Candidate = {
    voteLists.reduce(_ ++ _).distinct match {
      case Seq(winner) => winner
      case Seq() => throw new NoSuchElementException("Can't make up a winner from an empty list 🤷")
      case remainingCandidates: Seq[Candidate] =>
        val curVotes = getCurVotes(voteLists)
        val leastVotes: Int = remainingCandidates.map(curVotes).min
        val remainingVotes: Seq[Seq[Candidate]] = voteLists.map(_.filter(curVotes(_) > leastVotes))
        dbg(s"=========== Another Round ===========")
        dbg(s"voteLists: $voteLists")
        dbg(s"curVotes: $curVotes")
        dbg(s"leastVotes: $leastVotes; kicking out ${remainingCandidates.filter(curVotes(_) == leastVotes).mkString(", ")}")
        dbg(s"remainingVotes: $remainingVotes")
        if (remainingVotes.forall(_.isEmpty)) {
          dbg(s"tieBreaking: All remaining candidates (${remainingCandidates.mkString(", ")}) have $leastVotes votes.")
          val keptCandidates: Set[Candidate] = tieBreaking(remainingCandidates.toSet)
          dbg(s"tieBreaking: ${remainingCandidates.toSet -- keptCandidates} was removed by tieBreaking algorithm. $keptCandidates was kept.")
          assert(keptCandidates.nonEmpty)
          assert(keptCandidates != remainingCandidates.toSet)
          assert(keptCandidates subsetOf remainingCandidates.toSet)
          getWinner(voteLists.map(_.filter(keptCandidates)), tieBreaking)
        } else {
          getWinner(remainingVotes, tieBreaking)
        }
    }
  }
}

object AlternativeVote {
  opaque type Candidate = String
  def candidate(s: String): Candidate = s
}