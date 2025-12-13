package cs321.btree;

import java.util.LinkedList;


/*c
    K = Key
    V = Type of Value Stored
 */
public class Cache<K, V extends KeyInterface<K>> {

    int size;// Max size of the Cache
    LinkedList<V> list;
    public Cache(int size) {
        this.size = size;
        list = new LinkedList<V>();

    }

    public V get(K key) {
        // Searches the list and adds it to the front
        // Reuses code from remove() but its not worth it to call the function

        for (V item : list) {
            if(item.getKey().equals(key)) {
                list.remove(item);
                list.addFirst(item);
                return item;
            }
        }
        return null; // Else case

    }


    public V add(V value) {
        list.addFirst(value);
        if (list.size() > size) {
            return list.removeLast();
        }
        return null;
    }

    public V remove(K key) {
        for (V item : list) {
            if (item.getKey().equals(key)) {
                list.remove(item);
                return item;
            }
        }
        return null;
    }

    public void clear() {
        list.clear();
    }
}
// Could be its own but not for the sake of convenience
interface KeyInterface<K> {
    K getKey();
}
