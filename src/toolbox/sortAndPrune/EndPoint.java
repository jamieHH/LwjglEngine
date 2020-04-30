package toolbox.sortAndPrune;

public class EndPoint {

    public int boxId;
    public float value;
    public boolean isMin;

    public EndPoint(int boxId, float mValue, boolean isMin) {
        this.boxId = boxId;
        this.value = mValue;
        this.isMin = isMin;
    }
}