package lights

import scala.collection.MapView
    
enum Light {
    case NorthSouthGreen, NorthSouthYellow, EastWestGreen, EastWestYellow

    private val SwitchProportion = .3
    
    private def next = Light.fromOrdinal((ordinal + 1) % Light.values.size)
    
    def step(vehicleCounts: MapView[Road, Int]) = if (shouldSwitch(vehicleCounts)) next else this
    
    private def shouldSwitch(vehicleCounts: MapView[Road, Int]) = this match {
        case NorthSouthGreen => shouldSwitchStraightGreen(vehicleCounts(Road.South), vehicleCounts(Road.North), vehicleCounts(Road.West), vehicleCounts(Road.East))
        case EastWestGreen => shouldSwitchStraightGreen(vehicleCounts(Road.East), vehicleCounts(Road.West), vehicleCounts(Road.South), vehicleCounts(Road.North))
        case _ => true
    }
    
    private def shouldSwitchStraightGreen(frontCount: Int, backCount: Int, leftCount: Int, rightCount: Int) =
        (frontCount == 0 || backCount == 0) && frontCount + backCount <= SwitchProportion * (leftCount + rightCount)
}