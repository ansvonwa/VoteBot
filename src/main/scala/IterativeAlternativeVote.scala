import AlternativeVote.Candidate

class IterativeAlternativeVote(var voteLists: Seq[Seq[Candidate]]) {
  voteLists = voteLists
    .map(_.distinct) // should not have same candidate multiple times on list!
    .sortBy(_.hashCode) // order should never depend on input order!
  
  def winners: LazyList[Candidate] = {
    try {
      val winner = AlternativeVote(voteLists).getWinner()
      winner #:: IterativeAlternativeVote(voteLists.map(_.filter(_ != winner))).winners
    } catch {
      case _: NoSuchElementException => LazyList()
    }
  }
}
