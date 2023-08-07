# Chess

This is a server for a chess application which provides a REST and Web Socket API to allow users to play each other in a
real-time game

## API

### REST
Method: `POST`

Endpoint: `/game`

Body: ```
    {minExperience: String, maxExperience: String}```

### Web Socket
Users send and receive moves via a web-socket connection, implemented with Stomp. Users can publish and subscribe to the below topics:
    
* /game/{gameId}/move
  * send and receive moves that have been committed to the game
* /game/{gameId}/moves
  * receive current available moves given the state of the board


## Gameplay
### Layers
#### Game
The `Game` class maintains state on the game, such as players, turn, status, and board
#### Board
The `Board` class maintains the board state, including pieces and positions.
##### Hypothetical Board
The `HypotethicalBoard` is an implementation of `Board` that allows for checking the state of the board after a move has been executed. This is useful for check detection. The hypothetical board can be reset to the state of the gameplay board.
#### Piece
The `Piece` class and its subclasses are responsible for defining the movement patterns available to a piece
#### Movement Pattern Layer
The `MovementPattern` class implements all the possible movement patterns of a piece
#### Move
The `Move` class defines possible moves given the current state of the game. Moves are implemented with the decorator design pattern to accommodate complexities such as attacks, promotions, and castling. Eventually, a `Move` is given to the game for execution.

##### Move Executor
The `MoveExectuor` classes are responsible for committing a move to a board.

#### Check Detection
The `CheckDetectionService` is responsible for determining if the king can be attacked, which is used in determining the validity of a move and the status of a game.

## Running the application

The easiest way to run the application is with

```
docker compose up
```

This runs two docker containers: one for the API (exposed on localhost:8080) and one for MongoDB

NOTE: the Dockerfile copies over the application's `.jar` file. If you are running for the first time or have made
changes to the code, you should run

```
mvn package
```

before building the image and running the container