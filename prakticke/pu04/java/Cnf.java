import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

class Literal {
    private String name;
    private boolean neg;
    Literal(String name) {
        this.name = name;
        this.neg = false;
    }
    Literal(String name, boolean neg) {
        this.name = name;
        this.neg = neg;
    }
    public String name() {
        return name;
    }
    public boolean neg() {
        return neg;
    }
    public static Literal Lit(String name) {
        return new Literal(name);
    }
    public static Literal Not(String name) {
        return new Literal(name, true);
    }
    public static Literal Not(Literal lit) {
        return new Literal(lit.name(), !lit.neg());
    }
    public boolean isSatisfied(Map<String,Boolean> v) {
        return neg ^ v.get(name);
    }
    public String toString() {
        return (neg() ? "-" : "") + name();
    }
}

class Clause extends ArrayList<Literal> {
    Clause(Literal... lits) {
        super(Arrays.asList(lits));
    }
    Clause(Collection<? extends Literal> lits) {
        super(lits);
    }
    public boolean isSatisfied(Map<String,Boolean> v) {
        for (Literal lit : this)
            if (lit.isSatisfied(v))
                return true;
        return false;
    }
    public Set<String> vars() {
        Set<String> vs = new HashSet<String>();
        for (Literal lit : this)
            vs.add(lit.name());
        return vs;
    }
    public String toString() {
        return stream()
            .map( l -> l.toString() )
            .collect(Collectors.joining(" "))
        ;
    }
}

class Cnf extends ArrayList<Clause> {
    Cnf(Clause... cls) {
        super(Arrays.asList(cls));
    }
    Cnf(Collection<? extends Clause> cls) {
        super(cls);
    }
    public boolean isSatisfied(Map<String,Boolean> v) {
        for(Clause c : this)
            if (!c.isSatisfied(v))
                return false;
        return true;
    }
    public Set<String> vars() {
        Set<String> vs = new HashSet<String>();
        for (Clause cls : this)
            vs.addAll(cls.vars());
        return vs;
    }
    public String toString() {
        return stream()
            .map( cls -> cls.toString() + "\n" )
            .collect(Collectors.joining(""))
        ;
    }
}
