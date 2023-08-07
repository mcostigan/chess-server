package com.example.chessserver.gameplay.board

import com.example.chessserver.gameplay.check.CheckDetectionService
import com.example.chessserver.gameplay.move.Move
import com.example.chessserver.gameplay.move.MoveExecutor
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor
import com.example.chessserver.gameplay.pieces.PieceFactory

class GameBoard internal constructor(
    squares: MutableList<MutableList<Piece?>>,
    moveExecutor: MoveExecutor,
    checkDetectionService: CheckDetectionService
) : Board(squares, moveExecutor, checkDetectionService) {

    private var pieces: Map<PieceColor, Collection<Piece>> =
        squares.flatMap { it.filterNotNull() }.groupBy { it.getColor() }
    private var hypotheticalBoard: HypotheticalBoard


    init {
        this.hypotheticalBoard = HypotheticalBoard(this, checkDetectionService)
    }

    internal constructor(
        squares: MutableList<MutableList<Piece?>>,
        moveExecutor: MoveExecutor,
        checkDetectionService: CheckDetectionService, hypotheticalBoard: HypotheticalBoard
    ) : this(squares, moveExecutor, checkDetectionService) {
        this.hypotheticalBoard = hypotheticalBoard
    }

    // check if the move exposes the king in the hypothetical board without changing the state of this board or any of its pieces
    override fun exposesKing(move: Move): Boolean = this.hypotheticalBoard.exposesKing(move)

    override fun move(move: Move) {
        super.move(move)
        this.hypotheticalBoard.reset()
    }

    override fun getPiecesByColor(pieceColor: PieceColor): Collection<Piece> =
        pieces[pieceColor]!!.filter { it.isAlive() }

    companion object {
        fun newBoard(
            moveExecutor: MoveExecutor,
            pieceFactory: PieceFactory,
            checkDetectionService: CheckDetectionService,
        ): GameBoard {
            val squares: MutableList<MutableList<Piece?>> = MutableList(8) {
                MutableList(
                    8
                ) { null }
            }

            buildPawnRow(PieceColor.WHITE, squares, pieceFactory)
            buildPawnRow(PieceColor.BLACK, squares, pieceFactory)
            buildBaseRow(PieceColor.WHITE, squares, pieceFactory)
            buildBaseRow(PieceColor.BLACK, squares, pieceFactory)

            return GameBoard(squares, moveExecutor, checkDetectionService)
        }

        private fun buildPawnRow(
            color: PieceColor,
            squares: MutableList<MutableList<Piece?>>,
            pieceFactory: PieceFactory
        ) {
            for (col in 0..7) {
                addPiece(pieceFactory.buildPawn(Pair(color.pawnRow, col), color), squares)
            }
        }

        private fun buildBaseRow(
            color: PieceColor,
            squares: MutableList<MutableList<Piece?>>,
            pieceFactory: PieceFactory
        ) {
            val queenSideCastle = pieceFactory.buildRook(Pair(color.homeRow, 0), color)
            val kingSideCastle = pieceFactory.buildRook(Pair(color.homeRow, 7), color)
            addPiece(queenSideCastle, squares)
            addPiece(kingSideCastle, squares)

            addPiece(pieceFactory.buildKnight(Pair(color.homeRow, 1), color), squares)
            addPiece(pieceFactory.buildKnight(Pair(color.homeRow, 6), color), squares)

            addPiece(pieceFactory.buildBishop(Pair(color.homeRow, 2), color), squares)
            addPiece(pieceFactory.buildBishop(Pair(color.homeRow, 5), color), squares)

            addPiece(pieceFactory.buildQueen(Pair(color.homeRow, 3), color), squares)
            addPiece(pieceFactory.buildKing(Pair(color.homeRow, 4), color, kingSideCastle, queenSideCastle), squares)
        }

        private fun addPiece(piece: Piece, squares: MutableList<MutableList<Piece?>>) {
            val position = piece.getPosition()
            squares[position.first][position.second] = piece
        }
    }
}