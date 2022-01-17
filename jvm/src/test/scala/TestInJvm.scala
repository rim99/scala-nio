import org.junit.Test
import org.junit.Assert.*

class TestInJvm:
  val msg = "I was compiled by Scala 3. :)"
  @Test def it_should_show_message_again(): Unit =
    assertEquals("I was compiled by Scala 3. :)", msg)
