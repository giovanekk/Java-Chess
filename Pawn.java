package chess;
import java.io.Serializable;
import java.util.ArrayList;

class Pawn extends Piece implements Serializable{
    public char enpassant;
     public Pawn(String color, int rank, int file){
        super(color, rank, PieceFile.values()[file]);
        this.pieceType = color.equals("white") ? PieceType.WP : PieceType.BP; 
        this.enpassant = 'n';
    }
    
    public ReturnPlay move(String move, Piece piece, Square[][]board,ArrayList<ReturnPiece> p, Chess.Player turn, Square toMove){
        ReturnPlay ret = new ReturnPlay();
        ret.piecesOnBoard = Chess.pb.piecesOnBoard;
        int initFile = Piece.fileToInt(move.charAt(0));
        int initRank = Piece.rankToInt(move.charAt(1));
        int horz = (int)move.charAt(0)-(int)move.charAt(3);
        int vert = (int)move.charAt(1)-(int)move.charAt(4);
        int finalFile = Piece.fileToInt(move.charAt(3));
        int finalRank = Piece.rankToInt(move.charAt(4));
        Piece tempEnp = null;
        boolean enpassanted = false;
        if(this.pieceType == PieceType.WP){
            vert = vert * -1;
        }
        if(vert != 2 && vert != 1){
            ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return ret;
        }
        else if(vert == 1){
            if(horz != 0 && horz != 1 && horz != -1){
                ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
                return ret;
            }
            else if(horz == 1 || horz == -1){
                Piece enpPiece = board[initRank][finalFile].getPiece();
                if(board[finalRank][finalFile].getPiece() == null && (!(enpPiece instanceof Pawn) || ((Pawn)enpPiece).enpassant != 'd')){
                    System.out.println("tried capturing empty");
                    ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
                    return ret;
                }
                else if(board[finalRank][finalFile].getPiece() == null){
                    System.out.println("captured with enpassant");
                    board[initRank][finalFile].setPiece(null);
                    p.remove(enpPiece);
                    ret.piecesOnBoard.remove(enpPiece);
                    tempEnp = enpPiece;
                    enpassanted = true;
                }
            }
            else if(board[finalRank][finalFile].getPiece() != null){
                System.out.println("piece in the way");
                ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
                return ret;
            }
            else{
                System.out.println("actually worked");
            }
        }
        else{
            if(horz != 0){
                System.out.println("horz != 0");
                ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
                return ret;
            }
            int startPos = (this.pieceType == PieceType.WP) ? initRank:7-initRank;
            int dir = (this.pieceType == PieceType.WP) ? 1:-1;
            if(startPos != 1){
                System.out.println("started in wrong square");
                ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
                return ret;
            }
            if(board[initRank+dir][initFile].getPiece() != null || board[initRank+2*dir][initFile].getPiece() != null){
                System.out.println("piece in the way");
                ret.message = ReturnPlay.Message.ILLEGAL_MOVE;
                return ret;
            }
            this.enpassant = 'c';
            System.out.println("passed");

        }
        //promotion case:
        Piece promPiece = piece;
        if(move.charAt(4) == '8' || move.charAt(4) == '1'){
            promPiece = Pawn.promotion(move,piece);
        }
        //in case it is not a promotion:
        Piece tempInit = board[initRank][initFile].getPiece();
        p.remove(piece);
        promPiece.pieceFile = PieceFile.values()[finalFile];
        promPiece.pieceRank = finalRank+1;
        Piece tempFin = board[finalRank][finalFile].getPiece();
        board[initRank][initFile].setPiece(null);
        board[finalRank][finalFile].setPiece(promPiece);
        if(tempFin != null){
            p.remove(tempFin);
            ret.piecesOnBoard.remove(tempFin);
        }
        p.add(promPiece);
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
            piece = board[finalRank][finalFile].getPiece();
            board[initRank][initFile].setPiece(tempInit);
            board[finalRank][finalFile].setPiece(tempFin);
            if(tempFin != null){
                p.add(tempFin);
                ret.piecesOnBoard.add(tempFin);
            }
            tempInit.pieceFile = PieceFile.values()[initFile];
            tempInit.pieceRank = initRank+1;
            if(enpassanted){
                p.add(tempEnp);
                ret.piecesOnBoard.add(tempEnp);
                board[initRank][finalFile].setPiece(tempEnp);
            }
            for(ReturnPiece pp: p){
                ((Piece)pp).updateSquares(board);
            }
            return ret;
        }
        return ret;
    }
    
    public ArrayList<Square> updateSquares(Square[][] board){
        this.squares.clear();
        for(int i = 0; i < 4; i++){
            int[][] directions = {{1,1},{1,-1},{1,0},{2,0}}; //add enpassant
            int j = (this.pieceType == PieceType.WP) ? 1:-1;
            int file = Piece.pieceFileToInt(this.pieceFile);
            int rank = this.pieceRank-1;
            boolean hasMoved = (rank == 1 && this.pieceType == PieceType.WP) || (rank == 6 && this.pieceType == PieceType.BP);
            if(rank+j*directions[i][0] >= 0 && rank+j*directions[i][0] < 8 && file+directions[i][1] >= 0 && file+directions[i][1] < 8){
                if(i < 2){
                    Piece enpPiece = board[rank][file+directions[i][1]].getPiece();
                    if(board[rank+j][file+directions[i][1]].getPiece() != null || (enpPiece != null && enpPiece instanceof Pawn && ((Pawn)enpPiece).enpassant == 'd')){
                        this.squares.add(board[rank+j][file+directions[i][1]]);
                    }
                }
                else if(i == 2){
                    if(board[rank+j][file].getPiece() == null){
                        this.squares.add(board[rank+j][file]);
                    }
                }
                else{
                    if(board[rank+j][file].getPiece() == null && board[rank+2*j][file].getPiece() == null && hasMoved == false){
                        this.squares.add(board[rank+2*j][file]);
                    }
                }
            }
            else{continue;}
        }
        return this.squares;
    }

    public static Piece promotion(String move, Piece piece){
        int file = Piece.fileToInt(move.charAt(3));
        int rank = Piece.rankToInt(move.charAt(4));
        if((move.charAt(4) == '8' || move.charAt(4) == '1') && move.length()<6){
            //promote to queen
            piece = new Queen(piece.color,file, rank);
            piece.pieceType = piece.color.equals("white") ? PieceType.WQ : PieceType.BQ;
            return piece;
        }
        else if((move.charAt(4) == '8' || move.charAt(4) == '1') && move.length()>5){
            //promote to specified piece
            //fix this to implement integers instead of characters
            switch(move.charAt(6)){
                case 'Q':
                    piece = new Queen(piece.color,file,rank); //add color
                    piece.pieceType = piece.color.equals("white") ? PieceType.WQ : PieceType.BQ;
                    return piece;
                case 'R':
                    piece = new Rook(piece.color,file,rank); //add color
                    piece.pieceType = piece.color.equals("white") ? PieceType.WR : PieceType.BR;
                    return piece;
                case 'B':
                    piece = new Bishop(piece.color,file,rank); //add color
                    piece.pieceType = piece.color.equals("white") ? PieceType.WB : PieceType.BB;
                    return piece;
                case 'N':
                    piece = new Knight(piece.color,file,rank); 
                    piece.pieceType = piece.color.equals("white") ? PieceType.WN : PieceType.BN;
                    return piece;
            }
        }
        return piece;
    }
}