import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class TestFilter{
    
    public static void main(String args[]){
        int numAdds = 20000;
        int capacity = 10001;
        int rangeFactor = 10;
        // Random numbers between 0 and numAdds*rangeFactor are added
        
        CountingFilter cf = new CountingFilterImpl(capacity*10, false);
        HashMap<Integer,Integer> hm = new HashMap<Integer,Integer>();
    
        Random r = new Random(123); // Use the same numbers every time
        
        for(int i = 0;i < numAdds;i++){
            int num = r.nextInt(numAdds*rangeFactor);
            cf.add(num);
            Integer cur = hm.get(num);
            if (cur == null)
                hm.put(num, 1);
            else
                hm.put(num, cur+1);
        }
        
        int totalError = 0;
        for(int x:hm.keySet()){
            int actual = hm.get(x);
            int reported = cf.count(x);
            if (actual < reported){
              totalError += reported - actual;
            } else if (actual > reported){
              System.out.println("fatal error!");
              System.out.println(x +": " + actual + " " + reported);
            }
        }
        System.out.println("Total error: "+totalError);
        System.out.println("Average error per added value: "+((double)totalError/hm.size()));
        System.out.println("Average error per add: "+((double)totalError/numAdds));
        System.out.println("Average error per value in range: "+((double)totalError/(numAdds*rangeFactor)));
        
        cf = new CountingFilterImpl(1001);
        for (int i = 1; i <= 10; i++)
            cf.add(i*1001);
        // Challenge: Can you get this to output 1? 
        // If so, does your solution prevent this situation in general or is it easy to 
        //   alter the loop above to recreate the problem? 
        // Hint: here is a clever way to be unpredictable: n = new Random(n).nextInt();
        System.out.println("Trick question: "+ cf.count(1001));
        
    }
}