# Og
Implementation of Human vs Comp Go on a 4x4 board, though board size can be increased.  

## How to play

`javac Og.java`
`java Og`

Board will be drawn via terminal prompt. You will be prompted for game config, and then
prompted to enter row, col for each move. 

Win by occupying more spaces than the opponent. You occupy one space for each of your
pieces on the board. You 'capture' a space by surrounding it with pieces. When you 
capture a space, you also automatically occupy that space and gain 1 extra move
that turn (doesn't stack). 

Computer uses basic minimax algorithm with depth first search of board state
to determine optimal moves. 

Boardsize is 4x4, but you can modify the java file to increase it. However, 
as AI researchers have been struggling for ages to solve this problem, the 
computer calculation efficiency will decrease in exponential time (NP complete)
proportional to the board size. Therefore, sizes larger than 7 may take a 
frustratingly long amount of time to produce a move from the computer. 

###Known issues
Computer attempts to memoize board states but doesn't take board symmetry into 
account very well. Therefore it may understand what to do in one configuration 
but will not know what to do if that configuration is transposed. 

