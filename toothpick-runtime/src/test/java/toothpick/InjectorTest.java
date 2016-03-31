package toothpick;

import org.junit.Test;
import toothpick.config.Module;
import toothpick.integration.data.Bar;
import toothpick.integration.data.Foo;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

//TODO more unit tests
public class InjectorTest extends ToothPickBaseTest {

  @Test(expected = IllegalStateException.class) public void toProvider_shoudThrowException_whenBindingIsNull() throws Exception {
    //GIVEN
    InjectorImpl injector = new InjectorImpl();

    //WHEN
    injector.toProvider(null);

    //THEN
    fail("Should not allow null bindings");
  }

  @Test public void installOverrideModules_shoudInstallOverrideBindings_whenCalledOnce() {
    //GIVEN
    Foo testFoo = new Foo();
    InjectorImpl injector = new InjectorImpl(new ProdModule());
    injector.installOverrideModules(new TestModule(testFoo));

    //WHEN
    Foo instance = injector.getInstance(Foo.class);

    //THEN
    assertThat(instance, sameInstance(testFoo));
  }

  @Test public void installOverrideModules_shoudNotInstallOverrideBindings_whenCalledWithoutTestModules() {
    //GIVEN
    InjectorImpl injector = new InjectorImpl(new ProdModule());
    injector.installOverrideModules();

    //WHEN
    Foo instance = injector.getInstance(Foo.class);

    //THEN
    assertThat(instance, notNullValue());
  }

  @Test public void installOverrideModules_shoudInstallOverrideBindingsAgain_whenCalledTwice() {
    //GIVEN
    Foo testFoo = new Foo();
    Foo testFoo2 = new Foo();
    InjectorImpl injector = new InjectorImpl(new ProdModule());
    injector.installOverrideModules(new TestModule(testFoo));
    injector.installOverrideModules(new TestModule(testFoo2));

    //WHEN
    Foo instance = injector.getInstance(Foo.class);

    //THEN
    assertThat(instance, sameInstance(testFoo2));
  }

  @Test public void installOverrideModules_shoudNotOverrideOtherBindings() {
    //GIVEN
    Foo testFoo = new Foo();
    InjectorImpl injector = new InjectorImpl(new ProdModule2());
    injector.installOverrideModules(new TestModule(testFoo));

    //WHEN
    Foo fooInstance = injector.getInstance(Foo.class);
    Bar barInstance = injector.getInstance(Bar.class);

    //THEN
    assertThat(fooInstance, sameInstance(testFoo));
    assertThat(barInstance, notNullValue());
  }

  private static class TestModule extends Module {
    public TestModule(Foo foo) {
      bind(Foo.class).to(foo);
    }
  }

  private static class ProdModule extends Module {
    public ProdModule() {
      bind(Foo.class).to(new Foo());
    }
  }

  private static class ProdModule2 extends Module {
    public ProdModule2() {
      bind(Foo.class).to(new Foo());
      bind(Bar.class).to(new Bar());
    }
  }
}
