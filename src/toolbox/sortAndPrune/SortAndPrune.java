package toolbox.sortAndPrune;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SortAndPrune {

    private class Pair {

        public int box0;
        public int box1;

        public Pair(int b0, int b1) {
            this.box0 = b0;
            this.box1 = b1;
        }
    }

    private List<Box> boxes = new ArrayList<>();
    private List<EndPoint>[] endPoints;
    public List<Pair>[] intersectingPairs = new ArrayList[3];
    public List<Pair> fullIntersects = new ArrayList<>();

    public SortAndPrune() {
        boxes.add(new Box(1, new EndPoint(1, 0, true), new EndPoint(1, 3, false)));
        boxes.add(new Box(2, new EndPoint(2, 1, true), new EndPoint(2, 4, false)));
        boxes.add(new Box(3, new EndPoint(3, 10, true), new EndPoint(3, 12, false)));
        boxes.add(new Box(4, new EndPoint(4, 9, true), new EndPoint(4, 11, false)));
        boxes.add(new Box(5, new EndPoint(5, 9, true), new EndPoint(5, 11, false)));
    }

    public void update(List<Box> sort) {
        fullIntersects.clear();
        List<Box> sortBoxes = new ArrayList<>(sort);

        for (int i = 0; i < 3; i++) {
            sortAlongAxis(sortBoxes, i);
            intersectingPairs[i] = findPairsAlongAxis(sortBoxes, i);
        }

        List<Pair> xyIntersects = new ArrayList<>();
        for (Pair pair : intersectingPairs[0]) {
            if (pairExistsInList(intersectingPairs[2], pair)) {
                xyIntersects.add(pair);
//                fullIntersects.add(pair);
            }
        }
        for (Pair pair : xyIntersects) {
            if (pairExistsInList(intersectingPairs[2], pair)) {
                fullIntersects.add(pair);
            }
        }
    }

    public List<Integer> findFullIntersects(int searchId) {
        List<Integer> matches = new ArrayList<>();
        for (Pair pair: fullIntersects) {
            if (pair.box0 == searchId) {
                matches.add(pair.box1);
            } else if (pair.box1 == searchId) {
                matches.add(pair.box1);
            }
        }
        return matches;
    }

    private boolean pairExistsInList(List<Pair> pairs, Pair pair) {
        for (Pair p : pairs) {
            if (p.box0 == pair.box0 && p.box1 == pair.box1) {
                return true;
            } else if (p.box0 == pair.box1 && p.box1 == pair.box0) {
                return true;
            }
        }
        return false;
    }

    private void sortAlongAxis(List<Box> sortList, int axis) {
        sortList.sort((o1, o2) -> {
            if (o1.min[axis].value == o2.min[axis].value) {
                return 0;
            }
            return o1.min[axis].value < o2.min[axis].value ? -1 : 1;
        });
    }

    private List<Pair> findPairsAlongAxis(List<Box> sortList, int axis) {
        List<Pair> pairList = new ArrayList<>();
        for (int i = 0; i < sortList.size(); i++) {
            for (int j = i+1; j < sortList.size(); j++) {
                if (sortList.get(i).max[axis].value > sortList.get(j).min[axis].value) {
                    pairList.add(new Pair(sortList.get(i).id, sortList.get(j).id));
                }
            }
        }

        return pairList;
    }

    private void render(int axis) {
        for (Box box : boxes) {
            for (int i = 0; i < (int) box.min[axis].value; i++) {
                System.out.print(" ");
            }
            for (int i = 0; i < (int) (box.max[axis].value - box.min[axis].value); i++) {
                System.out.print("-");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("*******************************");
//        for (int i = 0; i < intersectingPairs[axis].size(); i++) {
//            System.out.print("["+intersectingPairs[axis].get(i).box0+","+intersectingPairs[axis].get(i).box1+"] ");
//        }
        for (int i = 0; i < fullIntersects.size(); i++) {
            System.out.print("["+fullIntersects.get(i).box0+","+fullIntersects.get(i).box1+"] ");
        }
        System.out.println();
        System.out.println("*******************************");
    }

    private Box getBoxId(int i) {
        for (Box box : boxes) {
            if (box.id == i) {
                return box;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        SortAndPrune c = new SortAndPrune();
        boolean running = true;
        while (running) {
            c.update(c.boxes);
            c.render(0);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String s = reader.readLine();
            if (s != "x") {
                c.getBoxId(1).move(Integer.parseInt(s));
            } else {
                running = false;
            }
        }
        System.exit(0);
    }
}
