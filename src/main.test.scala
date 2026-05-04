//> using test.dep org.scalatest::scalatest::3.2.20

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.Inspectors.forEvery

import lights.main

class MainSpec extends AnyFlatSpec with should.Matchers {
    it should "generate proper output" in {
        forEvery(
            os.list(os.pwd / "examples")
                .map(_.last)
                .filter(_.endsWith("input.json"))
        ) { input =>
            val actualOutput = input.dropRight("input.json".size) + "actual-output.json"
            val expectedOutput = input.dropRight("input.json".size) + "output.json"
            main(s"examples/$input", s"examples/$actualOutput")
            val actualOutputPath = os.pwd / "examples" / actualOutput
            val expectedOutputPath = os.pwd / "examples" / expectedOutput
            assertResult(os.read(expectedOutputPath))(os.read(actualOutputPath))
            os.remove(actualOutputPath)
        }
    }
}