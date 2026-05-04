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
    
    def next = Light.fromOrdinal((ordinal + 1) % Light.values.size)
end Light

case class StepStatus(leftVehicles: Seq[String]) derives upickle.Writer
case class Output(stepStatuses: Seq[StepStatus]) derives upickle.Writer

case class Vehicle(id: String, endRoad: Road)

class Vehicles(queues: Seq[Queue[Vehicle]]) {
    def addVehicle(id: String, startRoad: Road, endRoad: Road) =
        Vehicles(queues.updated(startRoad.ordinal, queues(startRoad.ordinal) :+ Vehicle(id, endRoad)))

    def step(light: Light) = letThroughVehiclesFrom(light match {
        case Light.NorthSouthGreen => handleStraightGreen(Road.South, Road.North)
        case Light.EastWestGreen => handleStraightGreen(Road.East, Road.West)
        case _ => Seq.empty
    })

    private def letThroughVehiclesFrom(roads: Seq[Road]) = {
        val (leavingVehicles, newQueues) = roads.map(road => queues(road.ordinal).dequeue).unzip
        (
            Vehicles((roads zip newQueues).foldLeft(queues) { (queue, update) =>
                val (road, newQueue) = update
                queue.updated(road.ordinal, newQueue)
            }),
            leavingVehicles.map(_.id)
        )
    }
    
    private def handleStraightGreen(frontRoad: Road, backRoad: Road) = {
        val frontQueue = queues(frontRoad.ordinal)
        val backQueue = queues(backRoad.ordinal)
        if (frontQueue.isEmpty) {
            if (backQueue.isEmpty) {
                Seq.empty
            } else {
                Seq(backRoad)
            }
        } else {
            if (backQueue.isEmpty) {
                Seq(frontRoad)
            } else {
                val isFrontTurningLeft = frontRoad.left == frontQueue.front.endRoad
                val isBackTurningLeft = backRoad.left == backQueue.front.endRoad
                if (isFrontTurningLeft == isBackTurningLeft) {
                    Seq(frontRoad, backRoad)
                } else {
                    if (isFrontTurningLeft) {
                        Seq(backRoad)
                    } else {
                        Seq(frontRoad)
                    }
                }
            }
        }
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
        val newLight = light.next
        val (newVehicles, vehiclesLeft) = vehicles.step(newLight)
        State(newLight, newVehicles, stepStatuses :+ StepStatus(vehiclesLeft))
    }
    
}

def runSimulation(commands: Iterable[Command]) =
    val initialState = State(
        Light.EastWestYellow,
        Vehicles(Seq.fill(Road.values.size)(Queue.empty)),
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