package hello;

public class JavaHello {
  public static String JavaHelloString = "Hello from Java!";

  public static int getSumFromKotlin(int a, int b) {
    KotlinHelloKt kot = new KotlinHelloKt(1);
    return kot.sum(a,b);
  }
}
