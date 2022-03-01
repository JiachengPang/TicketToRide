# Train.com

Directory layout:

```
|--Trains
|  |--Admin
|  |  |--referee-game-state.java
|  |--Common
|  |  |--map.java
|  |  |--player-game-state.java
|  |--Editor
|  |  |--map-editor.java
|  |--Planning
|  |  |--map-design.md
|  |  |--plan-analysis.md
|  |  |--visual.md
|  |  |--player-interface.md
|  |  |--game-state.md
|  |--Other/source_code/Trains
|  |  |  |--src
|  |  |  |  |--Common
|  |  |  |  |  |--City.java
|  |  |  |  |  |--Connection.java
|  |  |  |  |  |--Coord.java
|  |  |  |  |  |--Destination.java
|  |  |  |  |  |--ICity.java
|  |  |  |  |  |--IConnection.java
|  |  |  |  |  |--IDestination.java
|  |  |  |  |  |--TrainMap.java
|  |  |  |  |  |--map.java
|  |  |  |  |  |--IGeneralGameState.java
|  |  |  |  |  |--GeneralGameState.java
|  |  |  |  |  |--IPlayerGameState.java
|  |  |  |  |  |--PlayerGameState.java
|  |  |  |  |--Admin
|  |  |  |  |  |--IRefereeGameState.java
|  |  |  |  |  |--RefereeGameState.java
|  |  |  |  |--Utils
|  |  |  |  |  |--ConnectionParser.java
|  |  |  |  |  |--MapParser.java
|  |  |  |  |  |--PGSParser.java
|  |  |  |  |--Editor
|  |  |  |  |  |--IMapView.java
|  |  |  |  |  |--MapView.java
|  |  |  |  |  |--MapPanel.java
|  |  |  |  |--main.java
|  |  |  |  |--Player
|  |  |  |  |  |--BuyNowStrategy.java
|  |  |  |  |  |--CityPairComparator.java
|  |  |  |  |  |--ConnectionComparator.java
|  |  |  |  |  |--Hold10Strategy.java
|  |  |  |  |  |--ICityComparator.java
|  |  |  |  |  |--Strategy.java
|  |  |  |  |  |--StrategyReturn.java
|  |  |  |--Tests
|  |  |  |  |--Common
|  |  |  |  |  |--CityTest.java
|  |  |  |  |  |--ConnectionTest.java
|  |  |  |  |  |--CoordTest.java
|  |  |  |  |  |--DestinationTest.java
|  |  |  |  |  |--GeneralGameStateTest.java
|  |  |  |  |  |--PlayerGameStateTest.java
|  |  |  |  |  |--mapTest.java
|  |  |  |  |--Admin
|  |  |  |  |  |--RefereeGameStateTest.java
|  |  |  |--xmap
|  |  |  |  |--src
|  |  |  |  |  |--xmap.java
|  |  |  |--xvisualize
|  |  |  |  |--src
|  |  |  |  |  |--xvisualize.java
|  |  |  |--xlegal
|  |  |  |  |--src
|  |  |  |  |  |--xlegal.java
|  |  |  |--out
|  |  |  |  |--artifacts
|  |  |  |  |  |--xmap_jar
|  |  |  |  |  |  |--xmap.jar
|  |  |  |  |  |--xvisualize_jar
|  |  |  |  |  |  |--xvisualize.jar
|  |  |  |  |  |--xlegal_jar
|  |  |  |  |  |  |--xlegal.jar

```

# Map

The design consists of 4 interfaces:  
A TrainMap of the board keeps track of the following information:
* A dimension
* All cities
* All connections between cities

A City keeps track of its name and coordinate.  
A Connection keeps track of its endpoints, color, and number of segments.  
A Destination keeps track of its two cities.  

A TrainMap object is the top level interface that provides methods for getting information about the map.

Files:
The map.java file in Trains/Common stores a link to the source code of the map implementation. All source code resides in Trains/Other/source_code/Trains/src folder.  
The 4 interfaces are in: ICity.java, IConnection.java, IDestination.java, and TrainMap.java.  
The implementations of these interfaces are in: City.java, Connection.java, Destination.java, and map.java.  
The utility class Coord.java is for storing a coordinate on the map.  

All unit tests are in Trains/Other/Tests folder. All functionalities are tested and behaved as expected.

# MapView
A MapView class is located in the Trains/Other/source_code/Trains/src/Editor directory. It implements the IMapView interface, and has the purpose of consuming the map and produces a visualization of the map in a JFrame using Java Swing. The MapView utilizes the MapPanel class to draw the map components in a JPanel.

# xmap
The test harness source code is in Trains/Other/source_code/Trains/xmap/src. It takes a well-formed JSON file that contains 2 city names and information about the TrainMap object. It constructs a TrainMap and asks the map if the given 2 cities has a path. The executable file xmap is in arches/3/.

# GameState
There are 3 interfaces representing a game state:
* IGeneralGameState: keeps track of information that is visible to all parties, including all players and the referee
  - the board
  - number of cards left in the deck
  - acquired connections with player id
  - all player ids
  - turn order
  - the current turn
  - all connections available to acquire for the current player
* IPlayerGameState extends IGeneralGameState: keeps track of information that is visible to a particular player
  - the IGeneralGameState
  - the colored cards in the player's hand, hidden from other players
  - number of rails left
  - destinations, hidden from other players
* IRefereeGameState extends IGeneralGameState: keeps track of information that is visible to a referee (all information)
  - the IGeneralGameState
  - the deck
  - hand of every player
  - number of rails of every player
  - destinations of every player

An IGeneralGameState can decide all available connections for the player in the current turn. An IRefereeGameState can decide if an acquisiton of a connection is legal for the player in the current turn.

Source code of IGeneralGameState and IPlayerGameState and their implementations are located under Trains/Other/source_code/Trains/src/Common, while IRefereeGameState and its implementation are located under Trains/Other/source_code/Trains/src/Admin.

# xvisualize
The xvisualize program takes in a JSON string representing a map object and displays the map. It converts the JSON string into a TrainMap and creates a IMapView object. The program is under directory 4, with a map ready to be displayed under 4/Vis. The source code is under Trains/Other/source_code/Trains/xvisualize/src.

# Strategy
A Strategy object makes decisions for players:
 - which destination cards to get
 - what to do in a turn  

2 Strategies are implemented: Hold10Strategy and BuyNowStrategy.  

Hold10:
  - pick the first 2 destinations in lexicographic order
  - each turn, if the player is holding less than or equal to 10 cards, request cards.
    Otherwise, buy connection.
  - buy the first connection in lexicographic order

BuyNow:
  - pick the first 2 destinations in lexicographic order
  - each turn, buy a connection if possible, otherwise, request cards.
  - buy the first connection in lexicographic order

The makeMove method in a Strategy returns a StrategyReturn object, which is a holder for 2 values: boolean requestCards and IConnection connection, as a player might request more cards or obtain a connection in a turn. One of the values is guaranteed to be what the player should do in their turn. If requestCards is true, the player should request more cards; if connection is not null, the player should obtain that connection.

All code is under Trains/Other/source_code/Trains/src/Player.

# xlegal
xlegal takes in a series of well-formed JSON Strings representing in order:
  - the map
  - the player game state
  - the connection the player wishes to acquire
 
 And outputs whether the action is legal.  
 The program is under 5/ and the source code is under Trains/Other/source_code/Trains/xlegal/src.
