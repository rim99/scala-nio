import org.junit.Test
import org.junit.Assert.*

class TestExample:
  val msg = "I was compiled by Scala 3. :)"
  @Test def it_should_show_message(): Unit =
    assertEquals("I was compiled by Scala 3. :)", msg)
