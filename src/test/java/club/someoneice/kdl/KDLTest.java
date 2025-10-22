package club.someoneice.kdl;

import club.someoneice.kdl.exception.KDLStyleException;
import club.someoneice.kdl.objects.KArray;
import org.junit.jupiter.api.Test;

import java.io.File;

public class KDLTest {
  @Test
  public void commentTest() {
    final File file = new File("./example.kdl");
    final KArray array;
    try {
      array = KDL.parse(file);
    } catch (KDLStyleException e) {
      throw new RuntimeException(e);
    }

    System.out.println(array.getValue());
  }
}
