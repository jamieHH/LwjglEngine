package toolbox.sortAndPrune;

public class Box {

    public int id;
    public EndPoint[] min = new EndPoint[3];
    public EndPoint[] max = new EndPoint[3];

    public Box(int id, EndPoint min, EndPoint max) {
        this.id = id;
        this.min[0] = min;
        this.max[0] = max;
        this.min[1] = new EndPoint(id, 0, true);
        this.max[1] = new EndPoint(id, 1, false);
        this.min[2] = new EndPoint(id, 0, true);
        this.max[2] = new EndPoint(id, 1, false);
    }

    public Box(Box src) {
        set(src);
    }

    public Box(int id, EndPoint[] min, EndPoint[] max) {
        this.id = id;
        this.min = min;
        this.max = max;
    }

    public void move(float i) {
        this.max[0].value += i;
        this.min[0].value += i;
    }

    public void move(float x, float y, float z) {
        this.max[0].value += x;
        this.min[0].value += x;
        this.max[1].value += y;
        this.min[1].value += y;
        this.max[2].value += z;
        this.min[2].value += z;
    }

    public Box set(Box src) {
        this.id = src.id;
        this.min = src.min;
        this.max = src.max;
        return this;
    }
}