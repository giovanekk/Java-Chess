//Giovane Kim (GJK67) and Shadmehr Khan (SRK169)
package chess;

import java.util.ArrayList;

import chess.ReturnPiece.PieceFile;
import chess.ReturnPiece.PieceType;

import java.io.Serializable;

class ReturnPiece {
	static enum PieceType {WP, WR, WN, WB, WQ, WK, 
		            BP, BR, BN, BB, BK, BQ};
	static enum PieceFile {a, b, c, d, e, f, g, h};
	
	PieceType pieceType;
	PieceFile pieceFile;
	int pieceRank;  // 1..8
	public String toString() {
		return ""+pieceFile+pieceRank+":"+pieceType;
	}
	public boolean equals(Object other) {
		if (other == null || !(other instanceof ReturnPiece)) {
			return false;
		}
		ReturnPiece otherPiece = (ReturnPiece)other;
		return pieceType == otherPiece.pieceType &&
				pieceFile == otherPiece.pieceFile &&
				pieceRank == otherPiece.pieceRank;
	}
}

class ReturnPlay {
	enum Message {ILLEGAL_MOVE, DRAW, 
				  RESIGN_BLACK_WINS, RESIGN_WHITE_WINS, 
				  CHECK, CHECKMATE_BLACK_WINS,	CHECKMATE_WHITE_WINS, 
				  STALEMATE};
	
	ArrayList<ReturnPiece> piecesOnBoard;
	Message message;
}

public class Chess implements Serializable{
	
	enum Player { white, black }
	static Player turn;
	static Square[][] board;
	static ReturnPlay pb;
	static boolean enpassant;
	static char promotion;
	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
	public static ReturnPlay play(String move) {
		ReturnPlay play = new ReturnPlay();
		play.piecesOnBoard = Chess.pb.piecesOnBoard;
		/* FILL IN THIS METHOD */
		move = move.strip();
		String[] moveArray = move.split(" ");
		
		int initFile = Piece.fileToInt(move.charAt(0));
		int initRank = Piece.rankToInt(move.charAt(1));
		int finalFile = Piece.fileToInt(move.charAt(3));
		int finalRank = Piece.rankToInt(move.charAt(4));
		if(move.equals("resign")){
			if(turn == Player.white){
				play.message = ReturnPlay.Message.RESIGN_BLACK_WINS;
				return play;
			}
			else{
				play.message = ReturnPlay.Message.RESIGN_WHITE_WINS;
				return play;
			}
		}
		else if (move.length() < 5){
			System.out.println("too long");
			play.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return play;
		}
		if((initFile < 0 || initFile > 7) || (initRank < 0 || initRank > 7)||(finalFile < 0 || finalFile > 7)||(finalFile < 0 || finalFile > 7)){
			play.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return play;
		}
		Piece initPiece = board[initRank][initFile].getPiece();
		Piece finalPiece = board[finalRank][finalFile].getPiece();
		if(move.charAt(2) != ' '){	
			play.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return play;
		}
		else if(move.substring(0,2).equals(move.substring(3,5))){
      		play.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return play;
        }
		
		else if(moveArray.length==3 && moveArray[2].equals("draw?")){
			play.message = ReturnPlay.Message.DRAW;
			return play;
		}
		else if(moveArray.length==4 && moveArray[3].equals("draw?")) play.message = ReturnPlay.Message.DRAW;
		else if(board[initRank][initFile].getPiece()==null){
			System.out.println("no piece selected");
			play.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return play;
		}
		else if(turn == Player.white && board[initRank][initFile].getPiece().color.equals("black")){
			System.out.println("white's move, but black piece selected");
			play.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return play;
		}
		else if(turn == Player.black && board[initRank][initFile].getPiece().color.equals("white")){
			System.out.println("black's move, but white piece selected");
			play.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return play;
		}
		else if(finalPiece!=null && initPiece.color==finalPiece.color){
			System.out.println("same color");
			play.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return play;
		}
		else{
			Piece thePiece = board[initRank][initFile].getPiece();
			play = thePiece.move(move, thePiece, board, Chess.pb.piecesOnBoard, turn, board[finalFile][finalRank]);	
			System.out.println(turn+"'s turn");
			for(ReturnPiece p: play.piecesOnBoard){
				if(p instanceof Pawn){
					if(((Pawn)p).enpassant != 'c'){((Pawn)p).enpassant='n';}
					else{((Pawn)p).enpassant++;}
				}
				((Piece)p).updateSquares(board);
			}
			System.out.println("turn:"+turn);
			String pieceColor = (turn == Player.white) ? "black" : "white";
            if(inCheck()){
				System.out.println("in checkaaaaa");
                play.message = ReturnPlay.Message.CHECK;
				//for move to everypossible location and updateSquares and check if king is still being attacked
				boolean hasMovesLeft = false;
				for(int i = 0; i < 8; i++){
					for(int j = 0; j < 8; j++){
						Square attackingSquare = board[i][j];
						Piece rightPiece = attackingSquare.getPiece();
						Piece tempInit = rightPiece;
						if(rightPiece != null && rightPiece.color.equals(pieceColor)){
							for(int k = 0; k < rightPiece.squares.size(); k++){
								Square targetSquare = rightPiece.squares.get(k);
								System.out.println(targetSquare);
								Piece tempTarget = targetSquare.getPiece();
								attackingSquare.setPiece(null);
								targetSquare.setPiece(rightPiece);
								for(ReturnPiece pp: play.piecesOnBoard){
									((Piece)pp).updateSquares(board);
								}
								hasMovesLeft = !inCheck();
								attackingSquare.setPiece(tempInit);
								targetSquare.setPiece(tempTarget);
								for(ReturnPiece pp: play.piecesOnBoard){
									((Piece)pp).updateSquares(board);
								}
								if(hasMovesLeft){
									System.out.println("has moves left"+attackingSquare+targetSquare);
									break;}
							}
							if(hasMovesLeft){break;}
						}
						if(hasMovesLeft){break;}
					} 
            }
			if(!hasMovesLeft && turn == Player.white){
				play.message = ReturnPlay.Message.CHECKMATE_WHITE_WINS;
				return play;
			}
			else if (!hasMovesLeft && turn == Player.black){
				play.message = ReturnPlay.Message.CHECKMATE_BLACK_WINS;
				return play;
			}
			}
			 if(play.message != ReturnPlay.Message.ILLEGAL_MOVE && play.message != ReturnPlay.Message.ILLEGAL_MOVE){
				turn = (turn == Player.white) ? Player.black : Player.white;
			 }
			return play;
		}
		return play;
	}
	public static boolean inCheck(){
		String pieceColor = (turn == Player.white) ? "black" : "white";
			Square kingSquare = null;
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    if(board[i][j].getPiece() != null){
                        if(board[i][j].getPiece().color.equals(pieceColor) && board[i][j].getPiece() instanceof King){
                            kingSquare = board[i][j];
							System.out.println(kingSquare);
                        }
                    }
                }
            }
		String opp = (turn == Player.white) ? "white" : "black";
		System.out.println(opp);
		if(kingSquare.isAttackedBy(opp, board)){
			//System.out.println("in check");
		}
		else{
			//System.out.println("not in check");
		}
		return kingSquare.isAttackedBy(opp, board);
	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		//DON'T KNOW IF I SHOULD KEEP THE FILES AS INTS OR CHANGE THEM TO CHARS
		//Make a new board and populate it
		ReturnPlay pb = new ReturnPlay();
		pb.piecesOnBoard = new ArrayList<ReturnPiece>();
		turn = Player.white;
		Square[][] board = new Square[8][8];
		Chess.board = board;
		Chess.pb = pb;
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				//white rooks:
				if(i==0 && (j==0 || j==7)){
					board[i][j] = new Square(i,j, new Rook("white", i+1, j));
				}
				//white knights:
				else if(i==0 && (j==1 || j==6)){
					board[i][j] = new Square(i,j, new Knight("white", i+1, j));	
				}
				//white bishops:
				else if(i==0 && (j==2 || j==5)){
					board[i][j] = new Square(i,j, new Bishop("white", i+1, j));
				}
				//white queen:
				else if(i==0 && j==3){
					board[i][j] = new Square(i,j, new Queen("white", i+1, j));
				//white king:
				}
				else if(i==0 && j==4){
					board[i][j] = new Square(i,j, new King("white", i+1, j));
				}
				//white pawns:
				else if (i==1){
					board[i][j] = new Square(i, j, new Pawn("white", i+1, j));
				}
				//black pawns:
				else if(i==6){
					board[i][j] = new Square(i,j, new Pawn("black", i+1, j));
				}
				//black rooks:
				else if(i==7 && (j==0 || j==7)){
					board[i][j] = new Square(i,j, new Rook("black", i+1, j));
				}
				//black knights:
				else if(i==7 && (j==1 || j==6)){
					board[i][j] = new Square(i,j, new Knight("black", i+1, j));
				}
				//black bishops:
				else if(i==7 && (j==2 || j==5)){
					board[i][j] = new Square(i,j, new Bishop("black", i+1, j));
				}
				//black queen:
				else if(i==7 && j==3){
					board[i][j] = new Square(i,j, new Queen("black", i+1, j));
				//black king:
				}
				else if(i==7 && j==4){
					board[i][j] = new Square(i,j, new King("black", i+1, j));
				}
				//empty squares:
				else{
					board[i][j] = new Square(i,j);
				}
			}
		}
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				if(board[i][j].getPiece()!=null){
					pb.piecesOnBoard.add(board[i][j].getPiece());
				}
			}
		}
		PlayChess.printBoard(pb.piecesOnBoard);
	}
}

