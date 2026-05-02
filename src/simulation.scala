package lights

case class StepStatus(leftVehicles: Iterable[String]) derives upickle.Writer
case class Output(stepStatuses: Iterable[StepStatus]) derives upickle.Writer

def runSimulation(commands: Iterable[Command]) =
    Output(commands.foldLeft(Seq.empty) { (stepStatuses, command) =>
        command match {
            case StepCommand() => stepStatuses :+ StepStatus(Iterable.empty)
            case _ => stepStatuses
        }
    })