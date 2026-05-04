package lights

import scala.collection.immutable.Queue

case class AddVehicleCommand(vehicleId: String, startRoad: Road, endRoad: Road)
case class StepCommand()
type Command = AddVehicleCommand | StepCommand

enum Road:
    case North, East, South, West
    
    def left = this match {
        case North => East
        case East => South
        case South => West
        case West => North
    }
end Road
    
enum Light:
    case NorthSouthGreen, NorthSouthYellow, EastWestGreen, EastWestYellow
    
    private def next = Light.fromOrdinal((ordinal + 1) % Light.values.size)
    
    def step = next
end Light

case class StepStatus(leftVehicles: Seq[String]) derives upickle.Writer
case class Output(stepStatuses: Seq[StepStatus]) derives upickle.Writer

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

class State(light: Light, vehicles: Vehicles, val stepStatuses: Seq[StepStatus]) {
    def addVehicle(id: String, startRoad: Road, endRoad: Road) =
        State(
            light,
            vehicles.addVehicle(id, startRoad, endRoad),
            stepStatuses
        )

    def step() = {
        val newLight = light.step
        val (vehiclesLeft, newVehicles) = vehicles.step(newLight)
        State(newLight, newVehicles, stepStatuses :+ StepStatus(vehiclesLeft))
    }
    
}

def runSimulation(commands: Iterable[Command]) =
    val initialState = State(
        Light.EastWestYellow,
        Vehicles(Map.from(Road.values.map((_, Queue.empty)))),
        Seq.empty
    )
    Output(
        commands.foldLeft(initialState) { (state, command) =>
            command match {
                case StepCommand() => state.step()
                case AddVehicleCommand(id, startRoad, endRoad) =>
                    state.addVehicle(id, startRoad, endRoad)
            }
        }.stepStatuses
    )