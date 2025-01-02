package chess;
import java.io.Serializable;
import java.util.ArrayList;

class Queen extends Piece implements Serializable{
    public Queen(String color, int rank, int file){
        super(color, rank, PieceFile.values()[file]);
        this.pieceType = color.equals("white") ? PieceType.WQ : PieceType.BQ; 
    }
    public ReturnPlay move(String move, Piece piece, Square[][]board, ArrayList<ReturnPiece> p, Chess.Player turn, Square toMove){
        ReturnPlay ret = new ReturnPlay();
        ret.piecesOnBoard = Chess.pb.piecesOnBoard;
        int finalFile = Piece.fileToInt(move.charAt(3));
        int finalRank = Piece.rankToInt(move.charAt(4));
        int initFile = Piece.pieceFileToInt(this.pieceFile);
        int initRank = this.pieceRank-1;

        boolean illeg = true;
        this.updateSquares(board);
        for(Square sq: this.squares){
            if(board[finalRank][finalFile]==sq){
                illeg = false;
                break;
            }
        }
        if(illeg){
            System.out.println("Illegal Q move");
            ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
        }
        else{
            Piece tempInit = board[initRank][initFile].getPiece();
            p.remove(piece);
            piece.pieceFile = PieceFile.values()[finalFile];
            piece.pieceRank = finalRank+1;
            Piece tempFin = board[finalRank][finalFile].getPiece();
            board[finalRank][finalFile].setPiece(piece);
            board[initRank][initFile].setPiece(null);
            if(tempFin != null){
                p.remove(tempFin);
                ret.piecesOnBoard.remove(tempFin);
                System.out.println(tempFin);
            }
            p.add(piece);
            for(ReturnPiece pp: p){
                ((Piece)pp).updateSquares(board);
            }

            Square kingSquare = null;
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    if(board[i][j].getPiece() != null){
                        if(board[i][j].getPiece().color.equals(this.color) && board[i][j].getPiece() instanceof King){
                            kingSquare = board[i][j];
                        }
                    }
                }
            }
            String opp = this.color.equals("white") ? "black":"white";
            if(kingSquare.isAttackedBy(opp, board)){
                ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
                board[initRank][initFile].setPiece(tempInit);
                board[finalRank][finalFile].setPiece(tempFin);
                if(tempFin != null){
                    p.add(tempFin);
                    ret.piecesOnBoard.add(tempFin);
                }
                tempInit.pieceFile = PieceFile.values()[initFile];
                tempInit.pieceRank = initRank+1;
                for(ReturnPiece pp: p){
                    ((Piece)pp).updateSquares(board);
                }
                return ret;
            }
        }
        return ret;
    }
    public ArrayList<Square> updateSquares(Square[][] board){
        this.squares.clear();
        for(int i = 0; i < 8; i++){
            int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,-1},{1,-1},{-1,1}};
            int j = 1;
            int file = Piece.pieceFileToInt(this.pieceFile);
            int rank = this.pieceRank-1;
            while(rank+j*directions[i][0] >= 0 && rank+j*directions[i][0] < 8 && file+j*directions[i][1] >= 0 && file+j*directions[i][1] < 8){
                if(board[rank+j*directions[i][0]][file+j*directions[i][1]].getPiece() == null){
                    this.squares.add(board[rank+j*directions[i][0]][file+j*directions[i][1]]);
                    j++;
                }
                else if(board[rank+j*directions[i][0]][file+j*directions[i][1]].getPiece().color.equals(this.color)){
                    break;
                }
                else{
                    this.squares.add(board[rank+j*directions[i][0]][file+j*directions[i][1]]);
                    break;
                }
            }
        }
        // for(int i = 0; i<squares.size(); i++){
        //     System.out.println(squares.get(i));
        // }

        return this.squares;
    }
}