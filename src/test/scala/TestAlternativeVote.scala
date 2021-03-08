
import AlternativeVote.{Candidate, candidate}
import org.junit.Test
import org.junit.Assert._

class TestAlternativeVote {
  /**
   * turns "ab,ac,bcd" into Set(Seq(a, b), Seq(a, c), Seq(b, c, d))
   */
  def voteLists(s: String): Seq[Seq[Candidate]] =
    s.split(",").toSeq
      .map(candidates)

  def candidates(s: String): Seq[Candidate] =
    s.map((c: Char) => candidate(c.toString)).toSeq

  val abc = new AlternativeVote(candidates("abc").toSet)

  val noTie: Any => Nothing = x => throw new Error(s"there should not be a tie: $x")

  @Test def t1(): Unit = {
    assertEquals("a", abc.getWinner(voteLists("a,a,ab,abc"), noTie))
  }

  @Test def t2(): Unit = {
    assertEquals("a", abc.getWinner(voteLists("a,ab,cba"), noTie))
  }

  @Test def t3(): Unit = {
    assertEquals("a", abc.getWinner(voteLists("a,ba,abc"), noTie))
  }

  @Test def t4(): Unit = {
    assertEquals("b", abc.getWinner(voteLists("a,ba,bca"), noTie))
  }

  @Test def t5(): Unit = {
    assertEquals("c", abc.getWinner(voteLists("a,b,c"), _ => Set(candidate("c"))))
  }

  @Test def t6(): Unit = {
    assertEquals("a", abc.getWinner(voteLists("a,ba,cba"), _ => Set(candidate("a"), candidate("c"))))
  }

  @Test def t7(): Unit = {
    assertEquals("b", abc.getWinner(voteLists("ba,bc,ca,da,ea"), noTie))
  }

  @Test def t8(): Unit = {
    assertEquals("a", abc.getWinner(voteLists("ba,bc,ca,da,ea,ab,ab"), noTie))
  }

  @Test def t9(): Unit = {
    assertEquals("a", abc.getWinner(voteLists("a,a,a"), noTie))
  }

}