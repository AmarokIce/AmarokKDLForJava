package club.someoneice.kdl;

import club.someoneice.kdl.exception.KDLStyleException;
import club.someoneice.kdl.objects.KArray;
import club.someoneice.kdl.objects.KDoc;
import club.someoneice.kdl.objects.KNode.KdlTypes;
import club.someoneice.kdl.objects.KNumber;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Testable
public class KDLTest {
  @Test
  public void commentTest() throws KDLStyleException {
    System.out.println("Comment Test Start");
    final String[] comments = {
        "String",
        "String /*String*/",
        "String \\",
        "String",
        "String /* String /* String */ String",
        "String */ String /**/ String"
    };

    final List<String> list = new ArrayList<>(Arrays.asList(comments));
    final List<KArray> array = KDL.parse(list);

    assert array.get(0).get(0).getType() == KdlTypes.String;
    assert array.get(1).get(0).getType() == KdlTypes.String;

    assert array.get(0).get(0).getValue().equals("String");
    assert array.get(1).get(0).getValue().equals("String");
    assert array.get(2).get(0).getValue().equals("String");
    assert array.get(2).get(1).getValue().equals("String");
    assert array.get(3).size() == 3;
  }

  @Test
  public void annotationTest() throws KDLStyleException {
    final String[] annotations = {
        "(string)String",
        "(string string)String"
    };

    final List<String> list = new ArrayList<>(Arrays.asList(annotations));
    final List<KArray> array = KDL.parse(list);
    assert array.get(0).get(0).getType() == KdlTypes.String;
    assert array.get(1).get(0).getType() == KdlTypes.String;

    assert array.get(0).get(0).getTypeComment().equals("string");
    assert array.get(1).get(0).getTypeComment().equals("string string");

    assert array.get(0).get(0).getValue().equals("String");
    assert array.get(1).get(0).getValue().equals("String");
  }

  @Test
  public void pairTest() throws KDLStyleException {
    final String[] pairs = {
        "key=value",
        "(key)title=(value)text"
    };

    final List<String> list = new ArrayList<>(Arrays.asList(pairs));
    final List<KArray> array = KDL.parse(list);

    assert array.get(0).get(0).getType() == KdlTypes.Pair;
    assert array.get(1).get(0).getType() == KdlTypes.Pair;

    assert array.get(0).get(0).asPair().getKey().getValue().equals("key");
    assert array.get(0).get(0).asPair().getValue().getValue().equals("value");

    assert array.get(1).get(0).asPair().getKey().getTypeComment().equals("key");
    assert array.get(1).get(0).asPair().getValue().getTypeComment().equals("value");


    assert array.get(1).get(0).asPair().getKey().getValue().equals("title");
    assert array.get(1).get(0).asPair().getValue().getValue().equals("text");
  }

  @Test
  public void multipleStringTest() throws KDLStyleException {
    final File file = new File("./test/multi.kdl");
    final KDoc array = KDL.parse(file);

    assert array.get(0).get(0).getType() == KdlTypes.String;
    assert array.get(0).get(0).getValue().equals("SimpleString");
    assert array.get(1).get(0).getValue().equals("Single Line String");

    System.out.println(array.get(2, 0).getValue().toString());
    System.out.println();
    System.out.println(array.get(3, 0).getValue().toString());
    System.out.println();
    System.out.println(array.get(4, 0).getValue().toString());
    System.out.println(array.get(4, 1).getValue().toString());
    System.out.println(array.get(4, 2).getValue().toString());
    System.out.println();
    System.out.println(array.get(5, 0).getValue().toString());
    System.out.println(array.get(5, 1).getValue().toString());
    System.out.println(array.get(5, 2).getValue().toString());
    System.out.println();
    System.out.println(array.get(6, 0).getValue().toString());
    System.out.println();
    System.out.println(array.get(7, 0).asPair().getKey().getValue().toString());
    System.out.println(array.get(7, 0).asPair().getValue().getValue().toString());
  }

  @Test
  public void finalTest() throws KDLStyleException {
    final File file = new File("./test/example.kdl");
    final KDoc array = KDL.parse(file);
    assert array.get(0, 0).getType() == KdlTypes.String;
    assert array.get(0, 1).getType() == KdlTypes.String;

    assert ((KNumber) array.get(1, 2).asDocOrEmpty().get(0, 1).asTypeNode())
        .getInt() == 114514;
  }
}
