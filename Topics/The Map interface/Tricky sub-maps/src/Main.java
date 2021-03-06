import java.util.*;

class MapUtils {
    public static Map<Integer, String> getSubMap(TreeMap<Integer, String> map) {
        // Write your code here
        final int shift = 4;
        Map<Integer, String> res = new TreeMap<>(Collections.reverseOrder());
        int first = map.firstKey();
        int last = map.lastKey();
        if (first % 2 == 0) {
            res.putAll(map.tailMap(last - shift));
        } else {
            res.putAll(map.headMap(first + shift + 1));
        }
        return res;
    }
}

/* Do not modify code below */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        TreeMap<Integer, String> map = new TreeMap<>();
        Arrays.stream(scanner.nextLine().split("\\s")).forEach(s -> {
            String[] pair = s.split(":");
            map.put(Integer.parseInt(pair[0]), pair[1]);
        });

        Map<Integer, String> res = MapUtils.getSubMap(map);
        res.forEach((k, v) -> System.out.println(k + " : " + v));
    }
}