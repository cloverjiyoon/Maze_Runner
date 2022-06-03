package HTW;

public enum Direction {
    N(0, 2),
    E(1,3),
    S(2,0),
    W(3,1);

    private int directionNum;
    private int oppositeDir;

    private Direction(int directionNum, int oppositeDir){
        this.directionNum = directionNum;
        this.oppositeDir = oppositeDir;
    }

    public int getDirectionNum(){
        return this.directionNum;
    }

    public int getOppositeDir(){
        return this.oppositeDir;
    }

}
