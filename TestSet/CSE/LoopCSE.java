package cs243;

public class LoopCSE {

    public static void main(String[] args) {
        
        int a = 1;
        int b = 2;
        int x = 0;
        for(int i = 0; i < 3; i++)
            x = a + b;
        int y = a + b;
        int z = a + b;
        int o = a + b;
        System.out.println(x + y + z + o);
    }

}