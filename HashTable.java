/**
 * AUTHOR: Jah Markabawi
 * DESCRIPTION: Basic HashTable implementation
 * DATE: 08/23/2019
 */

package algorithms;

import java.security.KeyException;
import java.util.ArrayList;

class HashTable<K, V> {
    
    private int size;
    private int binSize;
    private ArrayList<HashNode<K, V>> bins;

    HashTable() 
    {
        this(50);
    }

    HashTable(int initialBinSize) 
    {
        this.size = 0;
        this.binSize = initialBinSize;
        this.bins = new ArrayList<>(initialBinSize);
    }

    public boolean isEmpty() 
    {
        return this.size == 0;
    }

    public void put(K key, V value) 
    {
        int index = getIndex(key);
        HashNode<K, V> head = this.bins.get(index);

        HashNode<K, V> newNode = new HashNode(key, value);
        newNode.next = head;
        this.bins.set(index, newNode);
    }

    public T get(K key)
    {
        int idx = this.getIndex(K);
        HashNode<K, V> curHead = this.bins.get(idx);
        while(curHead != null) {
            if(key.equals(curHead.key)) 
                return curHead.value;
            curHead = curHead.next;
        }
        throw KeyException("Key not found");
    }

    public T pop(K key)
    {
        int idx = this.getIndex(K);
        HashNode<K, V> curHead, prev = null;
        curHead = this.bins.get(idx);
        while(curHead != null) {
            if(key.equals(curHead.key)) {
                if(prev == null) this.bins.set(idx, null);
                else prev.next = null;
                return curHead.value;
            }
            prev = curHead;
            curHead = curHead.next;
        }
        throw KeyException("Key not found");
    }

    private getIndex(K key) 
    {
        int hashCode = key.hashCode();
        return hashCode % this.binSize;
    }


    private class HashNode<K, V> {

        private K key;
        private V value;

        private HashNode next;

        HashNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

}