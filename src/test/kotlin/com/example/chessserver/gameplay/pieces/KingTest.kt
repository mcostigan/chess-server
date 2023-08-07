package com.example.chessserver.gameplay.pieces

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.move.MoveFactory
import com.example.chessserver.gameplay.patterns.CastleMovementPattern
import com.example.chessserver.gameplay.patterns.MovementPatternFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class KingTest {

    // Mock collaborators
    @Mock
    private lateinit var kingSideRook: Piece

    @Mock
    private lateinit var queenSideRook: Piece

    private var movementPatternFactory: MovementPatternFactory = MovementPatternFactory(MoveFactory())

    // Class under test
    private lateinit var king: King

    @BeforeEach
    fun setUp() {
        // Create the King instance with initial state
        king = King(Pair(0, 0), PieceColor.WHITE, kingSideRook, queenSideRook, movementPatternFactory)

    }

    @Test
    fun `king cannot castle after move`() {
        // Test the king state change from UnmovedKingState to MovedKingState
        var patterns = king.getMovementPatterns()

        assert(patterns.any { it is CastleMovementPattern })
        // Move the king to trigger state change
        king.move(Pair(1, 1))
        patterns = king.getMovementPatterns()

        assert(patterns.none { it is CastleMovementPattern })
    }

}