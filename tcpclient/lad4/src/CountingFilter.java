public interface CountingFilter {

    /** 
     * Adds a (hashed) element to the filter. After adding, count(e) will be one larger than before.
     * Complexity must be O(1)
     */
    public void add(int e);
    
    /** Version of add that adds objects */
    public void add(Object e);
    
    /**
     * Returns the number of times e has been added minus the number of times it has been removed. 
     * Complexity must be O(1)
     */
    public int count(int e);
    
    /** Version of count that counts objects */
    public int count(Object e);
    
    /** 
     * Removes a (hashed) element from the filter. Will reduce count(e) by one. 
     * You may assume that no element is removed more times than it is added (the caller needs to ensure this).
     * Complexity must be O(1) 
     */
    public void remove(int e);

    /** Version of remove that removes an object */
    public void remove(Object e);
}