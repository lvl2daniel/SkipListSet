import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;


//You can require that list items implement Comparable.
// This means that you can compare your list items against each other by simply using their internal
// compareTo() methods.
// Randomize the height of the list items rather than using logarithm boundaries. 50% chance of being height 2,
// 25% chance of being height 3, etc.
// Implement a reBalance() method to re-randomize the height of all list items. Don’t call this automatically.
// Figure out when and how to grow and shrink the maximum height of list items. (Hint: Powers of 2, and set a
// minimum so you don’t have silly thrashing on small lists.) Expect this to be a pain in the neck and don’t leave it
// for the last minute. 


public class SkipListSet <T extends Comparable<T>> implements SortedSet<T> {


    //Store private data types of levels, size, and head which points to the topmost node hat.

    private int levels = 1;
    private int size = 0;
    private SkipListSetItem<T> head;
    
    private class SkipListSetItem<T> implements Comparable<T> {
        //Custom Item Wrapper class that stores the value of the item, the next item, the previous item, the item above, and the item below.
        private T value;
        private SkipListSetItem<T> next;
        private SkipListSetItem<T> prev;
        private SkipListSetItem<T> up;
        private SkipListSetItem<T> down;

        public SkipListSetItem(T value, SkipListSetItem<T> prev, SkipListSetItem<T> next, SkipListSetItem<T> up, SkipListSetItem<T> down){
            this.value = value;
            this.next = next;
            this.prev = prev;
            this.up = up;
            this.down = down;
        }

        // compareTo method that compares the value of the current item to the value of the item passed in.
        @Override
        public int compareTo(T compareVal) {
            return ((Comparable<T>) this.value).compareTo(compareVal);
        }

    }

    //Default constructor that creates a new head node with null values.
    public SkipListSet(){
        this.head = new SkipListSetItem<T>(null, null, null, null, null);
    }

    //Constructor that takes in a collection of items and adds them to the list.
    public SkipListSet(Collection<? extends T> list){
        this.head = new SkipListSetItem<T>(null, null, null, null, null);
        for (T item : list){
            this.add(item);
        }
    }

    //Function that adds an item to the list.
    @Override
    public boolean add(T value){
       
        //Check for duplicate value.
        if(this.contains(value)){
            return false;
        }
        // randomize the level of the new item logarithmically by 50% chance of being height 2, 25% chance of being height 3, etc.
        int level = 1;
        // have the max level be the log base 2 of the size of the list, casted to an int.
        int maxLevel = (int) (Math.log(this.size()) / Math.log(2));
        while (Math.random() < 0.5 && level < maxLevel){
            level++;
        }
        // if the new item is higher than the current max level, increase the max level
        SkipListSetItem<T> current = this.head;

        // If the list is empty, add the new item to the list at level 1.
        if(this.head.next == null && this.levels == 1){
            level = 1;
            this.levels = level;
            int i = level;
            current = new SkipListSetItem<T>(value, null, null, null, null);
            this.head.next = current;
            current.prev = this.head;
        }
        //`If the new item is lower than the current max level, increase the max level.
        else if (level > this.levels){
            while(this.levels < level){
                this.levels++;
                current.up = new SkipListSetItem<T>(null, null, null, null, current);
                current = current.up;
                this.head = current;
            }
        }

        int currentLevel = this.levels;
        //Iterate through the list in the correct fashion through the express lanes.
        while (currentLevel >= 1 && current != null){
            while(current.next != null && current.next.compareTo(value) < 0){
                current = current.next;
            }
            //If the level of the current item is less than or equal to the level of the new item, add the new item to the list.
            if(currentLevel <= level){
                SkipListSetItem<T> newItem = new SkipListSetItem<T>(value, current, current.next, null, null);
                //System.out.println("Adding " + value + " at level " + currentLevel);
                //System.out.println("Current: " + current.value);
                //System.out.println("New item: " + newItem.value);
                SkipListSetItem<T> temp = current.next;
                current.next = newItem;
                if(temp != null)
                    temp.prev = newItem;
                if(currentLevel < level){
                    SkipListSetItem<T> newCurr = current;
                    while(newCurr.up == null){
                        newCurr = newCurr.prev;
                    }
                    newCurr = newCurr.up;
                    newCurr.next.down = newItem;
                    newItem.up = newCurr.next;
                }
            }
            current = current.down;
            currentLevel--;
        }
        this.size++;
        return true;
    }

    //Function that adds a collection of items to the list.
    @Override
    public boolean addAll(Collection<? extends T> list){
        //Iterate through the list and add each item to the list.
        for (Object item : list){
            this.add((T) item);
        }
        return true;
    }

    //Function that removes an item from the list.
    @Override
    public boolean remove(Object value){
        //Check if the list is empty, if so, return false.
        if(this.head.next == null && this.levels == 1){
            System.out.println("List is empty");
            return false;
        }

        //Iterate through the list in the correct fashion through the express lanes.
        int currentLevel = this.levels;
        SkipListSetItem<T> current = this.head;
        while(currentLevel >= 1 && current != null){
            while(current.next != null && current.next.compareTo((T) value) < 0){
                current = current.next;
            }
            //If the current item is equal to the item to be removed, remove the item from the list.
            if(current.next != null && current.next.compareTo((T) value) == 0){
                SkipListSetItem<T> temp = current.next;
                current.next = temp.next;
                if(temp.next != null)
                    temp.next.prev = current;
            }
            current = current.down;
            currentLevel--;
        }

        //Decrement the size of the list and return true.
        this.size--;
        return true;
    }

    //Function that removes a collection of items from the list.
    @Override
    public boolean removeAll(Collection<?> list){
        //Iterate through the list and remove each item from the list.
        for (Object item : list){
            this.remove((T) item);
        }
        return true;
    }

    //Function that checks if the list contains an item.
    @Override
    public boolean contains(Object value){
        SkipListSetItem<T> current = this.head;
        
        int currentLevel = this.levels;
        //Iterate through the list in the correct fashion through the express lanes.
        while (currentLevel >= 1 && current != null){
            while(current.next != null && current.next.compareTo((T) value) < 0){
                current = current.next;
            }
            //If the current item is equal to the item to be removed, return true.
            if(current.next != null && current.next.compareTo((T) value) == 0){
                return true;
            }
            else{
                current = current.down;
                currentLevel--;
            }
        }
        return false;
    }

    //Function that checks if the list contains a collection of items.
    @Override
    public boolean containsAll(Collection<?> list) {
        for (Object item : list){
            if(!this.contains((T) (item))){
                return false;
            }
        }
        return true;
    }


    //Function that returns the first item in the list.
    public T first(){
        SkipListSetItem<T> current = this.head;
        while(current.down != null){
            current = current.down;
        }
        return current.next.value;
    }

    //Function that returns the last item in the list.
    public T last(){
        SkipListSetItem<T> current = this.head;
        while(current.down != null){
            current = current.down;
        }
        while(current.next != null){
            current = current.next;
        }
        return current.value;
    }

    //Function that returns an iterator for the list.
    public Iterator<T> iterator(){
        return new SkipListSetIterator<T>(this.head);
    }

    //Private class that implements the iterator interface.
    private class SkipListSetIterator<T> implements Iterator<T> {
        private SkipListSetItem<T> current;
        //Constructor that takes in the head of the list and sets the current item to the first item in the bottom row of the list.
        public SkipListSetIterator(SkipListSetItem<T> head){
            this.current = head;
            int currentLevel = levels;
            while(currentLevel > 1){
                current = current.down;
                currentLevel--;
            }
        }

        //Function that checks if the list has a next item.
        @Override
        public boolean hasNext() {
            return current.next != null;
        }

        //Function that returns the next item in the list.
        @Override
        public T next() {
            current = current.next;
            return current.value;
        }

        //Function that removes the current item from the list.
        @Override
        public void remove(){
            if(current.prev == null){
                current.next.prev = null;
            }
            else if(current.next == null){
                current.prev.next = null;
            }
            else{
            current.prev.next = current.next;
            current.next.prev = current.prev;
            }
        }
    }

    //Throw an error for these functions because they are not implemented.
    @Override
    public SortedSet<T> headSet(T e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<T> tailSet(T e){
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<T> subSet(T e, T e2) {
        throw new UnsupportedOperationException();
    }

    // create a comparator that returns null.

    public Comparator<? super T> comparator(){
        return null;
    }

    //Function that clears the list.
    @Override
    public void clear(){
        this.head = new SkipListSetItem<T>(null, null, null, null, null);
        this.levels = 1;
        this.size = 0;
    }

    //Function that checks if the list is empty.
    @Override
    public boolean isEmpty() {
        return this.head.next == null && this.levels == 1;
    }

    //Function that deletes all values in the list that are not in the collection passed in.
    @Override
    public boolean retainAll(Collection<?> list) {
         for (T item : this){
            if(!list.contains(item)){
                this.remove(item);
            }
        }
        return true;
    }

    //Function that returns the size of the list.
    @Override
    public int size() {
        return this.size;
    }

    //Function that creates an array of the bottom level of the list.
    @Override
    public Object[] toArray(Object[] arr) {
        arr = (T[]) new Object[this.size() + 1];
        SkipListSetItem<T> current = this.head;
        int i = 0;
        while(current.next != null){
            arr[i] = current.next.value;
            current = current.next;
            i++;
        }
        return arr;
    }

    //Function that creates an array of the bottom level of the list.
    @Override
    public Object[] toArray() {
        T[] arr = (T[]) new Comparable[this.size() + 1];
        SkipListSetItem<T> current = this.head;
        int i = 0;
        int currentLevel = this.levels;
        while(currentLevel > 1){
            current = current.down;
            currentLevel--;
        }
        while(current.next != null){
            arr[i] = current.next.value;
            current = current.next;
            i++;
        }
        return arr;
    }

    //Function that rebalances the list at new random heights.

    public void reBalance(){
        int currentLevel = this.levels;
        SkipListSetItem<T> current = this.head;
        while(currentLevel > 1){
            current = current.down;
            currentLevel--;
        }
        ArrayList<T> list = new ArrayList<T>();
        while(current.next != null){
            list.add(current.next.value);
            current = current.next;
        }
        this.clear();
        for (T item : list){
            this.add(item);
        }
        
    }

    //Function that checks if the bottom level of the list is equal to the bottom level of another list.
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof SkipListSet)){
            return false;
        }
        SkipListSet<T> list = (SkipListSet<T>) o;
        if(list.size() != this.size()){
            return false;
        }
        SkipListSetItem<T> current = this.head;
        SkipListSetItem<T> current2 = list.head;
        while(current.down != null){
            current = current.down;
        }
        while(current2.down != null){
            current2 = current2.down;
        }
        while(current.next != null){
            if(current.next.compareTo(current2.next.value) != 0){
                return false;
            }
            current = current.next;
            current2 = current2.next;
        }
        return true;
    }

 




 

    
}
