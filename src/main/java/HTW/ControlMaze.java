package HTW;

import java.util.Scanner;

public class ControlMaze {

    public ControlMaze(MazeInterface maze) {

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        String action;
        String direction;
        Integer numCave;
        String shootDirection;

        boolean death = false;

        maze.printExplanation();
        // Print Maze
        System.out.println(maze);


        while (!death) {

            death = maze.checkEnemies();

            if(death){
                break;
            }

            // Show where Player can move
            maze.giveAdjacentInfo();
            maze.printDirection(maze.getCurrentPosition());

            System.out.println("Shoot or Move(S or M)?  ");
            action = myObj.nextLine();

            if(action.equals("M")){
                // Getting user input on direction
                System.out.println("Where to?  ");
                direction = myObj.nextLine();
                maze.setCurrentPosition(direction);

            }
            else if(action.equals("S")){
                System.out.println("No. of caves (1-5)?  ");
                numCave = Integer.valueOf(myObj.nextLine());
                System.out.println("In which direction?  ");
                shootDirection = myObj.nextLine();
                death = maze.shootArrow(shootDirection, numCave);

            }
            else{
                throw new IllegalArgumentException("Enter M or S");
            }

        }


    }
}


