import AlternativeVote.Candidate

import scala.annotation.tailrec
import scala.util.Random

class AlternativeVote(candidates: Set[Candidate]) {
  val rnd = Random(0)

  def dbg(s: => String) = println(s)
  
  @tailrec
  final def getWinner(voteLists: Seq[Seq[Candidate]],
                      tieBreaking: Set[Candidate] => Candidate = set => set.toSeq(rnd.nextInt(set.size))
                     ): Candidate = {
    voteLists.reduce(_ ++ _).distinct match {
      case Seq(winner) => winner
      case Seq() => throw new NoSuchElementException("Can't make up a winner from an empty list 🤷")
      case remainingCandidates: Seq[Candidate] =>
        val curVotes = voteLists.toSeq.flatMap(_.headOption).groupBy(identity).view.mapValues(_.size).toMap.withDefaultValue(0)
        dbg(s"=========== Another Round ===========")
        dbg(s"voteLists: $voteLists")
        dbg(s"curVotes: $curVotes")
        val leastVotes: Int = remainingCandidates.map(curVotes).min
        val remainingVotes: Seq[Seq[Candidate]] = voteLists.map(_.filter(curVotes(_) > leastVotes))
        dbg(s"leastVotes: $leastVotes; kicking out ${remainingCandidates.filter(curVotes(_) == leastVotes).mkString(", ")}")
        dbg(s"remainingVotes: $remainingVotes")
        if (remainingVotes.forall(_.isEmpty)) {
          dbg(s"WARN: There are only ${remainingCandidates.size} candidates with $leastVotes vote remaining: "+remainingCandidates.mkString(", "))
          val winner = tieBreaking(remainingCandidates.toSet)
          dbg(s"WARN: $winner was chosen by tieBreaking algorithm")
          winner
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