package hello.tests;

import hello.JavaHello;
import hello.KotlinHelloKt;
import junit.framework.TestCase;

public class HelloTest extends TestCase {
  public void testAdd() {
    KotlinHelloKt kot = new KotlinHelloKt(1);
    assertEquals(3,kot.sum(1,2));
  }
  public void testMax() {
    KotlinHelloKt kot = new KotlinHelloKt(1);
    kot.max(2,1);
  }
  public void testIncre() {
    KotlinHelloKt kot = new KotlinHelloKt(1);
    kot.incre(1);
  }
  public void testNeg() {
    KotlinHelloKt kot = new KotlinHelloKt(1);
    kot.neg(1);
  }
  public void testVoid() {
    KotlinHelloKt kot = new KotlinHelloKt(1);
    kot.vmeth(1);
  }
  public void testCons() {
    KotlinHelloKt kot = new KotlinHelloKt(1);
    kot.cons();
  }
  public void testSwitch() {
    KotlinHelloKt kot = new KotlinHelloKt(1);
    kot.switchWhen(1);
  }
}
