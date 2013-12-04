package cs243;

public class WhileLoopLim {

    public static void main(String[] args) {
        int x = 0;
        int y = 0;
        int i = 0;
        int j = 1;
        int sum = 0;
        while(i < 5){
            x = 10;
            y = j;
            sum += x + y;
            i++;
        }
        System.out.println(sum);
    }

}
