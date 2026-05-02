//> using toolkit 0.9.2

package lights

@main def main(inputFileName: String, outputFileName: String) = {
    val input = parseInput(os.read(os.pwd / inputFileName))
    val output = runSimulation(input)
    os.write.over(os.pwd / outputFileName, upickle.write(output, 2))
}