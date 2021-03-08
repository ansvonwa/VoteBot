import AlternativeVote.Candidate

import scala.annotation.tailrec
import scala.util.Random

class AlternativeVote(candidates: Set[Candidate]) {
  val rnd = Random(0)

  def getCurVotes(voteLists: Seq[Seq[Candidate]]): Map[Candidate, Int] =
    voteLists.toSeq
      .flatMap(_.headOption)
      .groupBy(identity)
      .view.mapValues(_.size)
      .toMap.withDefaultValue(0)

  def dbg(s: => String) = println(s)

  @tailrec
  final def getWinner(voteLists: Seq[Seq[Candidate]],
                      tieBreaking: Set[Candidate] => Set[Candidate] = set => set - set.toSeq(rnd.nextInt(set.size))
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
          val keptCandidates: Set[Candidate] = tieBreaking(remainingCandidates.toSet)
          dbg(s"WARN: All candidates (${remainingCandidates.mkString(", ")}) have $leastVotes votes now.")
          dbg(s"WARN: ${remainingCandidates.toSet -- keptCandidates} was/were removed by tieBreaking algorithm. $keptCandidates was/were kept.")
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