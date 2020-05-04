package engine.utils.sortAndPrune;

public class EndPoint {

    public int boxId;
    public int value;
    public boolean isMin;

    public EndPoint(int boxId, float mValue, boolean isMin) {
        this.boxId = boxId;
        this.value = (int) mValue * 100;
        this.isMin = isMin;
    }
}