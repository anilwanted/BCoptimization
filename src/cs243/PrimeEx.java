package cs243;

import java.math.BigInteger;
import java.util.Random;

public class PrimeEx {

	/**
	 * @param args
	 */
	 //this is just calling all teh functions in various ways
	public static void main(String[] args) {
	    final long startTime = System.currentTimeMillis();
	    
		printTest(10, 4);
		printTest(2, 2);
		printTest(54161329, 4);
		printTest(1882341361, 2);
		printTest(36, 9);

		System.out.println(isPrime(54161329) + " expect false");
		System.out.println(isPrime(1882341361) + " expect true");
		System.out.println(isPrime(2) + " expect true");
		int numPrimes = 0; 
		//Stopwatch s = new Stopwatch();
		//s.start();
		//this will find number of primes till that number 
		for(int i = 2; i < 30000000; i++) {
			if(isPrime(i)) {
				numPrimes++;
			}
		}
		//s.stop();
		//now the count of prime numbers till that number is printed
		System.out.println(numPrimes + " " + " ");
		//s.start();
		//This will find 
		//this is a another way to find the count of primes till that number and then print them
		boolean[] primes = getPrimes(30000000);
		int np = 0;
		for(boolean b : primes)
			if(b)
				np++;
		//s.stop();
		System.out.println(np + " " + " ");

		//this is one way to generate a prime number that is 1024 bits in length
		System.out.println(new BigInteger(1024, 10, new Random()));
		
		final long endTime = System.currentTimeMillis();

	//printing total time taken for all teh crap done in this function and this will be in milli seconds
	    System.out.println("Total execution time: " + (endTime - startTime));
	}

	//this is a lame way of finding prime 
	public static boolean[] getPrimes(int max) {
		//declare array 
		boolean[] result = new boolean[max + 1];
		//initialize the array all but elements at index 0 and 1
		for(int i = 2; i < result.length; i++)
			result[i] = true;
			
		final double LIMIT = Math.sqrt(max);
		//loop till the sqrt
		for(int i = 2; i <= LIMIT; i++) {
			//if index is true i.e. thinking its a prime and then u find factors then make it false.
			if(result[i]) {
				// cross out all multiples;
				int index = 2 * i;
				while(index < result.length){
					result[index] = false;
					 index += i;
				}
			}
		}
		return result;
	}


	//Just simple printing and calls the numFactors to see. 
	public static void printTest(int num, int expectedFactors) {
		//Stopwatch st = new Stopwatch();
		//st.start();
		int actualFactors = numFactors(num);
		//st.stop();
		System.out.println("Testing " + num + " expect " + expectedFactors + ", " +
				"actual " + actualFactors);
		if(actualFactors == expectedFactors)
			System.out.println("PASSED");
		else
			System.out.println("FAILED");
		//System.out.println(st.time());
	}

	// This returning if the number is prime or not
	// first finding sqrt and then loop till that to 
	// see if they are factors as of they are then its not a prime.
	public static boolean isPrime(int num) {
		assert num >= 2 : "failed precondition. num must be >= 2. num: " + num;
		final double LIMIT = Math.sqrt(num);
		//is Prime is set true if num is 2 or if num is odd number
		boolean isPrime = (num == 2) ? true : num % 2 != 0;
		int div = 3;
		//if isPrime is false ( which will happen for all even numbers ) this loop is not entered
		//all odd numbers greater than 2 will enter this loop and we will be in loop until 
		//isPrime set to false because we found a factor or till we reach the LIMI i.e. sqrt and that point
		//we exit loop with truy and return that, if we exit before that we return with false
		while(div <= LIMIT && isPrime) {
			isPrime = num % div != 0;
			div += 2;
		}
		return isPrime;
	}

	//This function is finding number of factors of the input
	//If you pass in 36, it will first find sqrt i.e 6 and then
	//in a loop till 6 will figure if other numbers are a factor or not by
	// ysing the % operator as if that output is zero it means the number in that loop is a factor
	public static int numFactors(int num) {
		assert num >= 2 : "failed precondition. num must be >= 2. num: " + num;
		int result = 0;
		final double SQRT = Math.sqrt(num);
		for(int i = 1; i < SQRT; i++) {
			if(num % i == 0) {
				result += 2;
			}
		}
		if(num % SQRT == 0)
			result++;
		return result;
	}

}
