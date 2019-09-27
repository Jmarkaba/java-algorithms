import java.util.Comparator;

class Heap<T> 
{
    protected int maxSize;
    protected int heapSize;
    protected T[] data;

    // Constructors
    Heap() { this(50); }
    Heap(int size) { 
        this.heapSize = 0;
        this.maxSize = size;
        this.data = new T[size];
    }
    Heap(T[] array) { 
        this.maxSize = array.length;
        this.heapSize = array.length;
        this.data = array;
    }
    Heap(T[] array, int heapSize) { 
        this.maxSize = array.length;
        this.heapSize = heapSize;
        this.data = array;
    }

    public int size() { return this.heapSize; }

    public static <T> void HeapSort(T[] arr) { HeapSort(arr, 0, arr.length); }
    public static <T> void HeapSort(T[] array, int start, int segmentSize) 
    {
        heap = Heap<T>(segmentSize);
        for(int i = segmentSize/2; i > 0; --i) {
            heap.heapify(i);
        }
        
        for(int j = 0; j < segmentSize; ++j) {
            swap(array, j, heap.heapSize-1);
            --heap.heapSize;
        }
    }

    // Private Methods
    private void heapify(int index, Comparator<T> comp) 
    {
        int li = this.LEFT(index);
        if(li < this.maxSize) return;
        int ri = this.RIGHT(index);

        int qualParent = index;

        if(comp.compare(this.data[index], this.data[li]) < 0) qualParent = li;
        if(comp.compare(this.data[qualParent], this.data[ri]) < 0) qualParent = ri;

        if(qualParent != index)
            this.heapify(qualParent, comp);
    }

    private static <T> void swap(T[] array, int i, int j) 
    {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    // For heap indexing
    private static LEFT(int index) { return 2*index + 1; }
    private static RIGHT(int index) { return 2*index + 2; }
    private static PARENT(int index) { return (index-1)/2; }
}