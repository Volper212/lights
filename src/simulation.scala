package lights

import scala.collection.immutable.Queue

case class AddVehicleCommand(vehicleId: String, startRoad: Road, endRoad: Road)
case class StepCommand()
type Command = AddVehicleCommand | StepCommand

case class Output(stepStatuses: Seq[StepStatus]) derives upickle.Writer

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