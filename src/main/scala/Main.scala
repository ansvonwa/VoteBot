import AlternativeVote.{Candidate, candidate}

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
  }

  def msg = "I was compiled by dotty :)"

}
