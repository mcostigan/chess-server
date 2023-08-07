package com.example.chessserver.gameplay.board

import com.example.chessserver.gameplay.check.CheckDetectionService
import com.example.chessserver.gameplay.move.HypotheticalMoveExecutor
import com.example.chessserver.gameplay.move.Move
import com.example.chessserver.gameplay.move.MoveExecutor
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
internal abstract class BoardTest {
    abstract val squares: MutableList<MutableList<Piece?>>
    abstract val moveExecutor: MoveExecutor
    abstract val checkDetectionService: CheckDetectionService
    abstract val board: Board


    protected val mockWhite: Piece = mock()
    protected val mockBlack: Piece = mock()

    protected val validMove: Move = mock()
    protected val invalidMove: Move = mock()

    init {

        whenever(mockWhite.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockWhite.getPosition()).thenReturn(Pair(0, 0))
        whenever(mockWhite.isAlive()).thenReturn(true)

        whenever(mockBlack.getColor()).thenReturn(PieceColor.BLACK)
        whenever(mockBlack.getPosition()).thenReturn(Pair(7, 0))
        whenever(mockBlack.isAlive()).thenReturn(true)
    }

    @Test
    fun getSize() {
        assert(board.getSize() == 8)
    }

    @Test
    fun `should return null if no piece at position`() {
        assert(board.getPiece(Pair(2, 0)) == null)
    }

    @Test
    fun `should return piece at position`() {
        val result = board.getPiece(Pair(0, 0))
        assert(result?.getPosition() == Pair(0, 0))
        assert(result?.getColor() == PieceColor.WHITE)
    }

    @Test
    open fun `should defer to move executor on move`() {
        board.move(mock())
        verify(moveExecutor).executeMove(any(), any())
    }

    @Test
    open fun getPiecesByColor() {
        val pieces = board.getPiecesByColor(PieceColor.WHITE)
        assert(pieces.size == 1)
        assert(pieces.all { it.getColor() == PieceColor.WHITE })
    }

    @Test
    open fun `exposes king should return false if not in check`() {
        val mockMove: Move = mock()
        whenever(mockMove.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockMove.isCastle()).thenReturn(false)

        whenever(checkDetectionService.isCheck(PieceColor.WHITE, board)).thenReturn(false)
        assert(!board.exposesKing(mockMove))

        verify(checkDetectionService).isCheck(PieceColor.WHITE, board)
    }

    @Test
    open fun `exposes king should return true if in check`(){
        val mockMove: Move = mock()
        whenever(mockMove.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockMove.isCastle()).thenReturn(false)

        whenever(checkDetectionService.isCheck(PieceColor.WHITE, board)).thenReturn(true)
        assert(board.exposesKing(mockMove))

        verify(checkDetectionService).isCheck(PieceColor.WHITE, board)
    }

    @Test
    open fun `exposes king returns true if king moves into check on castle`() {
        val mockMove: Move = mock()
        whenever(mockMove.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockMove.isCastle()).thenReturn(true)

        whenever(checkDetectionService.isCheck(PieceColor.WHITE, board)).thenReturn(true)
        assert(board.exposesKing(mockMove))

        verify(checkDetectionService).isCheck(PieceColor.WHITE, board)
    }

    @Test
    open fun `exposes king returns true if king starts in check on castle`() {
        val mockMove: Move = mock()
        whenever(mockMove.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockMove.isCastle()).thenReturn(true)
        whenever(mockMove.getFrom()).thenReturn(Pair(0, 4))
        whenever(mockMove.getTo()).thenReturn(Pair(0, 2))

        whenever(checkDetectionService.isCheck(PieceColor.WHITE, board)).thenReturn(false)
        whenever(checkDetectionService.canAttack(PieceColor.BLACK, Pair(0, 4), board)).thenReturn(true)

        assert(board.exposesKing(mockMove))

    }

    @Test
    open fun `exposes king returns true if king moves through check on castle`() {
        val mockMove: Move = mock()
        whenever(mockMove.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockMove.isCastle()).thenReturn(true)
        whenever(mockMove.getFrom()).thenReturn(Pair(0, 4))
        whenever(mockMove.getTo()).thenReturn(Pair(0, 2))

        whenever(checkDetectionService.isCheck(PieceColor.WHITE, board)).thenReturn(false)
        whenever(checkDetectionService.canAttack(PieceColor.BLACK, Pair(0, 4), board)).thenReturn(false)
        whenever(checkDetectionService.canAttack(PieceColor.BLACK, Pair(0, 3), board)).thenReturn(true)

        assert(board.exposesKing(mockMove))

    }

    @Test
    open fun `exposes king returns false for valid castle move`() {
        val mockMove: Move = mock()
        whenever(mockMove.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockMove.isCastle()).thenReturn(true)
        whenever(mockMove.getFrom()).thenReturn(Pair(0, 4))
        whenever(mockMove.getTo()).thenReturn(Pair(0, 2))

        whenever(checkDetectionService.isCheck(PieceColor.WHITE, board)).thenReturn(false)
        whenever(checkDetectionService.canAttack(PieceColor.BLACK, Pair(0, 4), board)).thenReturn(false)
        whenever(checkDetectionService.canAttack(PieceColor.BLACK, Pair(0, 3), board)).thenReturn(false)

        assert(!board.exposesKing(mockMove))

    }

    @Test
    fun isCheck() {
        whenever(checkDetectionService.isCheck(PieceColor.WHITE, board)).thenReturn(true)

        assert(board.isCheck(PieceColor.WHITE))
        verify(checkDetectionService).isCheck(PieceColor.WHITE, board)
    }
}

internal class GameBoardTest : BoardTest() {
    override val squares: MutableList<MutableList<Piece?>> = MutableList(8) { MutableList(8) { null } }
    override val moveExecutor: MoveExecutor = mock()
    override val checkDetectionService: CheckDetectionService = mock()
    override val board: Board

    private val hypotheticalBoard: HypotheticalBoard

    init {

        squares[0][0] = mockWhite
        squares[7][0] = mockBlack
        hypotheticalBoard = mock()
        board = GameBoard(squares, moveExecutor, checkDetectionService, hypotheticalBoard)
    }

    @Test
    override fun `should defer to move executor on move`() {
        super.`should defer to move executor on move`()

        // should reset the hypothetical board after a move
        verify(hypotheticalBoard).reset()
    }

    @Test
    override fun `exposes king should return false if not in check`() {
        whenever(hypotheticalBoard.exposesKing(any())).thenReturn(false)
        assert(!board.exposesKing(mock()))
    }

    override fun `exposes king should return true if in check`() {
        whenever(hypotheticalBoard.exposesKing(any())).thenReturn(true)
        assert(board.exposesKing(mock()))
    }

    override fun `exposes king returns true if king moves into check on castle`() {
        whenever(hypotheticalBoard.exposesKing(any())).thenReturn(true)
        assert(board.exposesKing(mock()))
    }

    override fun `exposes king returns true if king starts in check on castle`() {
        whenever(hypotheticalBoard.exposesKing(any())).thenReturn(true)
        assert(board.exposesKing(mock()))

    }

    override fun `exposes king returns true if king moves through check on castle`() {
        whenever(hypotheticalBoard.exposesKing(any())).thenReturn(true)
        assert(board.exposesKing(mock()))
    }

    override fun `exposes king returns false for valid castle move`() {
        whenever(hypotheticalBoard.exposesKing(any())).thenReturn(false)
        assert(!board.exposesKing(mock()))
    }

    @Test
    fun `gets available moves that do not expose king`(){
        whenever(hypotheticalBoard.exposesKing(invalidMove)).thenReturn(true)
        whenever(hypotheticalBoard.exposesKing(validMove)).thenReturn(false)
        whenever(mockWhite.getAvailableMoves(board)).thenReturn(listOf(validMove, invalidMove))

        val moves = board.availableMoves(PieceColor.WHITE)
        assert(moves.size == 1)
        assert(moves.contains(validMove))
    }

}

internal class HypotheticalBoardTest : BoardTest() {
    override val squares: MutableList<MutableList<Piece?>> = mock()
    override val moveExecutor: HypotheticalMoveExecutor = mock()
    override val checkDetectionService: CheckDetectionService = mock()
    override val board: Board

    private val mockGameplayBoard: GameBoard = mock()

    init {

        whenever(mockGameplayBoard.getPiece(any())).thenReturn(null)
        whenever(mockGameplayBoard.getPiece(Pair(0, 0))).thenReturn(mockWhite)
        whenever(mockGameplayBoard.getPiece(Pair(7, 0))).thenReturn(mockBlack)
        whenever(mockGameplayBoard.getPiecesByColor(PieceColor.WHITE)).thenReturn(listOf(mockWhite))

        board = HypotheticalBoard(moveExecutor, checkDetectionService).apply {
            setGameplayBoard(mockGameplayBoard)
        }
    }

    @Test
    override fun getPiecesByColor() {
        super.getPiecesByColor()

        // should retrieve pieces from the parent gameplay board
        verify(mockGameplayBoard).getPiecesByColor(PieceColor.WHITE)
    }

    @Test
    override fun `exposes king should return false if not in check`() {
        super.`exposes king should return false if not in check`()
        verify(moveExecutor).executeMove(any(), any())
    }

    @Test
    override fun `exposes king should return true if in check`() {
        super.`exposes king should return false if not in check`()
        verify(moveExecutor).executeMove(any(), any())
    }

    @Test
    override fun `exposes king returns true if king moves into check on castle`() {
        super.`exposes king returns true if king moves into check on castle`()
        verify(moveExecutor).executeMove(any(), any())
    }

    @Test
    override fun `exposes king returns true if king starts in check on castle`() {
        super.`exposes king returns true if king starts in check on castle`()
        verify(moveExecutor).executeMove(any(), any())

    }

    @Test
    override fun `exposes king returns true if king moves through check on castle`() {
        super.`exposes king returns true if king moves through check on castle`()
        verify(moveExecutor).executeMove(any(), any())

    }

    @Test
    override fun `exposes king returns false for valid castle move`() {
        super.`exposes king returns false for valid castle move`()
        verify(moveExecutor).executeMove(any(), any())

    }

}