import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DriverHW02 {

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 1) {
            System.err.println("Usage: java DriverHW02 [-m maleName] [-f femaleName] file1 [file2 ...]");
            return;
        }

        String[] maleNames = new String[args.length];
        int maleCount = 0;
        String[] femaleNames = new String[args.length];
        int femaleCount = 0;
        String[] files = new String[args.length];
        int fileCount = 0;
        boolean maleFirst = false;

        boolean filesStarted = false;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!filesStarted && (arg.equals("-f") || arg.equals("-m"))) {
                if (i + 1 >= args.length) {
                    System.err.println("Missing name after " + arg);
                    return;
                }
                String name = args[i + 1];
                if (name.startsWith("-")) {
                    System.err.println("Invalid name after " + arg);
                    return;
                }
                if (arg.equals("-m")) {
                    maleNames[maleCount++] = name;
                    if (i == 0) {
                        maleFirst = true;
                    }
                } else {
                    femaleNames[femaleCount++] = name;
                }
                i++;
            } else {
                filesStarted = true;
                files[fileCount++] = arg;
            }
        }

        if (fileCount == 0) {
            System.err.println("No input files specified.");
            return;
        }

        int startYear = 1990;
        int endYear = 2017;
        int numYears = endYear - startYear + 1;

        NameLL maleList = new NameLL(numYears);
        NameLL femaleList = new NameLL(numYears);

        for (int i = 0; i < fileCount; i++) {
            String filename = files[i];
            int year = extractYear(filename);
            if (year < startYear || year > endYear) {
                System.err.println("File year out of expected range: " + filename);
                return;
            }
            int yearIndex = year - startYear;

            Scanner sc = new Scanner(new File(filename));
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length != 5) {
                    System.err.println("Invalid line: " + line);
                    continue;
                }

                int rank;
                int maleCountNum;
                int femaleCountNum;
                if (isInteger(parts[0]) && isInteger(parts[2]) && isInteger(parts[4])) {
                    rank = Integer.parseInt(parts[0]);
                    maleCountNum = Integer.parseInt(parts[2]);
                    femaleCountNum = Integer.parseInt(parts[4]);
                } else {
                    System.err.println("Invalid number format in line: " + line);
                    continue;
                }

                String maleName = parts[1];
                String femaleName = parts[3];

                maleList.insertOrUpdate(maleName, yearIndex, rank, maleCountNum);
                femaleList.insertOrUpdate(femaleName, yearIndex, rank, femaleCountNum);
            }
            sc.close();
        }

        if(maleFirst) {
            for (int i = 0; i < maleCount; i++) {
                        printStats(maleList, maleNames[i], startYear);
            }
            for (int i = 0; i < femaleCount; i++) {
                    printStats(femaleList, femaleNames[i], startYear);
            }
        }
        else {
            
            for (int i = 0; i < femaleCount; i++) {
                    printStats(femaleList, femaleNames[i], startYear);
            }
            for (int i = 0; i < maleCount; i++) {
                        printStats(maleList, maleNames[i], startYear);
            }
        }
        

        
    }

    private static boolean isInteger(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    private static int extractYear(String filename) {
        String digits = "";
        for (int i = 0; i < filename.length(); i++) {
            char c = filename.charAt(i);
            if (Character.isDigit(c)) digits += c;
        }
        if (digits.length() == 4) {
            if (isInteger(digits)) {
                return Integer.parseInt(digits);
            }
        }
        return -1;
    }

    private static void printStats(NameLL list, String name, int startYear) {
        int idx = list.index(name);
        if (idx == -1) {
            System.out.println("That name isn't in our data.");
            return;
        }
        System.out.println(idx);

        int numYears = list.getNumYears();
        boolean printedYear = false;
        for (int yearIndex = 0; yearIndex < numYears; yearIndex++) {
            double[] stats = list.yearStats(name, yearIndex);
            if (stats != null) {
                printedYear = true;
                System.out.println(startYear + yearIndex);
                System.out.printf("%s: %.0f, %.0f, %.6f\n", name, stats[0], stats[1], stats[2]);
            }
        }

        if (!printedYear) {
            System.out.println("No yearly data found for " + name);
        }

        double[] total = list.totalStats(name);
        if (total != null) {
            System.out.println("Total");
            System.out.printf("%s: %.0f, %.0f, %.6f\n", name, total[0], total[1], total[2]);
        }
    }
}
