package lights

case class StepStatus(leftVehicles: Seq[String]) derives upickle.Writer

class State(light: Light, vehicles: Vehicles, val stepStatuses: Seq[StepStatus]) {
    def addVehicle(id: String, startRoad: Road, endRoad: Road) =
        State(
            light,
            vehicles.addVehicle(id, startRoad, endRoad),
            stepStatuses
        )

    def step() = {
        val newLight = light.step(vehicles.counts)
        val (vehiclesLeft, newVehicles) = vehicles.step(newLight)
        State(newLight, newVehicles, stepStatuses :+ StepStatus(vehiclesLeft))
    }
}