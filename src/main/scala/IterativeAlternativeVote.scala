import AlternativeVote.Candidate

class IterativeAlternativeVote(var voteLists: Seq[Seq[Candidate]]) {
  voteLists = voteLists
    .map(_.distinct) // should not have same candidate multiple times on list!
    .sortBy(_.hashCode) // order should never depend on input order!

  def winners: LazyList[Candidate] = {
    if (voteLists.flatten.isEmpty) LazyList()
    else {
      val winner = new AlternativeVote(voteLists).getWinner()
      winner #:: new IterativeAlternativeVote(voteLists.map(_.filter(_ != winner))).winners
    }
  }
}
