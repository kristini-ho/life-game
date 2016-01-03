
import java.awt.Color;
import java.util.*;
import java.io.*;
public class Life{        
    public void runLife(Color [][] board, Color color) {     
            int row = board.length;
            int col = board[0].length;
            int[][] toDie = new int[row][col];
            for(int r = 1; r < row; r++){
                for(int c = 1; c < col; c++){                                                       
                    int neighbor = countNeighbors(board, r, c);         
                    if(neighbor == 0 || neighbor == 1 || neighbor >= 4) toDie[r][c] = -1;
                    if(neighbor == 3) toDie[r][c] = 1;  
                }
            }
            for(int r = 1; r < row; r++){
                for(int c = 1; c < col; c++){                                                       
                    if(toDie[r][c] == -1) board[r][c] = Color.WHITE;  
                    if(toDie[r][c] == 1) board[r][c] = color;   
                }
            }
                
    }   
    private boolean inBoundNeigh(Color[][] board, int row, int col){
        if(row < 1 || row > 20) return false;
        if(col < 1 || col > 20) return false;      
        return(board[row][col] != Color.WHITE);             
    }
    private int countNeighbors(Color[][] board, int r, int c){
        int neighbor = 0;
        if(inBoundNeigh(board, r-1, c)) neighbor++;
        if(inBoundNeigh(board, r+1, c)) neighbor++;
        if(inBoundNeigh(board, r, c-1)) neighbor++;
        if(inBoundNeigh(board, r, c+1)) neighbor++;
        if(inBoundNeigh(board, r-1, c-1)) neighbor++;
        if(inBoundNeigh(board, r-1, c+1)) neighbor++;
        if(inBoundNeigh(board, r+1, c-1)) neighbor++;
        if(inBoundNeigh(board, r+1, c+1)) neighbor++;     
        return neighbor;
    }
       
}