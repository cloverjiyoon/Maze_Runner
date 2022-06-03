package HTW;

import java.util.Scanner;

public class PlayMaze {

    public PlayMaze(int col, int row, int pitNum, int batNum, int arrowNum, boolean wrapping, int numOfWalls){

        MazeInterface maze;
        if(wrapping){
            maze = new WrappingRoomMaze(col, row, numOfWalls, pitNum, batNum, arrowNum);
        }
        else{
            maze = new RoomMaze(col, row, numOfWalls, pitNum, batNum, arrowNum);
        }

        new ControlMaze(maze);

    }
}
