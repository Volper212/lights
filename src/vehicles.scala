package lights

import scala.collection.immutable.Queue

case class Vehicle(id: String, startRoad: Road, endRoad: Road) {
    def isTurningLeft = startRoad.left == endRoad
}

class Vehicles(queues: Map[Road, Queue[Vehicle]]) {
    def addVehicle(id: String, startRoad: Road, endRoad: Road) =
        Vehicles(queues + (startRoad -> (queues(startRoad) :+ Vehicle(id, startRoad, endRoad))))

    def step(light: Light) = {
        val (leftVehicles, newQueues) = light match {
            case Light.NorthSouthGreen => handleStraightGreen(Road.South, Road.North)
            case Light.EastWestGreen => handleStraightGreen(Road.East, Road.West)
            case _ => (Seq.empty, queues)
        }
        (leftVehicles.map(_.id), Vehicles(newQueues))
    }
    
    def counts = queues.view.mapValues(_.size)
    
    private def handleStraightGreen(frontRoad: Road, backRoad: Road) =
        (queues(frontRoad), queues(backRoad)) match {
            case (frontVehicle +: newFrontQueue, backVehicle +: newBackQueue) =>
                (frontVehicle.isTurningLeft, backVehicle.isTurningLeft) match {
                    case (true, false) => (Seq(backVehicle), queues + (backRoad -> newBackQueue))
                    case (false, true) => (Seq(frontVehicle), queues + (frontRoad -> newFrontQueue))
                    case _ => (Seq(frontVehicle, backVehicle), queues + (frontRoad -> newFrontQueue) + (backRoad -> newBackQueue))
                }
            case (frontVehicle +: newFrontQueue, _) => (Seq(frontVehicle), queues + (frontRoad -> newFrontQueue))
            case (_, backVehicle +: newBackQueue) => (Seq(backVehicle), queues + (backRoad -> newBackQueue))
            case _ => (Seq.empty, queues)
        }
}