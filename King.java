package chess;
import java.io.Serializable;
import java.util.ArrayList;

class King extends Piece implements Serializable{
    public boolean hasMoved;
    public boolean inCheck;
    public King(String color, int rank, int file){
        super(color, rank, PieceFile.values()[file]);
        this.pieceType = color.equals("white") ? PieceType.WK : PieceType.BK;
        this.hasMoved = false;
        this.inCheck = false;
    }
    
    public ReturnPlay move(String move, Piece piece, Square[][]board, ArrayList<ReturnPiece> p, Chess.Player turn, Square toMove){
        ReturnPlay ret = new ReturnPlay();
        ret.piecesOnBoard = Chess.pb.piecesOnBoard;
        int initFile = Piece.fileToInt(move.charAt(0));
        int initRank = Piece.rankToInt(move.charAt(1));
        int finalFile = Piece.fileToInt(move.charAt(3));
        int finalRank = Piece.rankToInt(move.charAt(4));
        boolean illeg = true;
        boolean castle = false;
        this.updateSquares(board);

        if(!this.hasMoved && initRank-finalRank == 0 && Math.abs(finalFile-initFile) == 2){
            int dir = (finalFile  == 2)? 0:7;
            int start =  (finalFile  == 2)? 1:5;
            int stop =  (finalFile  == 2)? 4:7;
            Piece rook = board[initRank][dir].getPiece();
            String opp = (this.color.equals("white")) ? "black":"white";//originally white
            if(rook == null || !(rook instanceof Rook) || ((Rook)rook).hasMoved){
                System.out.println("Rook is null or has moved");
                ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
                return ret;
            }
            if(board[initRank][4].isAttackedBy(opp,board)){
                System.out.println("King is in check");
                ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
                return ret;
            }
            boolean blocked = false;
            for(int i = start; i < stop; i++){
                if(board[initRank][i].isAttackedBy(opp,board)){
                    blocked = true;
                    break;
                }
                if(board[initRank][i].getPiece() != null){
                    blocked = true;
                    break;
                }
            }
            if(!blocked){
                castle = true;
                int rookFile = (finalFile  == 2)? 3:5;
                p.remove(rook);
                ret.piecesOnBoard.remove(rook);
                rook.pieceFile = PieceFile.values()[rookFile];
                rook.pieceRank = finalRank+1;
                board[finalRank][rookFile].setPiece(rook);
                board[initRank][dir].setPiece(null);
                p.add(rook);
                ret.piecesOnBoard.add(rook);
                rook.updateSquares(board);
                if(((Rook)rook).hasMoved == false){((Rook)rook).hasMoved = true;}
            }
            else{
                System.out.println("being blocked");
                ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
                return ret;
            
            }
        }
            
        
        
        for(Square sq: this.squares){
            if(board[finalRank][finalFile]==sq){
                illeg = false;
                break;
            }
        }
        if(illeg && !castle){
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
            if(this.hasMoved == false){this.hasMoved = true;}
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
            if(rank+j*directions[i][0] >= 0 && rank+j*directions[i][0] < 8 && file+j*directions[i][1] >= 0 && file+j*directions[i][1] < 8 &&
                (board[rank+j*directions[i][0]][file+j*directions[i][1]].getPiece() == null ||
                 !board[rank+j*directions[i][0]][file+j*directions[i][1]].getPiece().color.equals(this.color))){
                    this.squares.add(board[rank+j*directions[i][0]][file+j*directions[i][1]]);
            }
        }
        return this.squares;
    }

    // public ReturnPlay castles(String move, ReturnPiece king, ReturnPiece rook){
    //     //fix this function to implement integers instead of characters
    //     ReturnPlay ret = new ReturnPlay();
    //     return ret;
    // }
}