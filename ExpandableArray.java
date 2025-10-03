public class ExpandableArray {
    private int[] arr;
    private int size;

    public ExpandableArray() {
        arr = new int[10];
        size = 0;
    }

    public void add(int val) {
        if (size >= arr.length) {
            int[] newArr = new int[arr.length * 2];
            for (int i=0; i<arr.length; i++) {
                newArr[i] = arr[i];
            }
            arr = newArr;
        }
        arr[size++] = val;
    }

    public int get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return arr[index];
    }

    public int size() {
        return size;
    }
}
