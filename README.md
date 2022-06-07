# Masters Thesis

#### *** DISCLAIMER This was my first independent coding assignment so the code is going to be very simplistic and basic but looking at more recent repositories will show my advancements over time.  ***

This project was submitted as part of my Masters thesis project while in university in 2017. The project compared two types of AI implementations, Heuristics-based and Monte Carlo Tree Search. The game [Lux Delux](https://store.steampowered.com/app/341950/Lux_Delux/) was an implementation of Risk which allowed individuals to implement their own AI written in Java.

They provided an API to hook into to obtain the following information:
* Board state
    * Occupied countries
    * Number of soldiers
    * Nearby countries
* Current phase
  * Pick countries
  * Place armies
  * Attack Phase

Using all this information you can create a Java application that calculates the moves to take during each phase to achieve victory.

I used a crude scoring system which could be used to give each of the AI means by which it can determine the best move to make. The same scoring system was used for both the Heuristics and MCTS.
