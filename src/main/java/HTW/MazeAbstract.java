package HTW;

import java.util.*;

abstract class MazeAbstract implements MazeInterface {

    private final int col;
    private final int row;
    private final int pitNum;
    private final int batNum;

    private int start;

    private HashMap<String, Integer> adjacentDir = new HashMap<>();
    List<String> dir = Arrays.asList(Direction.N.toString(),
            Direction.E.toString(),Direction.S.toString(), Direction.W.toString());

    // edges in order (1, 2, 3, ...)
    private List<Integer> innerEdge = new ArrayList<>();
    private List<Boolean> topEdge = new ArrayList<>();
    private List<Boolean> sideEdge = new ArrayList<>();
    private HashMap<Integer, List<Integer>> cells = new HashMap<>();
    private HashMap<Integer, List<Integer>> cellsOriginal = new HashMap<>();

    private HashMap<Integer, List<Boolean>> roomWithDoors = new HashMap<Integer, List<Boolean>>();
    private HashMap<Integer, List<Boolean>> tunnel = new HashMap<Integer, List<Boolean>>();

    // Enemies
    private HashSet<Integer> pitPosition = new HashSet<>();
    private HashSet<Integer> batPosition = new HashSet<>();
    private int wumpus = 0;


    private int arrowNum;
    private int arrowLoc;
    private int arrowDir;


    // for toString, to finalize and print Maze
    private List<Integer> finalEdges;
    private List<Integer> edgesByRow;



    public MazeAbstract(int col, int row, int pitNum, int batNum, int arrowNum) {

        this.col = col;
        this.row = row;
        this.setOuterEdge(col, row);


        this.pitNum = pitNum;
        this.batNum = batNum;
        this.arrowNum = arrowNum;


    }


    @Override
    public HashSet<Integer> setItem(int itemNum){
        int num = col * row;
        HashSet<Integer> item = new HashSet<>();
        Random random = new Random();
        int randInt;
        int temp;
        while(item.size() < itemNum){
            randInt = random.nextInt(roomWithDoors.keySet().size());
            temp = new ArrayList<>(roomWithDoors.keySet()).get(randInt);
            if((!pitPosition.contains(temp)) && (!batPosition.contains(temp)) &&
            (temp != wumpus) && (temp != start)) {
                item.add(temp);
            }
        }
        return item;

    }


    public void setOuterEdge(int col, int row){
        for (int i = 0; i < col; i++) {
            this.getTopEdge().add(true);
        }
        for (int i = 0; i < row; i++) {
            this.getSideEdge().add(true);
        }
        return;
    }


    public void setPitPosition(HashSet<Integer> item) {
        this.pitPosition = item;
    }

    public void setBatPosition(HashSet<Integer> item) {
        this.batPosition = item;
    }

    public void setStart(int item) {
        this.start = item;
    }

    public void setWumpus(int item) {
        this.wumpus = item;
    }

    public void setArrowLoc(int loc){
        this.arrowLoc = loc;
    }

    public int getStart(){
        return this.start;
    }


    @Override
    public int getCol(){ return this.col;}

    @Override
    public int getRow(){ return this.row;}

    @Override
    public HashMap<Integer, List<Integer>> getCell(){ return this.cells;}

    @Override
    public List<Boolean> getTopEdge(){
        return this.topEdge;
    }

    @Override
    public List<Boolean> getSideEdge(){
        return this.sideEdge;
    }

    @Override
    public int getWumpus() {
        return this.wumpus;
    }



    @Override
    // collecting edge of each cell
    // each cell has TWO edges for Perfect Maze, FOUR edges for Wrapping Maze
    public void setEdgesCell() {

        // initialize cells with empty list
        for (int i = 1; i <= this.col * this.row; i++) {
            this.cells.put(i, new ArrayList<Integer>());
        }

        // creating inner edges for a perfect maze
        for (int i = 1; i <= 2 * this.col * this.row - this.col - this.row; i++) {
            this.innerEdge.add(i);
        }

        // add edges in cells
        for (int i : this.innerEdge) {
            // if edge is Vertical
            if (i <= this.col * this.row - this.row) {
                cells.get(i + (i - 1) / (this.col - 1)).add(i);  // left
                cells.get(i + 1 + (i - 1) / (this.col - 1)).add(i); // right
            }
            // if edge is Horizontal
            else {
                cells.get(i - (this.col * this.row) + this.row).add(i); // up
                cells.get(i - (this.col * this.row) + this.row + this.col ).add(i); // down
            }
        }
        cellsOriginal.putAll(cells);
    }



    public HashMap<Integer, List<Integer>> getCellsOriginal() {
        return this.cellsOriginal;
    }

    @Override
    // processing which edges to keep
    public void carveMaze() {
        List<Integer> keys = new ArrayList<>(cells.keySet());
        Collections.shuffle(keys);
        Random random = new Random();
        // run the loop while all cells are connected
        while (cells.size() > 1) {
            for (int i : keys) {
                try {
                    // get the random edge number in iTH cells
                    int edgeNum = cells.get(i).get(random.nextInt(cells.get(i).size()));
                    // get the cells which contains above edge number
                    List<Integer> cellList = getCellByEdge(edgeNum);
                    // if two cells in the cellList are NOT the SAME OR only two cells are left and their edges are the same (last case)
                    // combine them
                    if (!cells.get(cellList.get(0)).equals(cells.get(cellList.get(1))) ||
                            (cells.size() == 2 && cells.get(cellList.get(0)).equals(cells.get(cellList.get(1))))) {
                        combineCell(cellList, edgeNum);
                    }
                    keys = new ArrayList<>(cells.keySet());
                } catch (Exception ignored) {}
            }
        }
    }

    // return the list of cells contain certain edge
    @Override
    public List<Integer> getCellByEdge(int edgeNum) {
        List<Integer> cellList = new ArrayList<>();
        for (int i : cells.keySet()) {
            if (cells.get(i).contains(edgeNum)) {
                cellList.add(i);
            }
            else continue;
            // each cell has 2 INNER edge
            if (cellList.size() == 2) {
                break;
            }
        }
        return cellList;
    }

    @Override
    public void combineCell(List<Integer> cellList, int edgeNum) {
        for (int i : cells.get(cellList.get(0))) {
            // if second cell doesn't contain edge in first cell, add that edge to second cell
            if (!cells.get(cellList.get(1)).contains(i)) {
                cells.get(cellList.get(1)).add(i);
            }
        }
        // remove the edge between the two cells in second cell's list after combining those cells
        cells.get(cellList.get(1)).remove(cells.get(cellList.get(1)).indexOf(edgeNum));
        Collections.sort(cells.get(cellList.get(1)));
        // remove first cell and keep the second cell only
        cells.remove(cellList.get(0));
    }

    @Override
    abstract public List<Integer> calculateFinalEdges();

    public List<Integer> getFinalEdges() {
        return this.finalEdges;
    }


    public void setFinalEdges(List<Integer> finalEdges){
        this.finalEdges = finalEdges;
    }

    // ********************** to print maze *************************** //

    public String printTopEdges(){
        String res = "";

        // print the top edges
        for(int i = 0; i < col; i++){
            if(topEdge.get(i)){
                res += "+---+";
//                System.out.print("+---+");
            }
            else{
                res += "+   +";
//                System.out.print("+   +");
            }
        }
        res += "\n";
        return res;
//        System.out.println();
    }

    // getting the edges in certain row
    public List<Integer> getEdgesInRow(List<Integer> finalEdges, int row) {
        List<Integer> edgesByRow = new ArrayList<>();
        for(int x: finalEdges){
            //Horizontal edges
            // 5  > 2*3 - 2  = 4(horizontal)        && 5 - 4 = 1 <= 3*(0+1) (in that row)          && 5 - 4 = 1 > 3*0 = 0
            if(x > this.col * this.row - this.row && (x - (this.col *this.row - this.row)) <= this.col * (row + 1) &&
                    (x - (this.col * this.row - this.row)) > this.col * row){
                // 5 - 4 - 2*0 + 3 - 1 = 3
                // getting wall position in that row + col - 1
                edgesByRow.add((x - (this.col * this.row - this.row)) - (this.col*row) + this.col - 1);

                //Vertical edges
                // 3 <= 6 - 2
            } else if(x <= this.col * this.row - this.row && x <= (this.col - 1) * (row + 1) && x > (this.col - 1) * row){
                // if it's the last edge in that row
                if(x % (this.col - 1) == 0){
                    edgesByRow.add(this.col - 1);
                } else{
                    edgesByRow.add(x % (this.col - 1));
                }
            }
        }
        return edgesByRow;
    }


    @Override
    public String toString() {
        String res;
        // print the top edges
        res = printTopEdges();

        // print Maze row by row
        for (int i = 0; i < this.row; i++) {
            edgesByRow = this.getEdgesInRow(finalEdges, i);
            // print left most Vertical edge
            if(sideEdge.get(i)){
                res += "|";
            }
            else{
                res += " ";
            }

            // print CELL and Vertical edge column by column
            for(int j = 0; j< this.col; j++){

                // CELL
                if(pitPosition.contains(i*col + j + 1)){
                    res += " P ";
                }
                else if(batPosition.contains(i*col + j + 1)){
                    res += " B ";
                }
                else if((i * col + j + 1) == wumpus){
                    res += " W ";
                }
                else if(start == i * col + j + 1){
                    res += " @ ";
                }
                else{
                    res += "   ";
                }


                // Vertical
                if (j != col - 1) {
                    if(edgesByRow.contains(j + 1)){
                        res += "| ";
                    }
                    else{
                        res += "  ";
                    }
                }

            }

            // print right most Vertical edge
            if(sideEdge.get(i)){
                res += "|";
            }
            else{
                res += " ";
            }

            res += "\n";

            // print Horizontal
            if(i != row - 1){
                for(int j = 0; j< this.col; j++){
                    if(edgesByRow.contains((j + col))){
                        res += "+---+";
                    }
                    else{
                        res += "+   +";
                    }
                }
                res += "\n";
            }
        }
        // print the bottom edges
        res += printTopEdges();

        return res;
    }

    public void printExplanation(){
        System.out.println("P : Bottomless pit\nB: Bats\n@: Current position\nW: Wumpus\n");
        System.out.println("Maze Size : " + col + " by " + row);
//        System.out.println("Following is the model map with the cell number for your convenience");

    }

    public int getCurrentPosition(){
        return start;
    }



    // ************* Enemies ***************//

    // true -> DEATH
    public boolean checkEnemies(){

        if(pitPosition.contains(start)){
            System.out.println("Zeeeees! You Fall into a pit! GoodBye!");
            return true;
        }
        else if(arrowNum == 0){
            System.out.println("You run out of arrow! You Loose!");
            return true;
        }
        else if(start == wumpus){
            System.out.println("Chomp, chomp, chomp, thanks for feeding the Wumpus!\n" +
                    "Better luck next time");
            return true;
        }
        else if(batPosition.contains(start)){
            Random random = new Random();
            int randInt = random.nextInt(3);
            if(randInt == 1){
                System.out.println("You got lucky and successfully duck superbats!");
                return false;
            }
            else{
                System.out.println("Snatch! you are grabbed by superbats and...");
                int newPosition = start;
                while(newPosition == start){
                    newPosition = new ArrayList<>(roomWithDoors.keySet()).get(random.nextInt(roomWithDoors.keySet().size()));
                }
                this.start = newPosition;
                if(batPosition.contains(start) || pitPosition.contains(start) || start == wumpus){
                    return checkEnemies();
                }
                return false;
            }
        }

        return false;
    }


    // ************** Move ********************//

    // getting open direction for the input sell in Clockwise direction
    // TRUE when there is a WALL in that direction
    public List<Boolean> getDirection(int cellNum){
        List<Boolean> directions = Arrays.asList(false, false, false, false);

        List<Integer> originalEdges = cellsOriginal.get(cellNum);


        // Horizontal wall  --
        // Check North : if cell is in the first row
        int temp = cellNum + col * row - row - col;
        if(cellNum >= 1 && cellNum <= col){
            directions.set(0,topEdge.get(cellNum - 1));
        }
        else if(finalEdges.contains(temp) && originalEdges.contains(temp)){
            directions.set(0, true);
        }


        // Check South : if cell is in the last row
        temp = cellNum + col * row - row;
        if(cellNum > (row - 1) * col){
            directions.set(2, topEdge.get(cellNum - (row - 1) * col - 1));
        }
        else if(finalEdges.contains(temp) && originalEdges.contains(temp)){
            directions.set(2, true);
        }


        // Check East : if cell is in the last column
        temp = cellNum - (cellNum/col);
        if(cellNum % col == 0){
            directions.set(1, sideEdge.get((cellNum / col) - 1));
        }
        else if(finalEdges.contains(temp) && originalEdges.contains(temp)){
            directions.set(1, true);
        }


        // Check West : if cell is in the first column
        temp = cellNum - cellNum/col - 1;
        if(cellNum % col == 1){
            directions.set(3, sideEdge.get(cellNum/col));
        }
        else if(finalEdges.contains(temp) && originalEdges.contains(temp) && cellNum % col != 0){
            directions.set(3, true);
        }
        //when the cell is in the last column
        else if(finalEdges.contains(temp+1) && originalEdges.contains(temp+1)  && cellNum % col == 0){
            directions.set(3, true);
        }

        return directions;
    }


    public void getRoomAndDoors(){
        List<Boolean> temp;
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                temp = getDirection(i*col + j + 1);
                // if it's room
                if(temp.stream().filter(p -> p == false).count()!=2){
                    roomWithDoors.put(i*col + j + 1, temp);
                }
                else{ // if it's tunnel
                    tunnel.put(i*col + j + 1, temp);
                }
            }
        }
    }


    public void printDirection(int cellNum){
//        List<Boolean> directions = this.roomWithDoors.get(cellNum);
        List<String> directionString = Arrays.asList(Direction.N.toString(),
                Direction.E.toString(),Direction.S.toString(), Direction.W.toString());
        System.out.print("You are in cave  " + cellNum + "\nTunnels lead to the  ");

        for(int i=0; i<4; i++){
            if(!roomWithDoors.get(cellNum).get(i)){
                System.out.print(directionString.get(i) + " ");
            }
        }
        System.out.println("\n");

    }

    public void setCurrentPosition(String directionOption){

        if(!(directionOption.equals(Direction.N.toString()) || directionOption.equals(Direction.S.toString()) ||
                directionOption.equals(Direction.E.toString()) || directionOption.equals(Direction.W.toString()))){
            throw new IllegalArgumentException("Direction should be one of the N, E, S, W");
        }

        int cur;
        getAdjacentCell(start);
        start = adjacentDir.get(directionOption);
        cur = Direction.valueOf(directionOption).getOppositeDir();

        arrowLoc = start;

        // if new position is tunnel, keep going through the tunnel
        if(tunnel.containsKey(start)){
            for(int i=0; i<4; i++){
                if(tunnel.get(start).get(i) == false && i != cur){
                    setCurrentPosition(dir.get(i));
                    return;
                }
            }
        }

    }


    public void navigateTunnel(String directionOption){

        if(!(directionOption.equals(Direction.N.toString()) || directionOption.equals(Direction.S.toString()) ||
                directionOption.equals(Direction.E.toString()) || directionOption.equals(Direction.W.toString()))){
            throw new IllegalArgumentException("Direction should be one of the N, E, S, W");
        }

        int prev;
        getAdjacentCell(arrowLoc);
        arrowLoc = adjacentDir.get(directionOption);
        prev = Direction.valueOf(directionOption).getOppositeDir();
        arrowDir = Direction.valueOf(directionOption).getDirectionNum();

//        // North
//        if(directionOption.equals("N")){
//            prev = 2;
//            arrowDir = 0;
//
//        }
//        // East
//        else if(directionOption.equals("E")){
//            prev = 3;
//            arrowDir = 1;
//
//        }
//        // South
//        else if(directionOption.equals("S")){
//            prev = 0;
//            arrowDir = 2;
//
//        }
//        // West
//        else if(directionOption.equals("W")){
//            prev = 1;
//            arrowDir = 3;
//        }
//        else{
//            throw new IllegalArgumentException("Direction should be one of the N, E, S, W");
//        }

        // if new position is tunnel, keep going through the tunnel
        if(tunnel.containsKey(arrowLoc)){
            for(int i=0; i<4; i++){
                if(tunnel.get(arrowLoc).get(i) == false && i != prev){
                    navigateTunnel(dir.get(i));
                    return;
                }
            }
        }

    }




    public boolean shootArrow(String direction, int numCave){
        arrowNum -= 1;
        int i;
        for(i=0; i<numCave; i++){
            navigateTunnel(direction);
            // wall blocks arrow
            if(roomWithDoors.get(arrowLoc).get(arrowDir) && i != numCave - 1){
                break;
            }
            direction = dir.get(arrowDir);
        }

        if(arrowLoc == wumpus && i == numCave){
            System.out.println("Hee hee hee, you got the Wumpus!\n" +
                    "Next time you won't be so lucky\n");
            return true;
        }
        else{
            System.out.println("Failed attempt! You didn't get the Wumpus!\n");
            arrowLoc = start;
        }
        return false;
    }


    public void getAdjacentCell(int cellNum){

        // North
        if(cellNum - col <= 0){
            adjacentDir.put("N", cellNum - col + col * row);
        }
        else{
            adjacentDir.put("N", cellNum - col);
        }

        // East
        if(cellNum % col == 0){
            adjacentDir.put("E", cellNum - (col - 1));
        }
        else{
            adjacentDir.put("E", cellNum + 1);
        }

        // South
        if(cellNum > (row - 1) * col){
            adjacentDir.put("S", cellNum - (row - 1) * col);
        }
        else{
            adjacentDir.put("S", cellNum + col);
        }

        // West
        if(cellNum % col == 1){
            adjacentDir.put("W", cellNum + (col - 1));
        }
        else{
            adjacentDir.put("W", cellNum - 1);
        }

    }

    public void giveAdjacentInfo(){
        boolean flagWumpus = true;
        boolean flagPit = true;
        for(Direction dir : Direction.values()){

            if(!roomWithDoors.get(start).get(dir.getDirectionNum())){
                navigateTunnel(dir.toString());

                if(arrowLoc == wumpus && flagWumpus){
                    System.out.println("You smell Wumpus!!");
                    flagWumpus = false;
                }
                if(pitPosition.contains(arrowLoc) && flagPit){
                    System.out.println("You feel very cold draft!!");
                    flagPit = false;
                }
                arrowLoc = start;

            }
        }

    }

}
