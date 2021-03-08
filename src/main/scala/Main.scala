import AlternativeVote.{Candidate, candidate}

object Main {

  def voteLists(s: String): Seq[Seq[Candidate]] =
    s.split(",").toSeq
      .map(candidates)

  def candidates(s: String): Seq[Candidate] =
    s.map((c: Char) => candidate(c.toString)).toSeq

  def main(args: Array[String]): Unit = {
    val av = AlternativeVote(candidates("ab").toSet)
    println(av.getWinner(voteLists("a,ab"), _ => throw new Error))
    println(av.getWinner(voteLists("a,a,ab,abc"), _ => throw new Error))
  }

  def msg = "I was compiled by dotty :)"

}
