import java.lang.RuntimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map;
import java.util.Set;




class Tester {
    int tested = 0;
    int passed = 0;
    int ncase = 0;
    int equiv = 0;
    int size = 0;
    int time = 0;

    public void compare(Object result, Object expected, String msg) {
        tested += 1;
        if (result.equals(expected)) {
            passed += 1;
        } else {
            System.out.println("    Failed: " + msg + ":");
            System.out.println("      got " + result + " expected " + expected);
        }
    }
    public boolean isSatisfied(Literal lit, Map<String,Boolean> v) {
        return lit.neg() ^ v.get(lit.name());
    }
    public boolean isSatisfied(Clause cls, Map<String,Boolean> v) {
        for (Literal lit : cls)
            if (lit.isSatisfied(v))
                return true;
        return false;
    }
    public boolean isSatisfied(Cnf cnf, Map<String,Boolean> v) {
        for(Clause c : cnf)
            if (!c.isSatisfied(v))
                return false;
        return true;
    }
    public int deg(Formula f) {
        if (f instanceof Variable)
            return 0;
        int sum = 0;
        for (Formula sf : f.subf())
            sum += deg(sf);
        return sum;
    }

    public int size(Cnf cnf) {
        int sum = 0;
        for (Clause cls : cnf)
            sum += cls.size();
        return sum;
    }

    public boolean isSatisfied(Formula f, Map<String,Boolean> v) {
        if (f instanceof Variable) {
            Variable fv = (Variable) f;
            return v.get(fv.name());
        }
        else if (f instanceof Negation) {
            return !isSatisfied(f.subf()[0], v);
        }
        else if (f instanceof Conjunction) {
            for (Formula sf : f.subf())
                if (!isSatisfied(sf, v))
                    return false;
            return true;
        }
        else if (f instanceof Disjunction) {
            for (Formula sf : f.subf())
                if (isSatisfied(sf, v))
                    return true;
            return false;
        }
        else if (f instanceof Implication) {
            return !isSatisfied(f.subf()[0], v) || isSatisfied(f.subf()[1], v);
        }
        else if (f instanceof Equivalence) {
            return isSatisfied(f.subf()[0], v) == isSatisfied(f.subf()[1], v);
        }
        throw new RuntimeException("Unknown formula");

    }

    class EqEqs {
        boolean equivalent = true;
        boolean equisatisfiable;
        private boolean fSat = false;
        private boolean cSat = false;

        EqEqs(boolean eq, boolean eqsat) {
            equivalent = eq;
            equisatisfiable = eqsat;
        }
        private void checkValuations(
            Formula f, Cnf cnf,
            List<String> vars, int i, Map<String, Boolean> v
        ) {
            if (i < vars.size()) {
                v.put(vars.get(i), true);
                checkValuations(f, cnf, vars, i + 1, v);
                v.put(vars.get(i), false);
                checkValuations(f, cnf, vars, i + 1, v);
            } else {
                /*
                System.err.print("  val ");
                for(String var : v.keySet()) {
                    System.err.print(var + ": " + v.get(var) + ", ");
                }
                System.err.println();
                */
                boolean isSatF = isSatisfied(f, v);
                boolean isSatC = isSatisfied(cnf, v);
                equivalent &= isSatF == isSatC;
                fSat |= isSatF;
                cSat |= isSatC;
                /*
                System.err.println("  "
                    + " isSatF " + isSatF
                    + " isSatC " + isSatC
                    + " fSat " + fSat
                    + " cSat " + cSat
                    + " equivalent " + equivalent
                );
                */
            }
        }
        EqEqs(Formula f, Cnf cnf) {
            Set<String> fVars = f.vars();
            Set<String> cVars = cnf.vars();
            Set<String> allVars = new HashSet<String>(fVars);
            allVars.addAll(cVars);
            List<String> vars = new ArrayList<String>(allVars);
            Map<String, Boolean> v = new HashMap<String, Boolean>();
            checkValuations(f, cnf, vars, 0, v);
            equisatisfiable = fSat == cSat;
        }
    }

    public void test(Formula f) {
        ncase += 1;
        tested += 1;
        System.err.println("CASE " + ncase + ": " + f.toString());
        long start = System.nanoTime();
        Cnf cnf = f.toCnf();
        long end = System.nanoTime();
        long duration = (end - start) / 1000;

        time += duration;
        size += size(cnf);

        System.err.println("CNF: time " + duration
                + " fDeg: " + deg(f)
                + " cnf size: " + size(cnf)
        );
        System.err.println("  fVars:   " + String.join(" ", f.vars()));
        System.err.println("  cnfVars: " + String.join(" ", cnf.vars()));

        EqEqs eq = new EqEqs(f, cnf);
        if (eq.equivalent)
            equiv += 1;

        if (eq.equisatisfiable) {
            passed += 1;
            System.err.println("PASSED : equisatisfiable" +
                (eq.equivalent ? " equivalent" : ""));
        } else {
            System.err.println("FAILED:");
            System.err.println("------CNF-----");
            if (cnf.size() < 20) {
                System.err.println(cnf.toString());
            } else {
                Cnf cnfSmall = new Cnf(cnf.subList(0, 20));
                System.err.println(cnf.toString());
                System.err.println("...");
            }
            System.err.println("--------------");
        }

    }

    public boolean status() {
        System.err.println("");
        System.err.println("TESTED " + tested);
        System.err.println("PASSED " + passed);
        System.err.println("SUM(equiv) " + equiv);
        System.err.println("SUM(time) " + time);
        System.err.println("SUM(size) " + size);

        System.err.println(tested == passed ? "OK" : "ERROR" );
        return tested == passed;
    }

}

public class CnfTest {
    static Literal Lit(String n) { return Literal.Lit(n); }
    static Literal Not(String n) { return Literal.Not(n); }
    static Clause Cls(Literal... lits) { return new Clause(lits); }

    static Variable Var(String v) { return new Variable(v); }
    static Negation Not(Formula f) { return new Negation(f); }
    static Conjunction And(Formula... fs) { return new Conjunction(fs); }
    static Disjunction Or(Formula... fs) { return new Disjunction(fs); }
    static Implication Impl(Formula l, Formula r) { return new Implication(l, r); }
    static Equivalence Eq(Formula l, Formula r) { return new Equivalence(l, r); }

    public static void main(String[] args) {
        Tester t = new Tester();
        Variable a = Var("a");
        Variable b = Var("b");
        Variable c = Var("c");

        t.test(a);

        t.test(Not(a));

        t.test(
            And(
                a,
                b
            )
        );

        t.test(
            And(
                Not(a),
                a
            )
        );

        t.test(
            And(
                a,
                Not(a)
            )
        );

        t.test(
            And(
                Not(a),
                Not(a)
            )
        );

        t.test(
            Or(a, b)
        );

        t.test(
            Impl( a, b )
        );

        t.test(
            Eq( a, b )
        );

        t.test(
            Or(
                Not(Impl(a,b)),
                Not(Impl(b,a))
            )
        );

        t.test(
            And(
                Impl(a,b),
                Impl(Not(a),c)
            )
        );

        t.test(
            Eq(
                And(a,Not(b)),
                Or(a,Impl(b,a))
            )
        );

        {
            int N = 17;
            Formula[] vars = new Formula[N];
            for (int i = 0; i < 17; ++i)
                vars[i] = Var(Integer.toString(i+1));

            t.test(And(vars));

            t.test(Or(vars));
        }
        //---------------
        //
        t.test(Not(Impl(a,a)));
        t.test(Not(Impl(a,Impl(b,a))));
        t.test(
            Not(Impl(
                Impl(a,Impl(b,c)),
                Impl(
                    Impl(a,b),
                    Impl(a,c)
                )
            )));
        t.test(Not(Impl(Impl(Not(a),Not(b)),Impl(b,a))));
        t.test(Not(Impl(Not(a),Impl(a,b))));

        t.test(Not(Eq(
            Not(And(a,b)),
            Or(Not(a),Not(b))
            )));
        t.test(Not(Eq(
            Not(Or(a,b)),
            And(Not(a),Not(b))
            )));
        t.test(
            And(
                Not(Or(a,b)),
                Not(And(Not(a),Not(b)))
            ));

        t.test(And(
            Or(
                Or(a,And(b,c)),
                And(Or(a,b),Or(a,c))
            ),
            Or(
                Not(Or(a,And(b,c))),
                Not(And(Or(a,b),Or(a,c)))
            )));

        t.test(And(
            Or(
                And(a,Or(b,c)),
                Or(And(a,b),And(a,c))
            ),
            Or(
                Not(And(a,Or(b,c))),
                Not(Or(And(a,b),And(a,c)))
            )));

        t.test(And(
            Or(
                Or(a,(Impl(b,c))),
                Impl(Or(a,b),Or(a,c))
            ),
            Or(
                Not(Or(a,(Impl(b,c)))),
                Not(Impl(Or(a,b),Or(a,c)))
            )));

        t.test(And(
            Or(
                Impl(a,Impl(b,c)),
                Impl(Impl(a,b),Impl(a,c))
            ),
            Or(
                Not(Impl(a,Impl(b,c))),
                Not(Impl(Impl(a,b),Impl(a,c)))
            )));







        t.test(Not(Impl(
            Or(a,And(b,c)),
            And(Or(a,b),Or(a,c))
            )));
        t.test(Not(Impl(
            And(a,Or(b,c)),
            Or(And(a,b),And(a,c))
            )));

        t.test(Not(Impl(
            Or(a,(Impl(b,c))),
            Impl(Or(a,b),Or(a,c))
            )));
        t.test(Not(Impl(
            Impl(a,Impl(b,c)),
            Impl(Impl(a,b),Impl(a,c))
            )));

        t.test(Not(Eq(
            Or(a,And(b,c)),
            And(Or(a,b),Or(a,c))
            )));
        t.test(Not(Eq(
            And(a,Or(b,c)),
            Or(And(a,b),And(a,c))
            )));

        t.test(Not(Eq(
            Or(a,(Eq(b,c))),
            Eq(Or(a,b),Or(a,c))
            )));
        t.test(Not(Eq(
            Impl(a,Eq(b,c)),
            Eq(Impl(a,b),Impl(a,c))
            )));

        t.test(Not(Eq(
            Or(a,Or(b,c)),
            Or(Or(a,b),c)
            )));
        t.test(Not(Eq(
            And(a,And(b,c)),
            And(And(a,b),c)
            )));


        t.test(Not(Eq(
            Impl(a, Impl(b, c)),
            Impl(b, Impl(a, c))
            )));

        t.test(Not(Eq(
            And(a, b),
            And(b, a)
            )));
        t.test(Not(Eq(
            Or(a, b),
            Or(b, a)
            )));

        t.test(Not(Impl(a,Or(a,b))));

        // zabavky s prazdnymi conj/disj
        t.test(Or());
        t.test(And());
        t.test(Not(Or()));
        t.test(Not(And()));
        t.test(And(Or()));
        t.test(Or(And()));


        Formula th1 = And(
            Impl(Var("dazdnik"), Not(Var("prsi"))),
            Impl(
                Var("mokraCesta"),
                Or(  Var("prsi"), Var("umyvacieAuto")  )
            ),
            Impl(Var("umyvacieAuto"), Not(Var("vikend")))
        );

        t.test(th1);

        t.test(
                And(
                    th1,
                    Not(
                        Impl(
                            And(  Var("dazdnik"), Var("mokraCesta")  ),
                            Not(Var("vikend"))
                        )
                    )
                ));

        Formula th2 = And(
            Impl(Var("kim"), Not(Var("sarah"))),
            Impl(Var("jim"), Var("kim")),
            Impl(Var("sarah"), Var("jim")),
            Or(Var("kim"), Var("jim"), Var("sarah"))
        );

        t.test(And(th2, Not(Var("sarah"))));
        t.test(And(th2, Not(Var("kim"))));


        System.exit(t.status() ? 0 : 1);
    }
}
