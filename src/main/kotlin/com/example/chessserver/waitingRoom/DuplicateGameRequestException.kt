package com.example.chessserver.waitingRoom

class DuplicateGameRequestException : Exception("A pending game request already exists for thsi user")