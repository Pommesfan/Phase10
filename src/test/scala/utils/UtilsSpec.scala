package utils
package model
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import utils.Utils

class UtilsSpec extends AnyWordSpec {
  "make inverse index list" when {
    val indices = List(4,5,6,7)
    "returns indexes from 1 to 10 except of the ones just defined" in {
      Utils.inverseIndexList(indices, 10) should be(List(0,1,2,3,8,9))
    }
  }
}
