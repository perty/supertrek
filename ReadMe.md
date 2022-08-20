# Super Trek

## Background

During the 70's computer did not have much memory and there typically no other user interface than a TTY or CRT with 24 lines of 80 characters.

Still, games where made. 

The programs, as the way things were before diskettes accompanied books, printed listings of Basic code. So to play the game, you simply typed the program on your program and saved it to a cassette tape. It was open source but more than a click away.

When I found a book "Basic computer games" containing "101 great games to play on your home computer", nostalgia kicked in.

This game was inspired by the TV-series Star Trek and crossed my path in the early 80's. This version is from 1978.

## Project

My idea was to do the same as back in the days and type it in but translate it to Java. I do not have a Basic interpreter and even if I had one, it would probably be of a different dialect.

### Basic language

Basic was the language for home computers. It is an interpreted language that was designed to be easy to learn. It was also designed to be used on TTY. To edit a statement, you entered its line number and typed the statement. That is why a typically Basic program has line numbers that are 10, 20 and then 30 since you
wanted to be able to insert lines between 10 and 20. (There was often a renumber function to use when you had inserted too many lines).

Variables in Basic are a letter and an optional number. Yes, two characters! They are also global even if there is a subroutine concept. The type is either a number or a string, the latter is indicated by a dollar sign after the name. Arrays are also possible in most dialects. Although a number may not necessarily be integer, it can be used to index an array.

Array indexes starts at 1, more human but everywhere else it is 0.

A common pattern with conditional statement is using a goto statement to skip the next few statements. Which becomes a challenge when writing in a modern language where you say that during the condition, execute the next few statements.

### Challenges

The code was printed using some printer terminal and then somehow put in the book. So the reading was not straight forward. It was hard to see the difference between an "1" and a "I", for example.
The "Q" character I have mistaken for "O" sometimes.

I have tried using OCR, it helps for messages but not for code.

Variable names can not be so descriptive names are not possible. Added to that, the author did not write comments on what they are used for. 

The flow of the program can be hard to follow at times. There are no boundaries between parts of the program other than the sparse comments the author provided. Sometimes the flow jumps back to a line earlier in the program. Is it a loop? 

Many calculations are hard to understand partly to naming of variables and partly lack of documentation. It is also core of the game logic so it is essential to get right.

Correctness will also be a challenge, if I have the stamina to type it all in. If it seems to work, is it as intended?

### Ultimate goal

The ultimate goal would be to put this online for others to enjoy and get a glimpse of what playing a game would be like back then.

