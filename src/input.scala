package lights

def parseInput(json: String) = {
    val input = ujson.read(json)
    input("commands").arr.map { command =>
        command("type").str match {
            case "addVehicle" => AddVehicleCommand(
                command("vehicleId").str,
                command("startRoad").road,
                command("endRoad").road,
            )
            case "step" => StepCommand()
        }
    }
}

extension (value: ujson.Value)
    def road = value.str match {
        case "north" => Road.North
        case "east" => Road.East
        case "south" => Road.South
        case "west" => Road.West
    }
