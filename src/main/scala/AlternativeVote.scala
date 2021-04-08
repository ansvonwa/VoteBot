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

  var randomTieBreaking: Set[Candidate] => Set[Candidate] =
    (set: Set[Candidate]) => {
      warn(s"Random tie breaking between $set!")
      set - (set.toSeq.sortBy(_.toString).toSeq(rnd.nextInt(set.size)))
    }

  def tieBreakingByOverallVotes(criticalCandidates: Set[Candidate]): Set[Candidate] = {
    def byCount(vl: Seq[Candidate]): Map[Int, Iterable[Candidate]] =
      vl
        .filter(criticalCandidates)
        .groupBy(identity)
        .view.mapValues(_.size)
        .groupBy(_._2)
        .view.mapValues(_.map(_._1))
        .toMap
    byCount(originalVoteLists.flatten) match {
      case byNumOfVotes if byNumOfVotes.sizeIs > 1 => // if candidates occur different number of times, we have a criterion!
        dbg(s"tieBreaking: There were different numbers of overall votes: ${byNumOfVotes.view.mapValues(_.toSeq).toMap}")
        byNumOfVotes.maxBy(_._1)._2.toSet
      case _ => // try cutting off lists at the lists ends, so we may get different results
        val longestVLSize = originalVoteLists.map(_.size).max
        (1 until longestVLSize).reverse.map { length =>
          byCount(originalVoteLists.flatMap(_.take(length)))
        }.find(_.sizeIs > 1) match {
          case Some(map) =>
            dbg(s"tieBreaking: After cutting off the ends of the voting lists, there were different numbers of overall votes: ${map.view.mapValues(_.toSeq).toMap}")
            map.maxBy(_._1)._2.toSet
          case None =>
            Console.err.println(originalVoteLists)
            Console.err.println("  " + originalVoteLists.map(_.filter(criticalCandidates)))
//            println(bnov.mapValues(_.toSet).toMap)
            randomTieBreaking(criticalCandidates)
        }
    }
  }

  def getCurVotes(voteLists: Seq[Seq[Candidate]]): Map[Candidate, Int] =
    voteLists.toSeq
      .flatMap(_.headOption)
      .groupBy(identity)
      .view.mapValues(_.size)
      .toMap.withDefaultValue(0)

  @inline def dbg(s: => String) = () // println(s)
  @inline def warn(s: => String) = Console.err.println(s)

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
          assert(keptCandidates.nonEmpty && keptCandidates != remainingCandidates.toSet && keptCandidates.subsetOf(remainingCandidates.toSet),
            "tie breaking must return a proper subset of the given candidates!")
          getWinner(voteLists.map(_.filter(keptCandidates)), tieBreaking)
        } else {
          getWinner(remainingVotes, tieBreaking)
        }
    }
  }
}

object AlternativeVote {
  /*opaque*/ type Candidate = String
  def candidate(s: String): Candidate = s
}
