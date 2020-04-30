package toolbox.sortAndPrune;

public class Box {

    public int id;
    public EndPoint[] min = new EndPoint[3];
    public EndPoint[] max = new EndPoint[3];

    public Box(int id, EndPoint min, EndPoint max) {
        this.id = id;
        this.min[0] = min;
        this.max[0] = max;
    }

    public Box(int id, EndPoint[] min, EndPoint[] max) {
        this.id = id;
        this.min = min;
        this.max = max;
    }

    public void move(float i) {
        this.min[0].value += i;
        this.max[0].value += i;
    }
}