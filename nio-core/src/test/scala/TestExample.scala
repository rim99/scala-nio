import org.scalatest.funspec.AnyFunSpec

class TestExample extends AnyFunSpec:
  describe("Test"){
    it("should execute"){
      val msg = "I was compiled by Scala 3. :)"
      assert("I was compiled by Scala 3. :)" == msg)
    }
  }
