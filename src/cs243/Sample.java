package cs243;

public class Sample {

    int global = 1;

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        for (int o = 0; o < 10000; o++) {
            int a, b, c, d, e, f, g, h, i, j, k;
            a = 2;

            b = removeUninitiailizedLocal(a);
            c = commonSubexpressionElimination();
            d = constantPropagation(a);
            e = copyPropagation(a);
            f = deadAssignmentElimination(a);
            g = constantFolding(a);
            h = conditionalBranchFolding(a);
            i = unreachableCodeElimination(a);
            j = loopInvariantMotion(a);
            k = valueRangeOptimization(a);

            System.out.println(a + b + c + d + e + f + g + h + i + j + k);
        }
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (endTime - startTime)/1000 );
    }

    public static int removeUninitiailizedLocal(int x) {

        // don't initialize f, g, h, i
        int a, b, c, d, e, f, g, h, i;
        a = x;
        b = a;
        c = a + b;
        d = a + b + c;
        e = a + b + c + d;

        System.out.println("a=" + a + " b=" + b + " c=" + c + " d=" + d + " e=" + e);
        System.out.println(a + b + c + d + e);

        return e;
    }

    public static int commonSubexpressionElimination() {
        naiveCSE();
        branchCSE();
        interleaveCSE();
        loopCSE();

        return 1;
    }

    public static int naiveCSE() {
        int a = 1;
        int b = 2;
        int x = a + b;
        int y = a + b;
        int z = a + b;
        int o = a + b;

        return x + y + z + o;
    }

    public static int branchCSE() {
        int a = 1;
        int b = 2;
        int c = 3;
        int o = a + b;

        int x = 0;
        int y = 0;
        if (c > 0)
            x = a + b;
        else
            y = a + b;

        int z = a + b;

        return x + y + z + o;
    }

    public static int interleaveCSE() {
        int a = 1;
        int b = 2;
        int x = a + b;

        a = 3;
        b = 4;
        int y = a + b;

        a = 5;
        b = 6;
        int z = a + b;

        return x + y + z;
    }

    public static int loopCSE() {
        int a = 1;
        int b = 2;
        int x = 0;

        for (int i = 0; i < 3; i++)
            x = a + b;

        int y = a + b;

        int z = 0;
        for (int i = 0; i < 3; i++)
            z = a + b;

        int o = a + b;

        return x + y + z + o;
    }

    public static int constantPropagation(int x) {

        // Simple constant propagation
        int a, b, c, d, e, f;
        a = 1;
        b = a;
        c = a + b;
        d = a + b + c;
        e = a + b + c + d;
        f = a + b + c + d + e;

        System.out.println("a=" + a + " b=" + b + " c=" + c + " d=" + d + " e=" + e + " f=" + f);
        System.out.println(a + b + c + d + e + f);

        return f;

    }

    public static int copyPropagation(int x) {

        // Simple copy propagation
        int a, b, c, d, e, f;
        a = x;
        b = a;
        c = a + b;
        d = a + b + c;
        e = a + b + c + d;
        f = a + b + c + d + e;

        System.out.println("a=" + a + " b=" + b + " c=" + c + " d=" + d + " e=" + e + " f=" + f);
        System.out.println(a + b + c + d + e + f);

        return f;

    }

    public static int deadAssignmentElimination(int x) {

        // Simple copy and constant propagation for dead assignment elimination
        int a, b, c, d, e, f;
        a = x;
        b = a;
        c = 1;
        d = c;
        e = a + b + c + d;
        f = a + b + c + d + e;

        System.out.println("a=" + a + " b=" + b + " c=" + c + " d=" + d + " e=" + e + " f=" + f);
        System.out.println(a + b + c + d + e + f);

        return f;
    }

    public static int constantFolding(int x) {
        int y = 0;
        y = x;

        return y;
    }

    public static int conditionalBranchFolding(int x) {
        int y = -10;
        //int a = x + y; 
        int z = 3;
        int a = y + z;

        if (a > 0) {
            a = 2 * a;
            System.out.println(" Value of a after first  : " + a);
        } else
            System.out.println("Value of a can't be changed at the first condition!");

        if (a > 10) {
            a = 2 * a;
            System.out.println("Value of a after 2nd condition : " + a);
        }

        if (a > 100) {
            System.out.println("Value of a after 3rd condition : " + a);
        }

        System.out.println("Final value of a : " + a);

        return a;
    }

    public static int unreachableCodeElimination(int x) {

        // This method should be applied to "conditionalBranchFolding"
        // It's hard to create unreachable code on purpose.
        int y = x;

        return y;
    }

    public static int loopInvariantMotion(int x) {
        naiveLIM();
        hoistLIM();
        depedentVariablesLIM();
        whileLoopLIM();
        multiVarsLoopLIM();
        return 1;
    }

    public static int naiveLIM() {
        int x = 0;
        for (int i = 0; i < 5; i++)
            x = 10;
        return x;
    }

    public static int[] hoistLIM() {
        int[] a = new int[100];
        int x = 1;
        int y = 2;

        for (int i = 1; i < 100; i++) {
            a[i - 1] = x + y;
        }

        return a;
    }

    public static int depedentVariablesLIM() {
        int a = 1;
        int x = 0;
        for (int i = 0; i < 3; i++) {
            a = i + 1;
            x = a;
        }
        return x;
    }

    public static int whileLoopLIM() {
        int x = 0;
        int y = 0;
        int i = 0;
        int j = 1;
        int sum = 0;
        while (i < 5) {
            x = 10;
            y = j;
            sum += x + y;
            i++;
        }
        return sum;
    }

    public static int multiVarsLoopLIM() {
        int a = 1;
        int b = 2;
        int c = 3;
        int x = 0;
        for (int i = 0; i < 3; i++)
            x = a + b + c;
        return x;
    }

    public static int valueRangeOptimization(int x) {
        for (int i = 1; i < 100; i++) {
            if (i > 0)
                System.out.println(i);
        }
        return 1;
    }

}
