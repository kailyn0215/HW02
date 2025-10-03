public class NameLL {

    class Node {
        Name data;
        Node prev;
        Node next;

        Node(Name data) {
            this.data = data;
            prev = null;
            next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public NameLL() {
        head = null;
        tail = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Insert Name in alphabetical order by name string
    public void insertSorted(Name name) {
        Node newNode = new Node(name);
        if (head == null) {
            head = tail = newNode;
            size++;
            return;
        }
        Node current = head;
        while (current != null && current.data.getName().compareTo(name.getName()) < 0) {
            current = current.next;
        }
        if (current == head) {
            // Insert at head
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        } else if (current == null) {
            // Insert at tail
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        } else {
            // Insert in middle
            Node prevNode = current.prev;
            prevNode.next = newNode;
            newNode.prev = prevNode;
            newNode.next = current;
            current.prev = newNode;
        }
        size++;
    }

    // Find Name node by name string, returns null if not found
    public Name find(String name) {
        Node current = head;
        while (current != null) {
            if (current.data.getName().equalsIgnoreCase(name)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    // index(String name): 1-based position of name in list or -1
    public int index(String name) {
        Node current = head;
        int pos = 1;
        while (current != null) {
            if (current.data.getName().equalsIgnoreCase(name)) {
                return pos;
            }
            current = current.next;
            pos++;
        }
        return -1;
    }

    // yearStats(name, year): returns array rank, number, percentage or null
    // Need total babies per year passed here for percentage computation (outside)
    public double[] yearStats(String name, int year, int startYear, int[] totalBabiesPerYear) {
        Name n = find(name);
        if (n == null) return null;
        int yearIndex = year - startYear;
        if (yearIndex < 0 || yearIndex >= n.getNumYears()) return null;
        int rank = n.getRankForYear(yearIndex);
        int count = n.getCountForYear(yearIndex);
        if (rank == 0) return null; // name not ranked this year
        double percentage = 0.0;
        if (totalBabiesPerYear[yearIndex] > 0) {
            percentage = (double) count / totalBabiesPerYear[yearIndex];
        }
        return new double[] {rank, count, percentage};
    }

    // totalStats(name): returns array total rank, total number, total percentage or null
    // totalBabiesForGender: total babies over all years (passed from driver)
    public double[] totalStats(String name, int totalBabiesForGender) {
        Name n = find(name);
        if (n == null) return null;
        int totalRank = n.getTotalRank();
        int totalCount = n.getTotalCount();
        double percentage = 0.0;
        if (totalBabiesForGender > 0) {
            percentage = (double) totalCount / totalBabiesForGender;
        }
        return new double[] {totalRank, totalCount, percentage};
    }

    // Iterate nodes
    public Node getHead() {
        return head;
    }
}
