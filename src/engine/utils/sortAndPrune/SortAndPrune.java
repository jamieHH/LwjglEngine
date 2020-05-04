package engine.utils.sortAndPrune;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SortAndPrune {

    private static class Pair {

        public int box0;
        public int box1;

        public Pair(int b0, int b1) {
            this.box0 = b0;
            this.box1 = b1;
        }
    }

    public List<Box> boxes = new ArrayList<>();
    private List<EndPoint> endPointsX = new ArrayList<>();
    private List<EndPoint> endPointsY = new ArrayList<>();
    private List<EndPoint> endPointsZ = new ArrayList<>();
    private List<List<Pair>> axisPairs = new ArrayList<>(3);
    private List<Pair> activePairs = new ArrayList<>();

    public SortAndPrune(boolean isTest) {
        addBox(new Box(1, new EndPoint(1, 0, true), new EndPoint(1, 3, false)));
        addBox(new Box(2, new EndPoint(2, 1, true), new EndPoint(2, 4, false)));
        addBox(new Box(3, new EndPoint(3, 10, true), new EndPoint(3, 12, false)));
        addBox(new Box(4, new EndPoint(4, 9, true), new EndPoint(4, 11, false)));
        addBox(new Box(5, new EndPoint(5, 9, true), new EndPoint(5, 11, false)));
    }

    public SortAndPrune() {

    }

    public void addBox(Box b) {
        boxes.add(b);
    }

    public void removeBoxId(int boxId) {
        for (Box b : boxes) {
            if (b.id == boxId) {
                boxes.remove(b);
                return;
            }
        }
    }

    public void update() {
        axisPairs.clear();
        for (int i = 0; i < 3; i++) {
            axisPairs.add(findPairsAlongAxis(boxes, i));
        }
        List<Pair> xyIntersects = new ArrayList<>();
        for (Pair pair : axisPairs.get(0)) {
            if (pairExistsInList(axisPairs.get(1), pair)) {
                xyIntersects.add(pair);
            }
        }
        activePairs.clear();
        for (Pair pair : xyIntersects) {
            if (pairExistsInList(axisPairs.get(2), pair)) {
                activePairs.add(pair);
            }
        }
    }

    public void update2() {
        activePairs.clear();
        endPointsX.clear();
        endPointsY.clear();
        endPointsZ.clear();

        addAxisEndPoints(boxes);

        List<Pair> xyIntersects = new ArrayList<>();
        for (Pair pair : findAxisPairs(endPointsX)) {
            if (pairExistsInList(findAxisPairs(endPointsY), pair)) {
                xyIntersects.add(pair);
            }
        }
        for (Pair pair : xyIntersects) {
            if (pairExistsInList(findAxisPairs(endPointsZ), pair)) {
                activePairs.add(pair);
            }
        }
    }

    public List<Integer> findFullIntersects(int searchId) {
        List<Integer> matches = new ArrayList<>();
        for (Pair pair: activePairs) {
            if (pair.box0 == searchId) {
                matches.add(pair.box1);
            } else if (pair.box1 == searchId) {
                matches.add(pair.box0);
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

    private void addAxisEndPoints(List<Box> boxes) {
        for (Box box : boxes) {
            endPointsX.add(box.min[0]);
            endPointsX.add(box.max[0]);
            endPointsY.add(box.min[1]);
            endPointsY.add(box.max[1]);
            endPointsZ.add(box.min[2]);
            endPointsZ.add(box.max[2]);
        }
        endPointsX.sort((o1, o2) -> {
            if (o1.value == o2.value) {
                return 0;
            }
            return o1.value < o2.value ? -1 : 1;
        });
        endPointsY.sort((o1, o2) -> {
            if (o1.value == o2.value) {
                return 0;
            }
            return o1.value < o2.value ? -1 : 1;
        });
        endPointsZ.sort((o1, o2) -> {
            if (o1.value == o2.value) {
                return 0;
            }
            return o1.value < o2.value ? -1 : 1;
        });
    }

    private List<Pair> findPairsAlongAxis(List<Box> sortList, int axis) {
        sortAlongAxis(sortList, axis);
        List<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < sortList.size(); i++) {
            for (int j = i + 1; j < sortList.size(); j++) {
                if (sortList.get(i).max[axis].value > sortList.get(j).min[axis].value) {
                    pairs.add(new Pair(sortList.get(i).id, sortList.get(j).id));
                }
            }
        }
        return pairs;
    }

    private List<Pair> findAxisPairs(List<EndPoint> endPointsAxis) {
        List<Integer> checked = new ArrayList<>();
        List<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < endPointsAxis.size(); i++) {
            EndPoint ep0 = endPointsAxis.get(i);
            if (!checked.contains(ep0.boxId)) {
                for (Integer id : getIdsBetweenId(endPointsAxis, ep0.boxId)) {
                    if (!checked.contains(id)) {
                        pairs.add(new Pair(ep0.boxId, id));
                    }
                }
                checked.add(ep0.boxId);
            }
        }
        return pairs;
    }

    private List<Integer> getIdsBetweenId(List<EndPoint> endPointsAxis, int boxId) {
        List<Integer> ids = new ArrayList<>();
        boolean write = false;
        for (EndPoint endPoint : endPointsAxis) {
            if (endPoint.boxId == boxId) {
                if (write) {
                    break;
                }
                write = true;
            } else {
                if (write) {
                    ids.add(endPoint.boxId);
                }
            }
        }
        return ids;
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
        for (Pair activePair : activePairs) {
            System.out.print("[" + activePair.box0 + "," + activePair.box1 + "] ");
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
        SortAndPrune c = new SortAndPrune(true);
        boolean running = true;
        while (running) {
            c.update();
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
