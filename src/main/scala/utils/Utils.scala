package utils

import scala.util.Random

object Utils {
  def inverseIndexList(indexList:List[Int], maxIndex:Int): List[Int] =
    var new_index_list = List[Int]()
    (0 until maxIndex).foreach(i =>
      if(!indexList.contains(i))
        new_index_list = i::new_index_list
    )
    new_index_list.reverse
}
