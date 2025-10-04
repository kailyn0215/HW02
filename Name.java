public class Name {
    private String name;
    private int[] ranks;
    private int[] counts;
    private int totalCount;
    private Name next;

    public Name(String name, int numYears) {
        this.name = name;
        ranks = new int[numYears];
        counts = new int[numYears];
        for (int i = 0; i < numYears; i++) {
            ranks[i] = -1;  // -1 means no rank for this year
            counts[i] = 0;
        }
        totalCount = 0;
        next = null;
    }

    public Name(String name) {
        this(name, 0);
    }

    @Override
    public String toString() {
        return name;
    }


    public String getName() {
        return name;
    }

    public Name getNext() {
        return next;
    }

    public void setNext(Name next) {
        this.next = next;
    }

    public void updateYear(int yearIndex, int rank, int count) {
        ranks[yearIndex] = rank;
        counts[yearIndex] = count;
        totalCount += count;
    }

    public int getRank(int yearIndex) {
        return ranks[yearIndex];
    }

    public int getCount(int yearIndex) {
        return counts[yearIndex];
    }

    public int getTotalCount() {
        return totalCount;
    }
}
