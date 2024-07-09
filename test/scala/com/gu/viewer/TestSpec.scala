import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

class TestSpec extends AnyFunSuite with Matchers {
  test("static string should match its own value") {
    val current = "two"
    current must be("two")
  }
}
