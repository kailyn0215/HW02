public class Name {
    private String name;
    private int[] yearlyRanks;     // Index = year - startYear, value = rank (int), 0 if not ranked that year
    private int[] yearlyCounts;    // Index = year - startYear, value = number of babies with this name that year
    private int startYear;         // earliest year data covers
    private int numYears;          // total number of years data covers

    private int totalCount;        // total babies for this name over all years
    private int totalRank;         // total rank (computed later)

    public Name(String name, int startYear, int numYears) {
        this.name = name;
        this.startYear = startYear;
        this.numYears = numYears;
        yearlyRanks = new int[numYears];
        yearlyCounts = new int[numYears];
        totalCount = 0;
        totalRank = -1;
    }

    public String getName() {
        return name;
    }

    // Add or update rank and count for given year index (year - startYear)
    public void addYearData(int yearIndex, int rank, int count) {
        yearlyRanks[yearIndex] = rank;
        yearlyCounts[yearIndex] = count;
        totalCount += count;
    }

    public int getRankForYear(int yearIndex) {
        return yearlyRanks[yearIndex];
    }

    public int getCountForYear(int yearIndex) {
        return yearlyCounts[yearIndex];
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalRank(int rank) {
        this.totalRank = rank;
    }

    public int getTotalRank() {
        return totalRank;
    }

    public int getNumYears() {
        return numYears;
    }

    public int getStartYear() {
        return startYear;
    }
}
