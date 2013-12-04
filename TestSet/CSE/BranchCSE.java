package cs243;

public class BranchCSE {

    public static void main(String[] args) {
        
        int a = 1;
        int b = 2;
        int c = 3;
        int x = 0;
        int y = 0;
        int o = a + b;
        if(c > 0)
            x = a + b;
        else
            y = a + b;
        int z = a + b;
        System.out.println(x + y + z + o);
    }

}
