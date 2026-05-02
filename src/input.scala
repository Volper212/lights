package lights

case class AddVehicleCommand(vehicleId: String, startRoad: String, endRoad: String)
case class StepCommand()
type Command = AddVehicleCommand | StepCommand

def parseInput(json: String) = {
    val input = ujson.read(json)
    input("commands").arr.map { command =>
        command("type").str match {
            case "addVehicle" => AddVehicleCommand(
                command("vehicleId").str,
                command("startRoad").str,
                command("endRoad").str,
            )
            case "step" => StepCommand()
        }
    }
}
