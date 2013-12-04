package cs243;

public class Count2sInRange {
	
public static void main(String[] args){
	System.out.println(numberOf2sInRange(20));
}
public static int numberOf2sInRange(int n){
	int count = 0;
	for(int i = 2; i <= n; i++)
		count += numberOf2s(i);
	return count;
}

public static int numberOf2s(int n){
	int count = 0;
	while(n > 0){
		if(n %10 == 2)
			count++;
		n = n/10;
	}
	return count;
}
}
