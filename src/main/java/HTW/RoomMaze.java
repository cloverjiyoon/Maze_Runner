package HTW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomMaze extends MazeAbstract{

    private int numOfWalls;

    public RoomMaze(int col, int row, int numOfWalls, int pitNum, int batNum, int arrowNum) {
        super(col, row, pitNum, batNum, arrowNum);

        this.numOfWalls = numOfWalls;

        if(numOfWalls >= ((row - 1) * col + (col - 1) * row) - (row * col - 1)) {
            throw new IllegalArgumentException("Remaining number of walls needs to be smaller in order to generate Room Maze");
        }

        this.setFinalEdges(this.calculateFinalEdges());

        getRoomAndDoors();

        setPitPosition(setItem(pitNum));
        setBatPosition(setItem(batNum));
        setStart(setItem(1).stream().mapToInt(Integer::intValue).toArray()[0]);
        setWumpus(setItem(1).stream().mapToInt(Integer::intValue).toArray()[0]);
        setArrowLoc(this.getStart());
    }

    @Override
    public List<Integer> calculateFinalEdges() {
        this.setEdgesCell();
        if (this.getCol() < 2 || this.getRow() < 2) {
            return new ArrayList<>();
        } else {
            this.carveMaze();
            List<Integer> finalEdgeList = new ArrayList<>();
            // only ONE cell left in cells after carving maze, finalEdgeList is getting the final set of left edges
            for (int i : this.getCell().keySet()) {
                finalEdgeList = this.getCell().get(i);
                break;
            }

            Random random = new Random();
            while (finalEdgeList.size() > this.numOfWalls) {
                finalEdgeList.remove(finalEdgeList.get(random.nextInt(finalEdgeList.size() - 1)));
            }
            return finalEdgeList;
        }
    }
}
