import org.scalatest.funspec.AnyFunSpec

class TestInJvm extends AnyFunSpec:
  describe("Test"){
    it("should also execute"){
      val msg = "I was compiled by Scala 3. :)"
      assert("I was compiled by Scala 3. :)" == msg)
    }
  }
    
