import eu.aschuetz.bettercoercion.access.Accessor;
import eu.aschuetz.bettercoercion.access.AccessorRegistry;
import eu.aschuetz.bettercoercion.access.method.generic.AbstractGenericMethodAccessor;
import eu.aschuetz.bettercoercion.access.method.specific.AbstractSpecificMethodAccessor;
import eu.aschuetz.bettercoercion.api.LuaBinding;
import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.api.LuaType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BetterCoercionTest {

    @Test
    public void test() throws Exception {
        LuaCoercion bc = LuaType.coercion();

        bc.optimize(TestClass.class);

        TestClass tc = new TestClass();
        LuaValue lv = bc.coerce(tc);
        for (int i = 1; i <=73; i++) {
            TestClass.x = 0;
            Assert.assertEquals(LuaValue.NONE, lv.get(T_S[i]).invoke(lv));
            Assert.assertEquals(i, TestClass.x);

            TestClass.x = 0;
            Assert.assertEquals(LuaValue.NONE, lv.get(T_S[i]).call(lv));
            Assert.assertEquals(i, TestClass.x);

            TestClass.x = 0;
            Assert.assertEquals(LuaValue.NONE, lv.get(T_S[i]).call(lv, LuaValue.ONE));
            Assert.assertEquals(i, TestClass.x);

            TestClass.x = 0;
            Assert.assertEquals(LuaValue.NONE, lv.get(T_S[i]).invoke(lv, LuaValue.ONE));
            Assert.assertEquals(i, TestClass.x);
        }

    }

    private static final LuaValue[] T_S = new LuaValue[100];
    static {
        for (int i = 0; i < T_S.length; i++) {
            T_S[i] = LuaValue.valueOf("t" + i);
        }
    }

    public static class TestClass {

        public static int x = 0;

        public Varargs t1() {x+=1; return null;}
        public LuaValue t2()  {x+=2; return null;}
        public LuaString t3()  {x+=3; return null;}
        public LuaNumber t4()  {x+=4; return null;}
        public LuaDouble t5()  {x+=5; return null;}
        public LuaInteger t6()  {x+=6; return null;}
        public LuaUserdata t7()  {x+=7; return null;}
        public LuaTable t8()  {x+=8; return null;}
        public LuaFunction t9()  {x+=9; return null;}
        public LuaClosure t10()  {x+=10; return null;}
        public LuaBoolean t11()  {x+=11; return null;}
        public LuaNil t12()  {x+=12; return null;}

        public static Varargs t13() {x+=13; return null;}
        public static LuaValue t14()  {x+=14; return null;}
        public static LuaString t15()  {x+=15; return null;}
        public static LuaNumber t16()  {x+=16; return null;}
        public static LuaDouble t17()  {x+=17; return null;}
        public static LuaInteger t18()  {x+=18; return null;}
        public static LuaUserdata t19()  {x+=19; return null;}
        public static LuaTable t20()  {x+=20; return null;}
        public static LuaFunction t21()  {x+=21; return null;}
        public static LuaClosure t22()  {x+=22; return null;}
        public static LuaBoolean t23()  {x+=23; return null;}
        public static LuaNil t24()  {x+=24; return null;}

        public Varargs t25(LuaValue l) {x+=25; return null;}
        public LuaValue t26(LuaValue l)  {x+=26; return null;}
        public LuaString t27(LuaValue l)  {x+=27; return null;}
        public LuaNumber t28(LuaValue l)  {x+=28; return null;}
        public LuaDouble t29(LuaValue l)  {x+=29; return null;}
        public LuaInteger t30(LuaValue l)  {x+=30; return null;}
        public LuaUserdata t31(LuaValue l)  {x+=31; return null;}
        public LuaTable t32(LuaValue l)  {x+=32; return null;}
        public LuaFunction t33(LuaValue l)  {x+=33; return null;}
        public LuaClosure t34(LuaValue l)  {x+=34; return null;}
        public LuaBoolean t35(LuaValue l)  {x+=35; return null;}
        public LuaNil t36(LuaValue l)  {x+=36; return null;}

        public static Varargs t37(LuaValue l) {x+=37; return null;}
        public static LuaValue t38(LuaValue l)  {x+=38; return null;}
        public static LuaString t39(LuaValue l)  {x+=39; return null;}
        public static LuaNumber t40(LuaValue l)  {x+=40; return null;}
        public static LuaDouble t41(LuaValue l)  {x+=41; return null;}
        public static LuaInteger t42(LuaValue l)  {x+=42; return null;}
        public static LuaUserdata t43(LuaValue l)  {x+=43; return null;}
        public static LuaTable t44(LuaValue l)  {x+=44; return null;}
        public static LuaFunction t45(LuaValue l)  {x+=45; return null;}
        public static LuaClosure t46(LuaValue l)  {x+=46; return null;}
        public static LuaBoolean t47(LuaValue l)  {x+=47; return null;}
        public static LuaNil t48(LuaValue l)  {x+=48; return null;}

        public Varargs t49(Varargs l) {x+=49; return null;}
        public LuaValue t50(Varargs l)  {x+=50; return null;}
        public LuaString t51(Varargs l)  {x+=51; return null;}
        public LuaNumber t52(Varargs l)  {x+=52; return null;}
        public LuaDouble t53(Varargs l)  {x+=53; return null;}
        public LuaInteger t54(Varargs l)  {x+=54; return null;}
        public LuaUserdata t56(Varargs l)  {x+=56; return null;}
        public LuaTable t57(Varargs l)  {x+=57; return null;}
        public LuaFunction t58(Varargs l)  {x+=58; return null;}
        public LuaClosure t59(Varargs l)  {x+=59; return null;}
        public LuaBoolean t60(Varargs l)  {x+=60; return null;}
        public LuaNil t61(Varargs l)  {x+=61; return null;}

        public static Varargs t62(Varargs l) {x+=62; return null;}
        public static LuaValue t63(Varargs l)  {x+=63; return null;}
        public static LuaString t64(Varargs l)  {x+=64; return null;}
        public static LuaNumber t65(Varargs l)  {x+=65; return null;}
        public static LuaDouble t66(Varargs l)  {x+=66; return null;}
        public static LuaInteger t67(Varargs l)  {x+=67; return null;}
        public static LuaUserdata t68(Varargs l)  {x+=68; return null;}
        public static LuaTable t69(Varargs l)  {x+=69; return null;}
        public static LuaFunction t70(Varargs l)  {x+=70; return null;}
        public static LuaClosure t71(Varargs l)  {x+=71; return null;}
        public static LuaBoolean t72(Varargs l)  {x+=72; return null;}
        public static LuaNil t73(Varargs l)  {x+=73; return null;}

        public static Varargs t55 (Varargs bep) {x+=55; return null;};
    }

    @Test
    public void test2() {
        LuaCoercion bc = LuaType.coercion();
        bc.optimize(TestClass.class);

        TestClass2 tc = new TestClass2();
        LuaValue lv = bc.coerce(tc);
        Assert.assertEquals("moopboop", lv.get("beep").call(lv, LuaValue.valueOf("moop")).checkjstring());
    }

    public static class TestClass2 {

        public LuaString beep(LuaString string) {
            return string.concat(LuaValue.valueOf("boop")).checkstring();
        }
    }

    @Test
    public void test3() {
        LuaCoercion bc = LuaType.coercion();
        bc.optimize(TestClass.class);

        TestClass3 tc = new TestClass3();
        LuaValue lv = bc.coerce(tc);
        LuaTable lt = new LuaTable();
        lt.set(1, "0");
        lt.set(2, "1");

        Assert.assertEquals("1[0, 1]", lv.get("beep").call(lv, LuaValue.valueOf(1), lt).checkjstring());

        int[] i = new int[]{1};
        lt = new LuaTable();
        lt.set(1, bc.coerce(i));

        Assert.assertEquals("1[1]", lv.get("beep2").call(lv, LuaValue.valueOf(1), lt).checkjstring());

        int[][] i2 = new int[][]{{4}};

        Assert.assertEquals("Meep1[4]", lv.get("beep").call(lv, LuaValue.valueOf(1), bc.coerce(i2)).checkjstring());
    }

    public static class TestClass3 {

        public String beep(int x, int[] y) {
            return x + Arrays.toString(y);
        }

        public String beep2(int x, int[][] y) {
            return x + Arrays.toString(y[0]);
        }

        public String beep(int x, int[][] y) {
            return "Meep" + x + Arrays.toString(y[0]);
        }
    }

    @Test
    public void test4() throws Throwable {
        LuaCoercion bc = LuaType.coercion();

        TestClass4 tc = new TestClass4();
        LuaValue lv = bc.coerce(tc);
        LuaTable lt = new LuaTable();
        lt.set(1, LuaValue.valueOf(0));
        lt.set(2, LuaValue.valueOf(1));
        lt.set(3, LuaValue.valueOf(4.2));

        Assert.assertEquals("014", lv.get("beep").call(lv, lt).checkjstring());

        LuaValue collection = lv.method("boop");
        try {
            collection.method("add", LuaValue.valueOf("meer"));
            Assert.fail("Could add string to Integer collection");
        } catch (LuaError exc) {
            Assert.assertEquals("bad argument: number expected, got string", exc.getMessage());
        }

        collection.method("add", LuaValue.valueOf("1"));

        collection = lv.method("boop2");
        try {
            collection.get(1).method("add", LuaValue.valueOf("meer"));
            Assert.fail("Could add string to Integer collection");
        } catch (LuaError exc) {
            Assert.assertEquals("bad argument: number expected, got string", exc.getMessage());
        }

        collection.get(1).method("add", LuaValue.valueOf("1"));
    }

    public static class TestClass4 {

        public String beep(Collection<Integer> numbers) {
            StringBuilder sb = new StringBuilder();
            for (Integer n : numbers) {
                sb.append(n);
            }
            return sb.toString();
        }

        public Collection<Integer> boop() {
            return new ArrayList<>();
        }

        public List<List<Integer>> boop2() {
            List<List<Integer>> list =  new ArrayList<>();
            list.add(new ArrayList<Integer>());
            return list;
        }
    }

    @Test
    public void test5() {
        LuaCoercion bc = LuaType.coercion();
        LuaValue tcl = bc.coerce(TestClass5.class);
        Assert.assertEquals(8, tcl.get("blah").call(tcl, LuaValue.valueOf(2)).checkint());
        Assert.assertEquals("name", tcl.get("getName").call(tcl, LuaValue.valueOf(2)).checkjstring());
        Assert.assertEquals(TestClass5.class.getName(), tcl.get("?cgetName").call(tcl).checkjstring());
    }




    public static class TestClass5 {

        public static int blah(int x) {
            return 6+x;
        }

        public static String getName() {
            return "name";
        }
    }


    @Test
    public void test6() {
        Map<String, String> myMap = new HashMap<>();
        LuaValue map = LuaType.valueOfMap(myMap, String.class, String.class);
        map.set("Hallo", "Welt");
        Assert.assertEquals("Welt", myMap.get("Hallo"));
        myMap.put("fup", "dup");
        Assert.assertEquals("dup", map.get("fup").checkjstring());
        Assert.assertEquals("2", map.len().checkjstring());
        map.set("beep", 1);
        Assert.assertEquals("1", myMap.get("beep"));
    }

    @Test
    public void test7() {

        TC7 r = new TC7();
        LuaValue value = LuaType.getBindingFactory(TC7.class, Runnable.class).bindAsUserdata(r);

        Assert.assertTrue(value.get("blah").isnil());
        Assert.assertFalse(r.bool.get());
        value.get("run").call();
        Assert.assertTrue(r.bool.get());
    }

    private class TC7 implements Runnable {

        AtomicBoolean bool = new AtomicBoolean();

        @Override
        public void run() {
            bool.set(true);
        }

        public void blah() {
            Assert.fail();
        }
    }

    @Test
    public void test8() {
        TestClass8 tc8 = new TestClass8();
        LuaValue lv = LuaType.valueOf(tc8);
        lv.get("buup").method("add", LuaValue.valueOf(1));
        try {
            lv.get("buup").method("add", LuaValue.valueOf("burrr"));
            Assert.fail("added string to int list");
        } catch (LuaError e) {

        }

        lv.method("meep").method("add", LuaValue.valueOf(1));
        try {
            lv.method("meep").method("add", LuaValue.valueOf("burrr"));
            Assert.fail("added string to int list");
        } catch (LuaError e) {

        }

        lv.method("maap").method("add", LuaValue.valueOf(1));
        try {
            lv.method("maap").method("add", LuaValue.valueOf("burrr"));
            Assert.fail("added string to int list");
        } catch (LuaError e) {

        }

    }

    @Test
    public void test9() {
        TestClass9 tco9 = new TestClass9();
        LuaValue lv = LuaType.valueOf(tco9);
        lv.method("muup").method("add", LuaValue.valueOf(1));
        try {
            lv.method("muup").method("add", LuaValue.valueOf("burrr"));
            Assert.fail("added string to int list");
        } catch (LuaError e) {

        }


    }

    @Test
    @Ignore
    public void test9b() {
        TestClass9 tco9 = new TestClass9();
        LuaValue lv = LuaType.valueOf(tco9);
        lv.method("muup").method("vrrt", LuaValue.valueOf(1));
        try {
            tco9.muup().vrrt(1);
            LuaValue v = lv.get("muup").call(lv);


            v.get("vrrt").call(v, LuaValue.valueOf("burrr"));
            Assert.fail("added string to int list");
        } catch (LuaError e) {

        }
    }

    public static class TestClass8L<X,Y> extends ArrayList<Y> {

        public void vrrt(Y y) {
            System.out.println(y);
        }
    }

    public static class TestClass8 {
        public TestClass8L<String, Integer> buup = new TestClass8L<>();

        public TestClass8L<String, Integer> meep(String a, int b) {
            return new TestClass8L<>();
        }

        public int g(int a) {
            return 1;
        }

        public int g(Integer b) {
            return 0;
        }

        public TestClass8L<String, ? extends Number> maap() {
            return new TestClass8L<>();
        }


    }

    public static class TestClass9 {
        public TestClass8L<String, ? super Integer> muup() {
            return new TestClass8L<>();
        }
    }

    @Test
    public void test10() {
        TestClass10 tc10 = new TestClass10();
        LuaValue lv = LuaType.valueOf(tc10);
        Assert.assertTrue(lv.get("?fgo").eq_b(lv.get("go")));
        Assert.assertTrue(lv.get("test").isnil());
        Assert.assertTrue(lv.get("test2").isnil());
        Assert.assertTrue(lv.get("beep").isfunction());
        Assert.assertTrue(lv.get("?mtest2").isfunction());
        Assert.assertTrue(lv.get("?mtest3").isnil());
        Assert.assertTrue(lv.get("test3").isfunction());
        Assert.assertTrue(lv.get("class").get("make").isfunction());
        Assert.assertTrue(lv.get("class").get("new").isfunction());
        Assert.assertTrue(lv.get("class").get("make").call(LuaType.valueOf(TestClass10.class)).get("go").isnil());
        Assert.assertTrue(lv.get("class").get("new").call(LuaType.valueOf(TestClass10.class)).get("go").eq_b(LuaValue.valueOf("a")));
    }

    public static class TestClass10 {

        public String go = "a";

        public TestClass10() {

        }

        @LuaBinding(name = "make")
        public TestClass10(String go) {
            this.go = go;
        }

        @LuaBinding(visible = false)
        public void test() {}

        @LuaBinding(name = "beep")
        public void test2() {}

        @LuaBinding(reflect = false)
        public void test3() {}

    }

    @Test
    public void test11() {
        TestClass11 tc11 = new TestClass11();
        LuaValue lv = LuaType.getBindingFactory(TestClass11.class, TestClass11Iface.class).bindAsUserdata(tc11);

        Assert.assertTrue(lv.get("test").isfunction());
        Assert.assertTrue(lv.get("test2").isnil());
        Assert.assertEquals(0, tc11.i);
        lv.get("test").call();
        Assert.assertEquals(1, tc11.i);
    }

    public static class TestClass11 implements TestClass11Iface {
        int i = 0;
        public void test() {
            i++;
        };
        public void test2() {};
    }

    public interface TestClass11Iface {
        void test();
    }

    @Test
    public void testDate() {
        long t = System.currentTimeMillis();
        Date date = LuaType.coercion().coerce(LuaType.valueOf(t), Date.class);
        Assert.assertEquals(t, date.getTime());


    }

    @Test
    public void testLongPrecision() {
        Assert.assertEquals(9007199254740993L, LuaType.valueOf(9007199254740993L).checklong());
        Assert.assertEquals(-9007199254740993L, LuaType.valueOf(-9007199254740993L).checklong());
    }

    @Test
    public void testBoolean() {
        Assert.assertTrue(LuaType.coercion().coerce(LuaValue.TRUE) instanceof Boolean);
    }

    @Test
    public void testNoReflect() {
        Assert.assertTrue(LuaType.valueOf(new NoReflectT2()).get("?f?x").isnil());
        Assert.assertTrue(LuaType.valueOf(new NoReflectT2()).get("x").isint());
        Assert.assertTrue(LuaType.valueOf(new NoReflectT3()).get("?f?x").isint());
        Assert.assertTrue(LuaType.valueOf(new NoReflectT3()).get("x").isint());

        Assert.assertTrue(LuaType.valueOf(new NoReflectT2()).get("?m?blah").isnil());
        Assert.assertTrue(LuaType.valueOf(new NoReflectT2()).get("blah").isfunction());
        Assert.assertTrue(LuaType.valueOf(new NoReflectT3()).get("?m?blah").isfunction());
        Assert.assertTrue(LuaType.valueOf(new NoReflectT3()).get("blah").isfunction());
    }

    public static class NoReflectT1 {

        public int x = 2;
        public void blah() {

        }
    }

    @LuaBinding(reflect = false)
    public static class NoReflectT2 extends NoReflectT1 {

        protected int y = 2;

        protected void blub() {

        }
    }

    public static class NoReflectT3 extends NoReflectT1 {

        protected int y = 2;

        protected void blub() {

        }
    }

    @Test
    public void testAccReg() {
        LuaCoercion lc = LuaType.coercion();

        AccessorRegistry accessorRegistry = lc.getAccessorRegistry();
        Accessor acc = accessorRegistry.getAccessorsFor(TestClass8.class);
        Assert.assertTrue(acc.getMethods().get(LuaValue.valueOf("meep"))[0] instanceof AbstractGenericMethodAccessor);
        Assert.assertTrue(acc.getMethods().get(LuaValue.valueOf("maap"))[0] instanceof AbstractSpecificMethodAccessor);

        Assert.assertTrue(acc.getMethods().get(LuaValue.valueOf("g")).length == 2);
        Assert.assertTrue(acc.getMethods().get(LuaValue.valueOf("g"))[0] instanceof AbstractSpecificMethodAccessor);
        Assert.assertTrue(acc.getMethods().get(LuaValue.valueOf("g"))[1] instanceof AbstractSpecificMethodAccessor);

        LuaValue lv = lc.coerce(new TestClass8());
        Assert.assertTrue(lv.method("g", lc.coerce(1)).checkint() == 0);
        Assert.assertTrue(lv.method("g", lc.coerce(new Integer(1))).checkint() == 0);

        Assert.assertTrue(lv.method("?mg;I", lc.coerce(1)).checkint() == 1);
        Assert.assertTrue(lv.method("?mg;Ljava/lang/Integer;", lc.coerce(new Integer(1))).checkint() == 0);


    }

    @Test
    public void testFakePojo() {
        FakePojoB pka = new FakePojoB();
        LuaValue x = LuaType.coercion().coerce(pka);
        x.set("_a", "a");
        x.set("_b", new LuaTable());
        x.get("_b").set(1, new LuaTable());
        x.set("_c", 1);
        Assert.assertEquals(1, pka.getTheC());
        Assert.assertEquals(0, pka.getC()); //Field should be hidden!
        Assert.assertTrue(pka.getB().get(0) instanceof FakePojoA);

        Globals gl = JsePlatform.standardGlobals();
        gl.set("x", x);
        LuaValue res = gl.load("local t = {} for i,v in pairs(x) do t[#t+1] = i t[#t+1] = v end return t").call();
        Assert.assertFalse(res.isnil());
        Set<String> stuff = new HashSet<>();
        for (int i = 1; i <= res.rawlen(); i++) {
            stuff.add(res.get(i++).tojstring());
        }
        Assert.assertFalse(stuff.isEmpty());
        Assert.assertTrue(stuff.contains("_a"));
        Assert.assertTrue(stuff.contains("_b"));
        Assert.assertTrue(stuff.contains("_c"));
        Assert.assertTrue(stuff.contains("_d"));
        Assert.assertTrue(stuff.contains("?fc"));
        Assert.assertTrue(stuff.contains("?f?c"));
        Assert.assertTrue(stuff.contains("?fd"));
        Assert.assertTrue(stuff.contains("?f?a"));
        Assert.assertTrue(stuff.contains("?m??wait"));
        Assert.assertTrue(stuff.contains("?m??wait;"));
        Assert.assertTrue(stuff.contains("?m??wait;J"));
    }

    @Test
    public void testEnum() {
        TestEnum x = LuaType.coercion().coerce(LuaValue.valueOf("DEF"), TestEnum.class);
        Assert.assertEquals(TestEnum.DEF, x);
        TestEnum x2 = LuaType.coercion().coerce(LuaValue.valueOf("dEf"), TestEnum.class);
        Assert.assertEquals(TestEnum.DEF, x2);
        TestEnum2 x3 = LuaType.coercion().coerce(LuaValue.valueOf("DEF"), TestEnum2.class);
        Assert.assertEquals(TestEnum2.DEF, x3);
        try {
            LuaType.coercion().coerce(LuaValue.valueOf("dEf"), TestEnum2.class);
            Assert.fail("exception expected");
        } catch (LuaError e) {
            //EXPECTED
        }

        EnumT3 t3 = new EnumT3();
        LuaType.valueOf(t3).set("x", "bC");
        Assert.assertEquals(TestEnum.BC, t3.x);
    }

    @Test
    public void testTrueFalse() {
        LuaValue x = LuaType.coercion().coerce(new TFTest());
        x.method("callWithTrue", LuaValue.TRUE);
        x.method("callWithFalse", LuaValue.FALSE);
        x.method("callWithTrue", LuaValue.valueOf("abc"));
        x.method("callWithFalse", LuaValue.NIL);

    }

    public static class TFTest {
        public void callWithTrue(boolean bool) {
            Assert.assertTrue(bool);
        }


        public void callWithFalse(boolean bool) {
            Assert.assertFalse(bool);
        }
    }

    @Test
    public void testManyArguments() {
        int i = 0;
        LuaValue lv = LuaType.valueOf(new TManyArgs());

        lv.get("a").invoke(LuaValue.varargsOf(new LuaValue[]{
            lv,
            LuaValue.valueOf("a"),
            LuaValue.valueOf("b"),
            LuaValue.valueOf("c"),
            LuaValue.valueOf("d"),
        }));

        i = lv.get("b").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
                LuaValue.valueOf("d"),
                LuaValue.valueOf("e"),
        })).checkint(1);

        Assert.assertEquals(0, i);

        i = lv.get("b").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
                LuaValue.valueOf("d"),
                LuaValue.valueOf("e"),
                LuaValue.valueOf("f"),
        })).checkint(1);

        Assert.assertEquals(1, i);

        i = lv.get("b").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
                LuaValue.valueOf("d"),
                LuaValue.valueOf("e"),
                LuaValue.valueOf("f"),
                LuaValue.valueOf("g"),
        })).checkint(1);


        Assert.assertEquals(2, i);


        lv.get("class").get("c").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv.get("class"),
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
                LuaValue.valueOf("d"),
        }));

        i = lv.get("class").get("d").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv.get("class"),
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
                LuaValue.valueOf("d"),
                LuaValue.valueOf("e"),
                LuaValue.valueOf("f"),
        })).checkint(1);


        Assert.assertEquals(1, i);

        i = lv.get("class").get("d").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv.get("class"),
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
                LuaValue.valueOf("d"),
                LuaValue.valueOf("e"),
                LuaValue.valueOf("f"),
                LuaValue.valueOf("g"),
        })).checkint(1);

        Assert.assertEquals(2, i);

        i = lv.get("class").get("d").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv.get("class"),
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
                LuaValue.valueOf("d"),
                LuaValue.valueOf("e"),
        })).checkint(1);
        Assert.assertEquals(0, i);
    }

    @Test
    public void testVarArgs() {
        testVarArgsRun(LuaType.valueOf(new TVarArgs()));
    }
    @Test
    public void testVarArgsStatic() {
        testVarArgsRun(LuaType.valueOf(TVarArgsStatic.class));
    }

    public void testVarArgsRun(LuaValue lv) {
        int i = 0;

        i = lv.get("a").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
        })).checkint(1);
        Assert.assertEquals(0, i);

        i = lv.get("a").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
        })).checkint(1);
        Assert.assertEquals(1, i);

        i = lv.get("a").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
        })).checkint(1);
        Assert.assertEquals(2, i);

        i = lv.get("b").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
        })).checkint(1);
        Assert.assertEquals(0, i);

        i = lv.get("b").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
        })).checkint(1);
        Assert.assertEquals(1, i);

        i = lv.get("b").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
        })).checkint(1);
        Assert.assertEquals(2, i);

        i = lv.get("c").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
        })).checkint(1);
        Assert.assertEquals(0, i);

        i = lv.get("c").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
        })).checkint(1);
        Assert.assertEquals(1, i);

        i = lv.get("c").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
                LuaValue.valueOf("d"),
        })).checkint(1);
        Assert.assertEquals(2, i);

        i = lv.get("d").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
        })).checkint(1);
        Assert.assertEquals(0, i);

        i = lv.get("d").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
                LuaValue.valueOf("d"),
        })).checkint(1);
        Assert.assertEquals(1, i);

        i = lv.get("d").invoke(LuaValue.varargsOf(new LuaValue[]{
                lv,
                LuaValue.valueOf("a"),
                LuaValue.valueOf("b"),
                LuaValue.valueOf("c"),
                LuaValue.valueOf("d"),
                LuaValue.valueOf("e"),
        })).checkint(1);
        Assert.assertEquals(2, i);
    }

    public static class TVarArgsStatic {

        private static void assertStuff(char base, String[] args) {
            for (int i = 0; i < args.length; i++) {
                Assert.assertEquals(String.valueOf((char)(base+i)), args[i]);
            }
        }
        public static int a(String... v) {
            assertStuff('a', v);
            return v.length;
        }

        public static int b(String a, String... v) {
            Assert.assertEquals("a", a);
            assertStuff('b', v);
            return v.length;
        }

        public static int c(String a, String b, String... v) {
            Assert.assertEquals("a", a);
            Assert.assertEquals("b", b);
            assertStuff('c', v);
            return v.length;
        }

        public static int d(String a, String b, String c, String... v) {
            Assert.assertEquals("a", a);
            Assert.assertEquals("b", b);
            Assert.assertEquals("c", c);
            assertStuff('d', v);
            return v.length;
        }
    }

    public static class TVarArgs {

        private static void assertStuff(char base, String[] args) {
            for (int i = 0; i < args.length; i++) {
                Assert.assertEquals(String.valueOf((char)(base+i)), args[i]);
            }
        }
        public int a(String... v) {
            assertStuff('a', v);
            return v.length;
        }

        public int b(String a, String... v) {
            Assert.assertEquals("a", a);
            assertStuff('b', v);
            return v.length;
        }

        public int c(String a, String b, String... v) {
            Assert.assertEquals("a", a);
            Assert.assertEquals("b", b);
            assertStuff('c', v);
            return v.length;
        }

        public int d(String a, String b, String c, String... v) {
            Assert.assertEquals("a", a);
            Assert.assertEquals("b", b);
            Assert.assertEquals("c", c);
            assertStuff('d', v);
            return v.length;
        }
    }

    public static class TManyArgs {
        public void a(String a, String b, String c, String d) {
            Assert.assertEquals("a", a);
            Assert.assertEquals("b", b);
            Assert.assertEquals("c", c);
            Assert.assertEquals("d", d);
        }

        public int b(String a, String b, String c, String d, String e, String...f) {
            Assert.assertEquals("a", a);
            Assert.assertEquals("b", b);
            Assert.assertEquals("c", c);
            Assert.assertEquals("d", d);
            Assert.assertEquals("e", e);
            if (f.length > 0) {
                Assert.assertEquals("f", f[0]);
            }

            if (f.length == 2) {
                Assert.assertEquals("g", f[1]);
            }

            return f.length;
        }

        public static void c(String a, String b, String c, String d) {
            Assert.assertEquals("a", a);
            Assert.assertEquals("b", b);
            Assert.assertEquals("c", c);
            Assert.assertEquals("d", d);
        }

        public static int d(String a, String b, String c, String d, String e, String...f) {
            Assert.assertEquals("a", a);
            Assert.assertEquals("b", b);
            Assert.assertEquals("c", c);
            Assert.assertEquals("d", d);
            Assert.assertEquals("e", e);
            if (f.length > 0) {
                Assert.assertEquals("f", f[0]);
            }


            if (f.length == 2) {
                Assert.assertEquals("g", f[1]);
            }

            return f.length;
        }
    }

    public static class EnumT3 {
        public TestEnum x;
    }

    public enum TestEnum {
        A,
        BC,
        DEF,
    }

    public enum TestEnum2 {
        A,
        a,
        BC,
        DEF,
    }
}
