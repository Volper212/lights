# Lights

A simulation of intelligent traffic lights. Vehicles can enter from the north, the south, the west or the east (each of those entry points is called a *road*). Rules:
- A vehicle can only go when it sees a green light.
- A vehicle can turn left, go straight or turn right.
- When a vehicle wants to turn left, it waits for the vehicle coming from the opposite side (if there is one).
- When two vehicles want to turn left from opposite sides, they go at the same time.

The simulation is split into steps, which could be thought of as clock cycles.

The light changes from green to yellow when **both** of the following conditions are true:
- There is an empty road with a green light.
- The number of vehicles with green lights does not exceed 0.3 times the number of vehicles with red lights.

## Usage

```sh
scala-cli . --server=false -- input.json output.json
```