/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamemypart;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author Buton
 */

public class  FXMLDocumentController implements Initializable {

	private static final int Column = 8;
	private static final int Row = 7;
	private static final int Circle_Diameter = 150;
	private static final String discColour1 = "#78dda6";
	private static final String discColour2 = "#134e2e";
        private static final String white = "#ffffff";

        private boolean isPlayerOneTurn = true;
        public boolean isMachineMode = true;
        
	private static String playerOne = "Player one";
	private static String playerTwo = "Player two";

	private Disc[][] insertedDiscsArray = new Disc[Row][Column];

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label playerOneLabel;

	@FXML
	public Label turnLabel;

	private boolean isAllowedToInsert = true;

	public void createPlayground() {

		Shape rectangleShape = createGameStructuralGrid();
		rootGridPane.add(rectangleShape, 0, 1);

		List<Rectangle> rectangleList = createClickableColumns();

                rectangleList.forEach(rectangle -> {
                    rootGridPane.add(rectangle, 0, 1);
            });
	}

	private Shape createGameStructuralGrid() {

		Shape rectangleShape = new Rectangle((Column+1) * Circle_Diameter, (Row + 1) * Circle_Diameter);
		for ( int row = 0; row < Row; row++) {
			for (int col = 0; col < Column; col++) {

				Circle circle = new Circle();
				circle.setRadius(Circle_Diameter / 2);
				circle.setCenterX(Circle_Diameter / 2);
				circle.setCenterY(Circle_Diameter / 2);
				circle.setSmooth(true);

				circle.setTranslateX(col * (Circle_Diameter + 5) + Circle_Diameter / 4);
				circle.setTranslateY(row * (Circle_Diameter + 5) + Circle_Diameter / 4);

				rectangleShape = Shape.subtract(rectangleShape, circle);
			}
		}
		rectangleShape.setFill(Color.WHITE);

		return rectangleShape;
	}

	private List<Rectangle> createClickableColumns() {

		List<Rectangle> rectangleList = new ArrayList<>();

		for (int col = 0; col < Column; col++) {
			Rectangle rectangle = new Rectangle(Circle_Diameter, (Row + 1) * Circle_Diameter);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (Circle_Diameter + 5) + Circle_Diameter / 4);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			final int column = col;
                        
                        rectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                if (isAllowedToInsert) {
                                    isAllowedToInsert = false;
                                    insertDisc(new Disc(isPlayerOneTurn), column);
                                    
                                     
                                        
                                    }
                                
                            }
                        });
               
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertDisc(Disc disc, int column){
		int row = Row -1;

		while(row >= 0) {
			if (getDiscIfPresent(row, column) == null)
				break;
				row--;
		}
		if(row < 0)
			return;


		insertedDiscsArray[row][column] = disc;
		insertedDiscPane.getChildren().add(disc);
		int currentRow = row;

		disc.setTranslateX(column * (Circle_Diameter + 5) + Circle_Diameter / 4);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (Circle_Diameter + 5) + Circle_Diameter / 4);

		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true;
			if (gameEnded(currentRow, column)) {
				gameOver();
                                isAllowedToInsert = false;
				return;
			}
			isPlayerOneTurn = !isPlayerOneTurn;

			playerOneLabel.setText(isPlayerOneTurn? playerOne : playerTwo);
                        
                        if(isMachineMode&&(!isPlayerOneTurn)){
                             try{
                             Random rand = new Random();
                             Thread.sleep(1000);
                             insertDisc(new Disc(isPlayerOneTurn), rand.nextInt(Column));
                             } catch (InterruptedException e){  }
                        }
                      
		});
		translateTransition.play();
                
                  
	}

	private boolean gameEnded(int row, int column){

		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)
										.mapToObj(r-> new Point2D(r, column))
										.collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
				.mapToObj(c-> new Point2D(row, c))
				.collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row - 3, column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
									.mapToObj(i -> startPoint1.add(i, -i))
									.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint2.add(i, i))
				.collect(Collectors.toList());

		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
						|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;

		for (Point2D point: points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {

				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
					chain = 0;
				}

			}
		return false;
		}

	private Disc getDiscIfPresent(int row, int column) {
		if (row >= Row || row<0 || column >= Column || column <0)
			return null;

			return insertedDiscsArray[row][column];

	}

	private void gameOver() {
		String winner = isPlayerOneTurn ? playerOne : playerTwo;
		System.out.println("Winner is: " + winner);
                
                JOptionPane.showMessageDialog(null, "Winner is: " + winner, "Connect4", JOptionPane.INFORMATION_MESSAGE);
                
                playerOneLabel.setText("Winner is:");
                turnLabel.setText(winner);
                turnLabel.underlineProperty().set(true);
	}

	public void resetGame() {
		insertedDiscPane.getChildren().clear();

            for (Disc[] insertedDiscsArray1 : insertedDiscsArray) {
                for (int col = 0; col < insertedDiscsArray1.length; col++) {
                    insertedDiscsArray1[col] = null;
                }
            }
		isPlayerOneTurn = true;
		playerOneLabel.setText(playerOne);

		createPlayground();
	}

	private static class Disc extends Circle {

		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove) {
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(Circle_Diameter/2);
			setFill(isPlayerOneMove? Color.valueOf(discColour1): Color.valueOf(discColour2));
			setCenterX(Circle_Diameter/2);
			setCenterY(Circle_Diameter/2);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
