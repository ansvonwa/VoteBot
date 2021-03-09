import AlternativeVote.{Candidate, candidate}
import org.junit.Test
import org.junit.Assert._

class TestIterativeAlternativeVote {
  /**
   * turns "ab,ac,bcd" into Set(Seq(a, b), Seq(a, c), Seq(b, c, d))
   */
  def voteLists(s: String): Seq[Seq[Candidate]] =
    s.split(",").toSeq
      .map(candidates)

  def candidates(s: String): Seq[Candidate] =
    s.map((c: Char) => candidate(c.toString)).toSeq

  @Test def rnd1(): Unit = {
    def rndSeq(seed: Int) = scala.util.Random(seed).alphanumeric.map {
      case c if c <= '9' => ','
      case c => c.toLower
    }.take(500).mkString("")

    assertEquals("uwznvfkxhberyiapojgdsqtcml", IterativeAlternativeVote(voteLists(rndSeq(0))).winners.mkString(""))
    assertEquals("opurhbqnmsifavzdetlkyxgcwj", IterativeAlternativeVote(voteLists(rndSeq(1))).winners.mkString(""))
    assertEquals("ochwdlfevtrismpgyzjxuqnakb", IterativeAlternativeVote(voteLists(rndSeq(2))).winners.mkString(""))
    assertEquals("zcxhyswvkptmeqrgaunfobijdl", IterativeAlternativeVote(voteLists(rndSeq(3))).winners.mkString(""))
    assertEquals("bjyihrszwdlkafemqgonuvxtcp", IterativeAlternativeVote(voteLists(rndSeq(4))).winners.mkString(""))
    assertEquals("ngpsozcfjwlimtxdeqvyrbahuk", IterativeAlternativeVote(voteLists(rndSeq(5))).winners.mkString(""))
    assertEquals("hiwobzfdypuvrkatgjexlnsqcm", IterativeAlternativeVote(voteLists(rndSeq(6))).winners.mkString(""))
    assertEquals("fdtkiouzxapmrenbwhylgjcsvq", IterativeAlternativeVote(voteLists(rndSeq(7))).winners.mkString(""))
  }

  @Test def rnd2(): Unit = {
    def rndSeq(seed: Int) = scala.util.Random(seed).alphanumeric.map {
      case c if c <= '9' => ','
      case c => c.toLower
    }.take(50).mkString("")

    assertEquals("wczhfuntrvi", IterativeAlternativeVote(voteLists(rndSeq(0))).winners.take(11).mkString(""))
    assertEquals("ihqzynavubos", IterativeAlternativeVote(voteLists(rndSeq(1))).winners.take(12).mkString(""))
    assertEquals("arhi", IterativeAlternativeVote(voteLists(rndSeq(2))).winners.take(4).mkString(""))
    assertEquals("ziwpco", IterativeAlternativeVote(voteLists(rndSeq(3))).winners.take(6).mkString(""))
    assertEquals("rws", IterativeAlternativeVote(voteLists(rndSeq(4))).winners.take(3).mkString(""))
    assertEquals("fpqrc", IterativeAlternativeVote(voteLists(rndSeq(5))).winners.take(5).mkString(""))
  }
}
