//> using toolkit 0.9.2

package lights

@main def main(inputPath: String, outputPath: String) = {
    val input = parseInput(os.read(makePath(inputPath)))
    val output = runSimulation(input)
    os.write.over(makePath(outputPath), upickle.write(output, 2))
}

def makePath(path: String) =
    if (path(0) == '/') os.Path(path) else os.pwd / os.RelPath(path)