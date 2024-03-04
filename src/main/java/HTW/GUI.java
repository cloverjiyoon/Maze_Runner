package HTW;

import javafx.application.Application;
import javafx.stage.Stage;

import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class GUI extends Application {
    private int col;
    private int row;
    private double width;
    private double wallWidth;
    private int pitNum;
    private int batNum;
    private int arrowNum;
    private boolean wrapping;
    private int numOfWalls;

    private Paint path = Color.LIGHTGRAY;
    private Paint wall = Color.BLACK;
    private boolean show = true;
    private Timeline timeline = new Timeline();
    private Timeline stopwatch = new Timeline();
    private Region[][] squares;
    private int x = 0;
    private int y = 0;
    private int time = 0;
    private EventHandler<KeyEvent> keyEvent;
    private MazeInterface maze;

    @Override
    public void start(Stage primaryStage) {
        VBox vBox = new VBox();
        StackPane stackPane = new StackPane(vBox);
        Scene scene = new Scene(stackPane, 600,600);
        primaryStage.setTitle("Maze");
        primaryStage.setScene(scene);
        primaryStage.show();
        startGame(primaryStage, vBox, scene, stackPane);
    }

    public void startGame(Stage primaryStage, VBox vBox, Scene scene, StackPane stackPane){
        stackPane.getChildren().clear();
        vBox.getChildren().clear();
        Label text = new Label("Enter the maze condition you want!");
        Label enterX = new Label("X: ");
        Label enterY = new Label("     Y: ");
        TextField xInput = new TextField();
        TextField yInput = new TextField();

//        Label enterPit = new Label("Pit #: ");
//        Label enterBat = new Label("     Bat #: ");
//        Label enterArrow = new Label("     Arrow #: ");
//        TextField pitInput = new TextField();
//        TextField batInput = new TextField();
//        TextField arrowInput = new TextField();

        Label enterWrapping = new Label("Wrapping: ");
        Label enterWalls = new Label("     Walls #: ");
        TextField wrappingInput = new TextField();
        TextField wallsInput = new TextField();


        Button submit = new Button("Start Game!");
        submit.setId("submit");

        xInput.setMaxWidth(100);
        yInput.setMaxWidth(100);
        xInput.setMinWidth(100);
        yInput.setMinWidth(100);

//        pitInput.setMaxWidth(100);
//        pitInput.setMinWidth(100);
//        batInput.setMaxWidth(100);
//        batInput.setMinWidth(100);
//        arrowInput.setMaxWidth(100);
//        arrowInput.setMinWidth(100);
        wrappingInput.setMaxWidth(100);
        wrappingInput.setMinWidth(100);
        wallsInput.setMaxWidth(100);
        wallsInput.setMinWidth(100);

        Font font = new Font(20);
        text.setFont(font);
        enterX.setFont(font);
        enterY.setFont(font);
//        enterPit.setFont(font);
//        enterBat.setFont(font);
//        enterArrow.setFont(font);
        enterWrapping.setFont(font);
        enterWalls.setFont(font);

        xInput.setFont(font);
        yInput.setFont(font);
//        pitInput.setFont(font);
//        batInput.setFont(font);
//        arrowInput.setFont(font);
        wrappingInput.setFont(font);
        wallsInput.setFont(font);


        submit.setFont(font);

        EventHandler<MouseEvent> handler = mouseEvent -> {
            Button temp = (Button) mouseEvent.getSource();
            String id = temp.getId();
            if(id.equals("submit")){
                try{
                    this.col = Integer.parseInt(xInput.getText());
                    this.row = Integer.parseInt(yInput.getText());
//                    this.pitNum = Integer.parseInt(pitInput.getText());
//                    this.batNum = Integer.parseInt(batInput.getText());
//                    this.arrowNum = Integer.parseInt(arrowInput.getText());
                    this.wrapping = Boolean.parseBoolean(wrappingInput.getText());
                    this.numOfWalls = Integer.parseInt(wallsInput.getText());
                    this.width = (this.col >= this.row) ? 500.0/this.col : 500.0/this.row;
                    this.wallWidth = this.width / 10.0;
                    this.squares = new Region[this.row][this.col];
                    generateMaze(primaryStage, vBox, scene, stackPane);
                } catch (Exception e){
                    text.setText("Please enter valid numbers!");
                }
            }
        };

        submit.setOnMouseClicked(handler);

        HBox h1 = new HBox(enterX, xInput, enterY, yInput);
        h1.setAlignment(Pos.CENTER);

//        HBox h2 = new HBox(enterPit, pitInput, enterBat, batInput, enterArrow, arrowInput);
//        h2.setAlignment(Pos.CENTER);

        HBox h3 = new HBox(enterWrapping, wrappingInput, enterWalls, wallsInput);
        h3.setAlignment(Pos.CENTER);

        vBox.getChildren().add(text);
        vBox.getChildren().add(h1);
//        vBox.getChildren().add(h2);
        vBox.getChildren().add(h3);
        vBox.getChildren().add(submit);

        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(40);
        stackPane.getChildren().add(vBox);
    }

    public void generateMaze(Stage primaryStage, VBox vBox1, Scene scene, StackPane stackPane) {
        vBox1.getChildren().clear();

        if(wrapping){
            maze = new WrappingRoomMaze(col, row, numOfWalls, pitNum, batNum, arrowNum);
        }
        else{

            maze = new RoomMaze(col, row, numOfWalls, pitNum, batNum, arrowNum);
        }

        this.x = (maze.getStart() - 1)% this.col;
        this.y = (maze.getStart() -1 ) / this.col;

        Task<List<Integer>> task = new Task<>() {
            @Override
            protected List<Integer> call() {
                return maze.getFinalEdges();
            }
        };
        task.setOnSucceeded(e -> {
            this.maze = maze;
            showMaze(primaryStage, vBox1, task.getValue(), scene, stackPane);
            this.timeline.stop();
        });



        new Thread(task).start();

        KeyFrame keyFrame = new KeyFrame(Duration.millis(350), actionEvent -> {});
        this.timeline.getKeyFrames().add(keyFrame);
//        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.timeline.play();
    }



    public void showMaze(Stage primaryStage, VBox vBox1, List<Integer> finalEdges, Scene scene, StackPane stackPane){
//        this.x = 0;
//        this.y = 0;
        System.out.println(maze);


        this.time = 0;
        VBox vBox = new VBox();
        vBox1.getChildren().clear();

        // setting up upper boarder line
        if(!wrapping){
            Rectangle upperBorder = new Rectangle(500 + 2 * this.wallWidth, this.wallWidth);
            upperBorder.setFill(this.wall);
            vBox.getChildren().add(upperBorder);
        }
        else{
            HBox hBoxUpper = new HBox();

            for(int i=0; i<this.col; i++){
                Rectangle upperBorder = new Rectangle(width, this.wallWidth);
                if(i == this.col - 1 || i == 0){
                    // adding tiny one block for edge
                    upperBorder = new Rectangle(width + this.wallWidth, this.wallWidth);
                }

                if(maze.getTopEdge().get(i)){
                    upperBorder.setFill(this.wall);
                }
                else{
                    upperBorder.setFill(Color.TRANSPARENT);
                }
                hBoxUpper.getChildren().add(upperBorder);
            }

            vBox.getChildren().add(hBoxUpper);
        }

        // Generate maze row by row
        for(int i = 0; i < this.row; i++){
            // numbering edges starting from 1 for each row
            List<Integer> wallsInRow = maze.getEdgesInRow(finalEdges, i);

            // Creating regions with borders as walls

            HBox hBox = new HBox();
            Region firstSquare = new Region(); // First square of each row
            firstSquare.setMaxSize(this.width,this.width);
            firstSquare.setMinSize(this.width, this.width);
            firstSquare.setPrefSize(this.width, this.width);
            firstSquare.setBackground(new Background(new BackgroundFill(this.path, null, null)));


            //first row cell then add black line in BOTTOM
            if(wallsInRow.contains(this.col)){
                // top right bottom left
                // add BOTTOM line of the FIRST CELL in each row
                firstSquare.setStyle("-fx-border-color: transparent transparent black transparent; -fx-border-width: " + this.wallWidth);
            }
            this.squares[i][0] = firstSquare;

            // vertical border the MOST LEFT side(row by row)
            Rectangle leftWall;
            if(maze.getSideEdge().get(i)){
               leftWall = new Rectangle(this.wallWidth,  this.width, this.wall);
            }
            else{
                leftWall = new Rectangle(this.wallWidth,  this.width, Color.TRANSPARENT);
            }

            hBox.getChildren().add(leftWall);
            hBox.getChildren().add(firstSquare);

            // fill up column-wise
            for(int j = 0; j < col - 1; j++){
                Region square = new Region();
                // first cell is already filled, started to fill from second cell in each row
                this.squares[i][j + 1] = square;
                square.setMaxSize(this.width, this.width);
                square.setMinSize(this.width, this.width);
                square.setPrefSize(this.width, this.width);
                square.setBackground(new Background(new BackgroundFill(this.path, null, null)));

                // Adding borderline for each cell
                if(wallsInRow.contains((j + 1))){
                    // Vertical : draw LEFT line of the cell starting from the second cell
                    square.setStyle("-fx-border-color: transparent transparent transparent black; -fx-border-width: " + wallWidth);
                    // Horizontal : draw BOTTOM line of the cell starting from the second cell
                    if(wallsInRow.contains(j + col + 1)){
                        square.setStyle("-fx-border-color: transparent transparent black black; -fx-border-width: " + wallWidth);
                    }
                } else if(wallsInRow.contains(j + col + 1)){ // when cell only need BOTTOM line
                    square.setStyle("-fx-border-color: transparent transparent black transparent; -fx-border-width: " + wallWidth);
                }
                hBox.getChildren().add(square);
                hBox.setAlignment(Pos.CENTER);
            } // end of Column-wise for loop

            // vertical border the most RIGHT side
            Rectangle rightWall;
            if(maze.getSideEdge().get(i)){
                rightWall = new Rectangle(this.wallWidth,  this.width, this.wall);
            }
            else {
                rightWall = new Rectangle(this.wallWidth, this.width, Color.TRANSPARENT);
            }

            hBox.getChildren().add(rightWall);

            // after setting up one row, add it vertically using hBox
            vBox.getChildren().add(hBox);

        } // end of ROW-wise for loop

        // bottom horizontal border

        if(!wrapping){
            Rectangle lowerBorder = new Rectangle(500 + 2 * this.wallWidth, this.wallWidth);
            lowerBorder.setFill(this.wall);
            vBox.getChildren().add(lowerBorder);
        }
        else{
            HBox hBoxLower = new HBox();

            for(int i=0; i<this.col; i++){
                Rectangle lowerBorder = new Rectangle(width, this.wallWidth);
                if(i == this.col - 1 || i == 0){
                    // adding tiny one block for edge
                    lowerBorder = new Rectangle(width + this.wallWidth, this.wallWidth);
                }

                if(maze.getTopEdge().get(i)){
                    lowerBorder.setFill(this.wall);
                }
                else{
                    lowerBorder.setFill(Color.TRANSPARENT);
                }
                hBoxLower.getChildren().add(lowerBorder);
            }
            vBox.getChildren().add(hBoxLower);
        }


        vBox.setMinSize(520,520);
        vBox.setPrefSize(520,520);
        vBox.setMaxSize(520,520);
        // adding everything to main vBox for the screen
        vBox1.getChildren().add(vBox);
        vBox1.getChildren().add(initOptions(scene, primaryStage, vBox1, stackPane));
        vBox1.setSpacing(10);
        vBox.setSpacing(0);
        vBox.setAlignment(Pos.CENTER);
        // initiating reaction to Key
        initKeyEvent(stackPane);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this.keyEvent);

        // Start location color setup
        this.squares[this.y][this.x].setBackground(new Background(new BackgroundFill(Color.RED, null, null)));

        // Goal location color setup
        this.squares[this.row - 1][this.col - 1].setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));
    }

    public HBox initOptions(Scene scene, Stage primaryStage, VBox vBox, StackPane stackPane) {
        Label timeLabel = new Label("0:00");
        timeLabel.setFont(new Font(20));
        Button button = new Button("BACK");
        button.setFont(new Font(16));
        // when "BACK" button was clicked, screen gets cleared and new game is generated
        button.setOnMouseClicked(mouseEvent -> {
            this.stopwatch.stop();
            this.stopwatch.getKeyFrames().clear();
            scene.removeEventFilter(KeyEvent.KEY_PRESSED, this.keyEvent);
            stackPane.getChildren().clear();
            startGame(primaryStage, vBox, scene, stackPane);
        });
        CheckBox checkBox = new CheckBox("Show route");
        checkBox.setSelected(true);
        checkBox.setOnAction(actionEvent -> {
            // set it as true for default
            this.show = checkBox.isSelected();
            if(!this.show){
                for(int i = 0; i < this.squares.length; i++){
                    for(int j = 0; j < this.squares[i].length; j++){
                        // initiate all cells with grey color if 'route' is not selected EXCEPT the current cell
                        if(!(i == this.y && j == this.x)) {
                            this.squares[i][j].setBackground(new Background(new BackgroundFill(this.path, null, null)));
                        }
                    }
                }
            } else { // set goal position as green color if 'route' is selected
                this.squares[this.row - 1][this.col - 1].setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));
            }
        });
        HBox hBox = new HBox(timeLabel, button, checkBox);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(25);
        startTiming(timeLabel);
        return hBox;
    }

    public void startTiming(Label timeLabel) {
        // 1000 millis is 1 second
        KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), actionEvent -> {
            this.time++;
            int minutes = this.time / 60;
            int seconds = this.time % 60;
            timeLabel.setText(String.format("%d:%02d", minutes, seconds));
        });
        this.time = 0;
        this.stopwatch.getKeyFrames().add(keyFrame);
        this.stopwatch.setCycleCount(Animation.INDEFINITE);
        this.stopwatch.play();
    }


    public void initKeyEvent (StackPane stackPane) {
        this.keyEvent = keyEvent -> {
            // getting the cell location(name) by using coordinate
            int cellNum = this.y * this.col + this.x + 1;
            // getting the wall information of the above cell
            List<Boolean> cellWalls = maze.getDirection(cellNum);


            // x and y are the index of squares
            if (keyEvent.getCode() == KeyCode.UP) {
                // can't go up in the first row
                // North
                if (this.y > 0 && !cellWalls.get(0)) {
                    update(this.x, this.y - 1, stackPane);
                }
                else if(this.y == 0 && !cellWalls.get(0)){
                    update(this.x, this.row - 1, stackPane);
                }
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                // can't go down in the last row
                // South
                if (this.y < this.row - 1 && !cellWalls.get(2)) {
                    update(this.x, this.y + 1, stackPane);
                }
                else if(this.y == this.row - 1 && !cellWalls.get(2)) {
                    update(this.x, 0, stackPane);
                }

            } else if (keyEvent.getCode() == KeyCode.LEFT) {
                // can't go left in first column
                // West
                if (this.x > 0 && !cellWalls.get(3)) {
                    update(this.x - 1, this.y, stackPane);
                }
                else if(this.x == 0 && !cellWalls.get(3)) {
                    update(this.col - 1, this.y, stackPane);
                }
            } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                // can't go right in last column
                // East
                if (this.x < this.col - 1 && !cellWalls.get(1)) {
                    update(this.x + 1, this.y, stackPane);
                }
                else if(this.x == this.col - 1 && !cellWalls.get(1)) {
                    update(0, this.y, stackPane);
                }
            }
        };
    }

    public void update(int newX, int newY, StackPane stackPane){

        // update the color of the current cell
        this.squares[newY][newX].setBackground(new Background(new BackgroundFill(Color.RED, null, null)));

        if(this.show) { // if 'route' is on, set previous cell as pink color
            this.squares[this.y][this.x].setBackground(new Background(new BackgroundFill(Color.PINK, null, null)));
        } else { // otherwise, set previous cell as grey color
            this.squares[this.y][this.x].setBackground(new Background(new BackgroundFill(this.path, null, null)));
        }

        // update the current cell coordinate
        this.x = newX;
        this.y = newY;

        // Reached GOAL cell
        if(this.x == this.col - 1 && this.y == this.row - 1){
            this.stopwatch.stop();
            this.stopwatch.getKeyFrames().clear();
            Label winLabel = new Label("Good Job!\nYou took " + time + " seconds.");
            winLabel.setTextAlignment(TextAlignment.CENTER);
            winLabel.setFont(new Font(40));
            stackPane.getChildren().add(winLabel);
//            stackPane.getChildren().get(1).setOpacity(-5);\\

            // create new Timeline to show the END banner
            Timeline animation = new Timeline();
            AtomicReference<Double> opacityMsg = new AtomicReference<>(1.0);
            AtomicReference<Double> opacityStackPane = new AtomicReference<>(0.0);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(5), actionEvent -> {
                VBox vBox = (VBox) stackPane.getChildren().get(0);
                vBox.getChildren().get(0).setOpacity(opacityMsg.get());
//                stackPane.getChildren().get(1).setOpacity(opacityStackPane.get());
                opacityStackPane.updateAndGet(v -> v + 0.05);
                opacityMsg.updateAndGet(v -> v - 0.005);
            });
            animation.getKeyFrames().add(keyFrame);
            animation.setCycleCount(150);
            animation.play();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }


}
