import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DriverHW02 {
    // Data constants
    private static final int START_YEAR = 1990;
    private static final int END_YEAR = 2017;
    private static final int NUM_YEARS = END_YEAR - START_YEAR +1;
    private static final int MAX_RANK = 1000;

    private NameLL maleList = new NameLL();
    private NameLL femaleList = new NameLL();

    // Total babies per year for male and female for percentage calculation
    private int[] maleBabiesPerYear = new int[NUM_YEARS];
    private int[] femaleBabiesPerYear = new int[NUM_YEARS];

    private int totalMaleBabies = 0;
    private int totalFemaleBabies = 0;

    // Entry point
    public static void main(String[] args) throws FileNotFoundException {
        DriverHW02 driver = new DriverHW02();
        driver.run(args);
    }

    private void run(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            System.err.println("No arguments specified.");
            return;
        }

        // Parse command line args for names to lookup and files to read
        // Map gender -> list of names to find
        // Track filenames separately

        String[] maleNames = new String[args.length]; // max array size, we trim later
        int maleCount = 0;
        String[] femaleNames = new String[args.length];
        int femaleCount = 0;

        // Files start after flags and names
        int fileStartIndex = -1;

        for (int i=0; i<args.length; i++) {
            if (args[i].equals("-m") || args[i].equals("-f")) {
                if (i+1 >= args.length) {
                    System.err.println("Missing name after " + args[i]);
                    return;
                }
                if (args[i].equals("-m")) {
                    maleNames[maleCount++] = args[i+1];
                } else {
                    femaleNames[femaleCount++] = args[i+1];
                }
                i++; // skip next, it's the name
            } else {
                // first arg not a flag, assumes file start
                fileStartIndex = i;
                break;
            }
        }

        if (fileStartIndex == -1) {
            System.err.println("No data files specified.");
            return;
        }

        // Collect filenames from fileStartIndex to end
        int numFiles = args.length - fileStartIndex;
        String[] filenames = new String[numFiles];
        for (int i=0; i<numFiles; i++) {
            filenames[i] = args[fileStartIndex + i];
        }

        // Read data files and build lists
        for (String file : filenames) {
            processFile(file);
        }

        // Compute total ranks for male and female lists
        computeTotalRanks(maleList);
        computeTotalRanks(femaleList);

        // Print output for each searched name, in order input
        for (int i=0; i<maleCount; i++) {
            printStats(maleList, maleNames[i], 'm');
        }
        for (int i=0; i<femaleCount; i++) {
            printStats(femaleList, femaleNames[i], 'f');
        }
    }

    // Process a single data file
    private void processFile(String filename) throws FileNotFoundException {
        File f = new File(filename);
        if (!f.exists()) {
            System.err.println("File does not exist: " + filename);
            return;
        }
        // Extract year from filename - find the first 4-digit number in filename substrings
        int year = extractYear(filename);
        if (year < START_YEAR || year > END_YEAR) {
            System.err.println("Year out of range in file: " + filename);
            return;
        }
        int yearIndex = year - START_YEAR;

        Scanner sc = new Scanner(f);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.length() == 0) continue;
            // parse line as rank,male-name,male-number,female-name,female-number
            String[] parts = line.split(",");
            if (parts.length != 5) continue; // skip malformed line
            try {
                int rank = Integer.parseInt(parts[0].trim());
                String maleName = parts[1].trim();
                int maleCount = Integer.parseInt(parts[2].trim());
                String femaleName = parts[3].trim();
                int femaleCount = Integer.parseInt(parts[4].trim());

                // Update maleList
                updateNameList(maleList, maleName, rank, maleCount, yearIndex, 'm');

                // Update femaleList
                updateNameList(femaleList, femaleName, rank, femaleCount, yearIndex, 'f');

                // Update totals for year
                maleBabiesPerYear[yearIndex] += maleCount;
                femaleBabiesPerYear[yearIndex] += femaleCount;

            } catch (NumberFormatException e) {
                // Skip bad data line
            }
        }
        sc.close();

        // Update total babies count for gender accumulated from year
        totalMaleBabies += maleBabiesPerYear[yearIndex];
        totalFemaleBabies += femaleBabiesPerYear[yearIndex];
    }

    private int extractYear(String filename) {
        // Simple extraction: extract last 4 digits before ".csv" or anywhere in name
        // Example: names1990.csv -> 1990
        for (int i=0; i < filename.length()-4; i++) {
            String sub = filename.substring(i, i+4);
            if (sub.matches("\\d{4}")) {
                try {
                    return Integer.parseInt(sub);
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        return -1; // not found
    }

    private void updateNameList(NameLL list, String name, int rank, int count, int yearIndex, char gender) {
        if (name == null || name.length() == 0) return;
        Name n = list.find(name);
        if (n == null) {
            // Insert new Name object with yearly arrays sized for all years
            n = new Name(name, START_YEAR, NUM_YEARS);
            list.insertSorted(n);
        }
        // Update year data for rank and count (if current data for year is zero)
        // Because a rank zero means no data - only update if current rank is 0 for that year
        if (n.getRankForYear(yearIndex) == 0) {
            n.addYearData(yearIndex, rank, count);
        }
    }

    // Compute total rank for all names in a list based on totalCount (desc), tie-break alpha asc
    // Assign ranks starting at 1 for highest totalCount
    private void computeTotalRanks(NameLL list) {
        // Extract all names into an array for sorting
        int size = list.size();
        if (size == 0) return;
        NameLL.Node current = list.getHead();

        // Build array of Name objects
        Name[] namesArray = new Name[size];
        int idx = 0;
        while (current != null) {
            namesArray[idx++] = current.data;
            current = current.next;
        }

        // Sort by totalCount descending, name ascending to break ties
        java.util.Arrays.sort(namesArray, (a, b) -> {
            if (b.getTotalCount() != a.getTotalCount()) {
                return b.getTotalCount() - a.getTotalCount();
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });

        // Assign ranks, tied same totalCount -> same rank, then skip ranks accordingly
        int rank = 1;
        for (int i=0; i< namesArray.length; i++) {
            if (i == 0) {
                namesArray[i].setTotalRank(rank);
            } else {
                if (namesArray[i].getTotalCount() == namesArray[i-1].getTotalCount()) {
                    namesArray[i].setTotalRank(rank);
                } else {
                    rank = i + 1;
                    namesArray[i].setTotalRank(rank);
                }
            }
        }
    }

    // Print statistics for a given name, for specified gender list 'm' or 'f'
    private void printStats(NameLL list, String name, char gender) {
        int idx = list.index(name);
        if (idx == -1) {
            System.out.println("That name isn't in our data.");
            return;
        }

        System.out.println(idx); // index in linked list

        int[] babiesPerYear = (gender == 'm') ? maleBabiesPerYear : femaleBabiesPerYear;
        int totalBabies = (gender == 'm') ? totalMaleBabies : totalFemaleBabies;

        for (int y = START_YEAR; y <= END_YEAR; y++) {
            double[] stats = list.yearStats(name, y, START_YEAR, babiesPerYear);
            if (stats != null) {
                System.out.println(y);
                System.out.printf("%s: %d, %d, %.6f\n", name, (int)stats[0], (int)stats[1], stats[2]);
            }
        }

        double[] totalStats = list.totalStats(name, totalBabies);
        if (totalStats != null) {
            System.out.println("Total");
            System.out.printf("%s: %d, %d, %.6f\n", name, (int)totalStats[0], (int)totalStats[1], totalStats[2]);
        }
    }
}
