
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

  val noTie: Any => Nothing = x => throw new Error(s"there should not be a tie: $x")

  @Test def t1(): Unit = {
    assertEquals("a", new AlternativeVote(voteLists("a,a,ab,abc")).getWinner(tieBreaking = noTie))
  }

  @Test def t2(): Unit = {
    assertEquals("a", new AlternativeVote(voteLists("a,ab,cba")).getWinner(tieBreaking = noTie))
  }

  @Test def t3(): Unit = {
    assertEquals("a", new AlternativeVote(voteLists("a,ba,abc")).getWinner(tieBreaking = noTie))
  }

  @Test def t4(): Unit = {
    assertEquals("b", new AlternativeVote(voteLists("a,ba,bca")).getWinner(tieBreaking = noTie))
  }

  @Test def t5(): Unit = {
    assertEquals("c", new AlternativeVote(voteLists("a,b,c")).getWinner(tieBreaking = _ => Set(candidate("c"))))
  }

  @Test def t6(): Unit = {
    assertEquals("a", new AlternativeVote(voteLists("a,ba,cba")).getWinner(tieBreaking = _ => Set(candidate("a"), candidate("c"))))
  }

  @Test def t7(): Unit = {
    assertEquals("b", new AlternativeVote(voteLists("ba,bc,ca,da,ea")).getWinner(tieBreaking = noTie))
  }

  @Test def t8(): Unit = {
    assertEquals("a", new AlternativeVote(voteLists("ba,bc,ca,da,ea,ab,ab")).getWinner(tieBreaking = noTie))
  }

  @Test def t9(): Unit = {
    assertEquals("a", new AlternativeVote(voteLists("a,a,a")).getWinner(tieBreaking = noTie))
  }

  @Test def t10(): Unit = {
    assertEquals("a", new AlternativeVote(voteLists("a,a,ba,bac")).getWinner())
  }

  @Test def t11(): Unit = {
    assertEquals("a", new AlternativeVote(voteLists("a,ba,c")).getWinner())
  }

  @Test def t12(): Unit = {
    assertEquals("a", new AlternativeVote(voteLists("a,ab,ba,ba,c")).getWinner())
  }

  @Test def t13(): Unit = {
    assertEquals(Set(), new AlternativeVote(voteLists("a,ab,ba,ba,c")).invalidCandidates(candidates("abc").toSet))
  }

  @Test def t14(): Unit = {
    assertEquals(Set(candidate("d")), new AlternativeVote(voteLists("a,ab,bad,ba,c")).invalidCandidates(candidates("abc").toSet))
  }

  @Test def rnd1(): Unit = {
    def rndSeq(seed: Int) = new scala.util.Random(seed).alphanumeric.map {
      case c if c <= '9' => ','
      case c => c.toLower
    }.take(500).mkString("")

    "uoozbnhfycihpnuedkvdcugj".zipWithIndex.foreach {
      case (c, i) =>
        assertEquals(c.toString, new AlternativeVote(voteLists(rndSeq(i))).getWinner())
    }
  }
}