import java.lang.System;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

class ValCase {
    Map<String,Boolean> valuation;
    boolean result;

    ValCase(Map<String, Boolean> valuation, boolean result) {
        this.valuation = valuation;
        this.result    = result;
    }
}

class Case {
    Formula formula;
    String string;
    int deg;
    Set<String> vars;
    ValCase[] vals;

    Case(Formula f, String s, int d, Set<String> vars, ValCase[] vals){
        this.formula = f;
        this.string = s;
        this.deg = d;
        this.vars = vars;
        this.vals = vals;
    }
}

class FTester {
    int tested = 0;
    int passed = 0;

    public void compare(Object result, Object expected, String msg) {
        tested += 1;
        if (result.equals(expected)) {
            passed += 1;
        } else {
            System.out.println("    Failed: " + msg + ":");
            System.out.println("      got " + result + " expected " + expected);
        }
    }

    public void compare(Formula result, Formula expected, String msg) {
        tested += 1;
        if (result.equals(expected)) {
            passed += 1;
        } else {
            System.out.println("    Failed: " + msg + ":");
            System.out.println("      got " + result + " expected " + expected);
        }
    }

    public void compareInstance(Object result, Object expected, String msg) {
        tested += 1;
        if (result == expected) {
            passed += 1;
        } else {
            System.out.println("    Failed compareInstance: " + msg + ":");
            System.out.println("      got " + result + " expected " + expected);
        }
    }

    public void testToString(Case c) {
        compare(c.formula.toString(), c.string, "toString " + c.string);
    }

    public void testVars(Case c) {
        compare(c.formula.vars(), c.vars, "vars " + c.string);
    }

    public boolean status() {
        System.out.println("TESTED " + tested + "\nPASSED " + passed);
        if (tested == passed) {
            System.out.println("OK");
            return true;
        } else {
            System.out.println("ERROR");
            return false;
        }
    }
}

public class FormulaTest {

    static Variable Var(String v) { return new Variable(v); }
    static Negation Neg(Formula f) { return new Negation(f); }
    static Conjunction And(Formula[] fs) { return new Conjunction(fs); }
    static Disjunction Or(Formula[] fs) { return new Disjunction(fs); }
    static Implication Impl(Formula l, Formula r) { return new Implication(l, r); }
    static Equivalence Eq(Formula l, Formula r) { return new Equivalence(l, r); }
    static Case CS(Formula f, String s, int d, Set<String> vars, ValCase[] vals) { return new Case(f, s, d, vars, vals); }
    static ValCase Val(Map<String, Boolean> v, boolean r) { return new ValCase(v, r); }

    public static void main(String[] args) {
        FTester t = new FTester();

        Set<String> ss = new HashSet<String>(Arrays.asList("a", "b"));

        Map<String,Boolean> v1 = new HashMap<>();
        v1.put("a", true);

        Map<String,Boolean> v2 = new HashMap<>();
        v2.put("a", false);

        Map<String,Boolean> a1 = new HashMap<>();
        a1.put("a", false);
        a1.put("b", false);

        Map<String, Boolean> a2 = new HashMap<>();
        a2.put("a", false);
        a2.put("b", true);

        Map<String, Boolean> a3 = new HashMap<>();
        a3.put("a", true);
        a3.put("b", false);

        Map<String, Boolean> a4 = new HashMap<>();
        a4.put("a", true);
        a4.put("b", true);

        Map<String,Boolean> b1 = new HashMap<>();
        b1.put("a", false);
        b1.put("b", false);
        b1.put("c", false);

        Map<String,Boolean> b2 = new HashMap<>();
        b2.put("a", true);
        b2.put("b", false);
        b2.put("c", false);

        Map<String,Boolean> b3 = new HashMap<>();
        b3.put("a", false);
        b3.put("b", true);
        b3.put("c", false);

        Map<String,Boolean> b4 = new HashMap<>();
        b4.put("a", true);
        b4.put("b", true);
        b4.put("c", false);

        Map<String,Boolean> b5 = new HashMap<>();
        b5.put("a", false);
        b5.put("b", false);
        b5.put("c", true);

        Map<String,Boolean> b6 = new HashMap<>();
        b6.put("a", true);
        b6.put("b", false);
        b6.put("c", true);

        Map<String,Boolean> b7 = new HashMap<>();
        b7.put("a", false);
        b7.put("b", true);
        b7.put("c", true);

        Map<String,Boolean> b8 = new HashMap<>();
        b8.put("a", true);
        b8.put("b", true);
        b8.put("c", true);

        Case[] cases = new Case[] {
            CS(new Variable("a"), "a", 0,
                new HashSet<String>(Arrays.asList("a")),
                new ValCase[] {
                Val(v1, true), Val(v2, false)
            }),

            new Case(new Negation(new Variable("a")), "-a", 1,
                new HashSet<String>(Arrays.asList("a")),
                new ValCase[] {
                Val(v1, false), Val(v2, true) 
            }),
            new Case(new Conjunction( new Formula[] { new Variable("a"), new Variable("b") } ), "(a&b)", 1,
                new HashSet<String>(Arrays.asList("a", "b")),
                new ValCase[] {
                Val(a1, false), Val(a2, false), Val(a3, false), Val(a4, true)
            }),
            new Case(new Disjunction( new Formula[] { new Variable("a"), new Variable("b") } ), "(a|b)", 1,
                new HashSet<String>(Arrays.asList("a", "b")),
                new ValCase[] {
                Val(a1, false), Val(a2, true), Val(a3, true), Val(a4, true)
            }),
            new Case(new Implication( new Variable("a"), new Variable("b") ), "(a->b)", 1,
                new HashSet<String>(Arrays.asList("a", "b")),
                new ValCase[] {
                Val(a1, true), Val(a2, true), Val(a3, false), Val(a4, true)
            }),
            new Case(new Equivalence( new Variable("a"), new Variable("b") ), "(a<->b)", 1,
                new HashSet<String>(Arrays.asList("a", "b")),
                new ValCase[] {
                new ValCase(a1, true), new ValCase(a2, false), new ValCase(a3, false), new ValCase(a4, true)
            }),
            new Case(new Disjunction(new Formula[] {
                    new Negation(new Implication(new Variable("a"), new Variable("b"))),
                    new Negation(new Implication(new Variable("b"), new Variable("a")))
                }), "(-(a->b)|-(b->a))", 5, 
                new HashSet<String>(Arrays.asList("a", "b")),
                new ValCase[] {
                new ValCase(a1, false), new ValCase(a2, true), new ValCase(a3, true), new ValCase(a4, false)
            }),
            new Case(new Conjunction(new Formula[] {
                    new Implication(new Variable("a"), new Variable("b")),
                    new Implication(new Negation(new Variable("a")), new Variable("c"))
                }), "((a->b)&(-a->c))", 4,
                new HashSet<String>(Arrays.asList("a", "b", "c")),
                new ValCase[] {
                    new ValCase(b1, false), new ValCase(b2, false), new ValCase(b3, false),
                    new ValCase(b4, true), new ValCase(b5, true), new ValCase(b6, false),
                    new ValCase(b7, true), new ValCase(b8, true)
            }),
            new Case(new Equivalence(
                    new Conjunction(new Formula[] { new Variable("a"), new Negation(new Variable("b")) }),
                    new Disjunction(new Formula[] { new Variable("a"), new Implication( new Variable("b"), new Variable("a")) })
                ), "((a&-b)<->(a|(b->a)))", 5,
                new HashSet<String>(Arrays.asList("a", "b")),
                new ValCase[] {
                    new ValCase(a1, false), new ValCase(a2, true), new ValCase(a3, true), new ValCase(a4, false)
            }),
        };

        System.out.println("Testing toString");
        for (Case c : cases) {
            t.testToString(c);
        }


        System.out.println("Testing vars");
        for (Case c : cases) {
            t.testVars(c);
        }


        {
            System.out.println("Testing Variable.name");
            Variable a = new Variable("a");
            Variable b = new Variable("beta");
            t.compare(a.name(), "a", "Variable.name");
            t.compare(b.name(), "beta", "Variable.name");
        }

        {
            System.out.println("Testing Negation.originalFormula");
            Formula a = new Variable("a");
            Formula na = new Negation(a);
            Negation nna = new Negation(na);
            t.compareInstance(nna.originalFormula(), na, "Negation.originalFormula");
        }

        {
            System.out.println("Testing Implication rightSide / leftSide");
            Formula a = new Variable("a");
            Formula b = new Variable("b");
            Formula na = new Negation(a);
            Implication nab = new Implication(na, b);
            t.compareInstance(nab.leftSide(), na, "Implication.leftSide");
            t.compareInstance(nab.rightSide(), b, "Implication.rightSide");
        }

        {
            System.out.println("Testing Equivalence rightSide / leftSide");
            Formula a = new Variable("a");
            Formula b = new Variable("b");
            Formula na = new Negation(a);
            Equivalence nab = new Equivalence(na, b);
            t.compareInstance(nab.leftSide(), na, "Equivalence.leftSide");
            t.compareInstance(nab.rightSide(), b, "Equivalence.rightSide");
        }

        {
            System.out.println("Testing Equals");
            t.compare(Var("a").equals(Var("a")), true, "equals a a");
            t.compare(Var("a").equals(Var("b")), false, "equals a b");
            Formula a = Eq(
                    And(new Formula[] {Var("a"), Neg(Var("b"))}),
                    Or(new Formula[] {Var("a"), Impl(Var("b"), Var("a"))})
            );
            Formula b = Eq(
                    And(new Formula[] { Var("a"), Neg(Var("b"))}),
                    Or(new Formula[] {Var("a"),Impl(Var("b"),Var("a"))})
            );
            Formula c = Eq(
                    Or(new Formula[] {Var("a"),Neg(Var("b"))}),
                    Or(new Formula[] {Var("a"),Impl(Var("b"),Var("a"))})
            );
            t.compare(a.equals(a), true, "equals " + a.toString() + " " + a.toString());
            t.compare(a.equals(b), true, "equals " + a.toString() + " " + b.toString());
            t.compare(a.equals(c), false, "equals " + a.toString() + " " + c.toString());
        }

        System.exit(t.status() ? 0 : 1);
    }
}
