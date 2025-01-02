package chess;

import java.io.Serializable;
import java.util.ArrayList;

class Square implements Serializable{
    public int file;
    public int rank;
    private Piece piece;

    public Square(int file, int rank){
        this.file = file; 
        this.rank = rank;
        this.piece = null;
    }
    public Square(int file, int rank, Piece piece){
        this.file = file;
        this.rank = rank;
        this.piece = piece;
    }
    public Piece getPiece(){
        return this.piece;
    }
    public void setPiece(Piece piece){
        this.piece = piece;
    }
    public String toString() {
		return ""+piece;
	}
    public boolean isAttackedBy(String color, Square[][] board){
        //1 represents white and 8 represents black
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                Piece sq = board[i][j].getPiece();
                if(sq != null && sq.color.equals(color)){
                    //System.out.print(sq);
                    //System.out.print(sq.squares);
                    for(Square attacking: sq.squares){
                        if(attacking==this){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}   
