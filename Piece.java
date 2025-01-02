package chess;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Piece extends ReturnPiece implements Serializable{
    public ArrayList<Square> squares;
    public String color;
    public Piece(String color, int pieceRank, PieceFile pieceFile){
        super();
        this.squares = new ArrayList<Square>();
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
        this.color = color;
    }
    public abstract ReturnPlay move(String move, Piece piece, Square[][]board, ArrayList<ReturnPiece> p, Chess.Player turn, Square toMove);

    public abstract ArrayList<Square> updateSquares(Square[][] board);

    public static int fileToInt(char file){
        return (int)file - (int)'a';
    }
    public static int rankToInt(char rank){
        return (int)rank - (int)'1';
    }
    public static int pieceFileToInt(PieceFile file){
        return file.ordinal();
    }
}

/*
 * e2 e4
 * e7 e5
 * g1 f3
 * b8 c6
 * f1 c4
 * g8 f6
 * e1 g1
 */