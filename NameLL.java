public class NameLL {
    private Name head;
    private int numYears;
    private int[] totalBabiesPerYear;

    public NameLL(int numYears) {
        this.head = null;
        this.numYears = numYears;
        totalBabiesPerYear = new int[numYears];
        for (int i = 0; i < numYears; i++)
            totalBabiesPerYear[i] = 0;
    }

    public NameLL() {
        this.head = null;
        this.numYears = 0;
        this.totalBabiesPerYear = new int[0];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        Name current = head;
        while (current != null) {
            sb.append(current.toString());
            if (current.getNext() != null) {
                sb.append(", ");
            }
            current = current.getNext();
        }
        sb.append("");
        return sb.toString();
    }



    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        int count = 0;
        Name current = head;
        while (current != null) {
            count++;
            current = current.getNext();
        }
        return count;
    }

    public Name first() {
        return head;
    }

    public Name last() {
        if (head == null) return null;
        Name current = head;
        while (current.getNext() != null) {
            current = current.getNext();
        }
        return current;
    }

    public void insertFirst(Name newName) {
        newName.setNext(head);
        head = newName;
    }

    public void insertBack(Name newName) {
        newName.setNext(null);
        if (head == null) {
            head = newName;
            return;
        }
        Name current = head;
        while (current.getNext() != null) {
            current = current.getNext();
        }
        current.setNext(newName);
    }

    public void insertSortedAlpha(Name newName) {
        if (head == null || head.getName().compareTo(newName.getName()) > 0) {
            newName.setNext(head);
            head = newName;
            return;
        }
        Name current = head;
        while (current.getNext() != null && current.getNext().getName().compareTo(newName.getName()) < 0) {
            current = current.getNext();
        }
        newName.setNext(current.getNext());
        current.setNext(newName);
    }

    public void insertOrUpdate(String name, int yearIndex, int rank, int count) {
        if (yearIndex >= 0 && yearIndex < numYears) {
            totalBabiesPerYear[yearIndex] += count;
        }

        if (head == null) {
            head = new Name(name, numYears);
            head.updateYear(yearIndex, rank, count);
            return;
        }

        Name curr = head;
        Name prev = null;
        while (curr != null && curr.getName().compareTo(name) < 0) {
            prev = curr;
            curr = curr.getNext();
        }

        if (curr != null && curr.getName().equals(name)) {
            curr.updateYear(yearIndex, rank, count);
            return;
        }

        Name newNode = new Name(name, numYears);
        newNode.updateYear(yearIndex, rank, count);
        if (prev == null) {
            newNode.setNext(head);
            head = newNode;
        } else {
            newNode.setNext(curr);
            prev.setNext(newNode);
        }
    }

    /**
     * Return 1-indexed position of name in linked list, or -1 if not found
     */
    public int index(String name) {
        int pos = 1;
        Name curr = head;
        while (curr != null) {
            if (curr.getName().equals(name)) return pos;
            curr = curr.getNext();
            pos++;
        }
        return -1;
    }

    /**
     * yearStats returns array size 3: rank, count, percentage for given name and year
     * Returns null if no such name or no rank that year
     */
    public double[] yearStats(String name, int yearIndex) {
        if (yearIndex < 0 || yearIndex >= numYears) return null;
        Name curr = head;
        while (curr != null) {
            if (curr.getName().equals(name)) {
                int r = curr.getRank(yearIndex);
                if (r == -1) return null;
                int c = curr.getCount(yearIndex);
                double perc = totalBabiesPerYear[yearIndex] > 0 ? (double) c / totalBabiesPerYear[yearIndex] : 0;
                return new double[]{r, c, perc};
            }
            curr = curr.getNext();
        }
        return null;
    }

    /**
     * totalStats returns array size 3: total rank, total count, total percentage for name over all years
     * Rank is calculated by sorting all names by totalCount descending
     * Return null if name not found
     */
    public double[] totalStats(String name) {
        java.util.ArrayList<Name> names = new java.util.ArrayList<>();
        Name curr = head;
        while (curr != null) {
            names.add(curr);
            curr = curr.getNext();
        }

        names.sort((a, b) -> Integer.compare(b.getTotalCount(), a.getTotalCount()));

        int rank = 1;
        int lastCount = -1;
        int nameRank = -1;
        for (int i = 0; i < names.size(); i++) {
            Name n = names.get(i);
            if (lastCount != n.getTotalCount()) {
                rank = i + 1;
                lastCount = n.getTotalCount();
            }
            if (n.getName().equals(name)) {
                nameRank = rank;
                break;
            }
        }

        if (nameRank == -1) return null;

        int grandTotalBabies = 0;
        for (int tb : totalBabiesPerYear) {
            grandTotalBabies += tb;
        }

        // Get total count for this name
        int totalCount = 0;
        for (Name n : names) {
            if (n.getName().equals(name)) {
                totalCount = n.getTotalCount();
                break;
            }
        }

        double percentage = (grandTotalBabies > 0) ? (double) totalCount / grandTotalBabies : 0;

        return new double[]{nameRank, totalCount, percentage};
    }

    public int getNumYears() {
        return numYears;
    }
}
