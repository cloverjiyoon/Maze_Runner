package HTW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PerfectMaze extends MazeAbstract{

    public PerfectMaze(int col, int row, int pitNum, int batNum, int arrowNum) {
        super(col, row, pitNum, batNum, arrowNum);

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
            throw new IllegalArgumentException("Cannot make a perfect maze with this number of row and column");
        } else {
            this.carveMaze();
            List<Integer> finalEdgeList = new ArrayList<>();
            // only ONE cell left in cells after carving maze, finalEdgeList is getting the final set of left edges
            for (int i : this.getCell().keySet()) {
                finalEdgeList = this.getCell().get(i);
                break;
            }
            return finalEdgeList;
        }
    }


}
