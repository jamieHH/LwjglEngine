package toolbox.sortAndPrune;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SortAndPrune {

    private class Pair {

        public Box box0;
        public Box box1;

        public Pair(Box b0, Box b1) {
            this.box0 = b0;
            this.box1 = b1;
        }
    }

    private List<Box> boxes = new ArrayList<>();
    private List<EndPoint>[] endPoints;
    public List<Pair>[] intersectingPairs = new ArrayList[3];

    private SortAndPrune() {
        boxes.add(new Box(1, new EndPoint(1, 0, true), new EndPoint(1, 3, false)));
        boxes.add(new Box(2, new EndPoint(2, 1, true), new EndPoint(2, 4, false)));
        boxes.add(new Box(3, new EndPoint(3, 10, true), new EndPoint(3, 12, false)));
        boxes.add(new Box(4, new EndPoint(4, 9, true), new EndPoint(4, 11, false)));
        boxes.add(new Box(5, new EndPoint(5, 9, true), new EndPoint(5, 11, false)));
    }

    private  void tick() {
        List<Box> sortBoxes = new ArrayList<>(boxes);

        sortAlongAxis(sortBoxes, 0);
        intersectingPairs[0] = findPairsAlongAxis(sortBoxes, 0);
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
        int count = 1;
        for (int i = 0; i < sortList.size(); i++) {
            for (int j = i+1; j < sortList.size(); j++) {
                count++;
                if (sortList.get(i).max[axis].value > sortList.get(j).min[axis].value) {
                    pairList.add(new Pair(sortList.get(i), sortList.get(j)));
                }
            }
        }

        System.out.println(count + " checks");
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
        for (int i = 0; i < intersectingPairs[axis].size(); i++) {
            System.out.print("["+intersectingPairs[axis].get(i).box0.id+","+intersectingPairs[axis].get(i).box1.id+"] ");
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
            c.tick();
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
