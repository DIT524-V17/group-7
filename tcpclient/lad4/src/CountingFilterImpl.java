import javafx.collections.transformation.SortedList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CountingFilterImpl implements CountingFilter {
   
    /** Initiates a counting filter, the capacity indicates how much memory can be used
     * (memory usage must be O(capacity)) */
    public CountingFilterImpl(int capacity){
        // TODO Auto-generated method stub
        // Hint: Create an array of size capacity
    }

    public CountingFilterImpl(int number, boolean b){ // The boolean is just so it doesn't override the other constructor

	    int findPrimes = 0;

	    Collection<Integer> optimusPrime = new HashSet<>();             // Stores all existing known prime numbers.
	    optimusPrime.add(2);                                            // Add two since it's the only even prime factorization required.

	    while (++findPrimes > 0){                                       // Basically infinity loop, Complexity O(N until next prime above input).
		    boolean c = true;                                           // Checks for primes.
		    for (Integer i: optimusPrime)                               // Complexity of O(Amount of primes found previously, always less than N).
			    if (findPrimes % i == 0 && i != 1){                     // Divide it by all existing primes to identify if it's a prime.
				    c = false;                                          // Not a prime, and does not add it to the collection.
				    break;                                              // Break out of this foreach loop.
			    }
		    if (c){ // Adds it to the known primes.
			    optimusPrime.add(findPrimes++); // Increment because we want it to jump 2 steps.

			    if (findPrimes > number){// Ends the method if the prime found is bigger than the capacity number.
				    System.out.println("closest prime bigger than: " + number + " is " + --findPrimes);
				    findPrimes = -1;
			    }
		    }
	    }
    }
    

    public void add(int e) {
        // TODO Auto-generated method stub
    }

    public void add(Object e) {
        // TODO Auto-generated method stub
        // Hint: Call the other add method
    }

    public int count(int e) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int count(Object e) {
        // TODO Auto-generated method stub
        // Hint: Call the other count method
        return 0;
    }
    
    public void remove(int e) {
        // TODO Auto-generated method stub
    }

    public void remove(Object e) {
        // TODO Auto-generated method stub
        // Hint: Call the other remove method
    }
}