package cs243;

public class HoistSample {

    public static void main(String[] args) {
        int[] a = new int[100];
        int x = 1;
        int y = 2;
        
        for (int i = 1; i < 100; i++)
        {
          a[i - 1] = x + y;
        }
    }

}
