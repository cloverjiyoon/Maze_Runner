package HTW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface MazeInterface {

    // setting up Pits and Bats
    public HashSet<Integer> setItem(int itemNum);

    public void setOuterEdge(int col, int row);

    public void setPitPosition(HashSet<Integer> item);

    public void setBatPosition(HashSet<Integer> item);

    public void setStart(int item);

    public void setWumpus(int item);

    public void setArrowLoc(int loc);

    public int getStart();

    public int getCol();

    public int getRow();

    public HashMap<Integer, List<Integer>> getCell();

    public List<Boolean> getTopEdge();

    public List<Boolean> getSideEdge();

    public int getWumpus();

    public HashMap<Integer, List<Integer>> getCellsOriginal();


    // collecting edge of each cell
    // each cell has TWO edges for Perfect Maze, FOUR edges for Wrapping Maze
    public void setEdgesCell();


    // processing which edges to keep
    public void carveMaze();

    public List<Integer> getCellByEdge(int edgeNum);

    public void combineCell(List<Integer> cellList, int edgeNum);

    // Calculate the final set of edges
    public List<Integer> calculateFinalEdges();

    // Set the final set of edges
    public void setFinalEdges(List<Integer> finalEdges);

    public List<Integer> getFinalEdges();

    public String printTopEdges();

    public List<Integer> getEdgesInRow(List<Integer> finalEdges, int row);

    public boolean checkEnemies();

    public void printExplanation();

    public int getCurrentPosition();


    public void setCurrentPosition(String newPosition);

    public List<Boolean> getDirection(int cellNum);

    public void printDirection(int cellNum);

    public void getRoomAndDoors();

    public boolean shootArrow(String direction, int numCave);

    public void navigateTunnel(String directionOption);

    public void getAdjacentCell(int cellNum);

    public void giveAdjacentInfo();



}
