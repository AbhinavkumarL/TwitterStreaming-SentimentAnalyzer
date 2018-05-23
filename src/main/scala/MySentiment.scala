
object MySentiment extends Enumeration {
    type MySentiment = Value
    val POSITIVE, NEGATIVE, NEUTRAL = Value

    def toSentiment(sentiment: Int): MySentiment = sentiment match {
      case x if x == 0 || x == 1 => MySentiment.NEGATIVE
      case 2 => MySentiment.NEUTRAL
      case x if x == 3 || x == 4 => MySentiment.POSITIVE
    }
  }
