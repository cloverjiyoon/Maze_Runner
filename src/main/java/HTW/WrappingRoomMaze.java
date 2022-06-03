package HTW;

import java.util.Random;

public class WrappingRoomMaze extends RoomMaze{

    public WrappingRoomMaze(int col, int row, int numOfWalls, int pitNum, int batNum, int arrowNum) {
        super(col, row, numOfWalls, pitNum, batNum, arrowNum);

    }

    @Override
    public void setOuterEdge(int col, int row) {
        Random random = new Random();
        for (int i = 0; i < col; i++) {
            this.getTopEdge().add(random.nextBoolean());
        }
        for (int i = 0; i < row; i++) {
            this.getSideEdge().add(random.nextBoolean());
        }
    }

}
