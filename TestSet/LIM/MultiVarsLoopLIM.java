package cs243;

public class MultiVarsLoopLIM {

    public static void main(String[] args) {
        int a = 1;
        int b = 2;
        int c = 3;
        int x = 0;
        for(int i = 0; i < 3; i++)
            x = a + b + c;
        System.out.println(x);
    }

}
