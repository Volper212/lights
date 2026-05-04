package lights

enum Road {
    case North, East, South, West
    
    def left = this match {
        case North => East
        case East => South
        case South => West
        case West => North
    }
}
