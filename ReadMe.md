# Super Trek

## Background

During the 70's computer did not have much memory and there were typically no other user interface than a TTY or CRT with 24 lines of 80 characters.

Still, games where made. 

The programs were, as the way things were before diskettes accompanied books, printed listings of Basic code. So to play the game, you simply typed the program on your home computer and saved it to a cassette tape. It was open source but more than a click away.

When I found a book "Basic computer games" containing "101 great games to play on your home computer", nostalgia kicked in.

This game was inspired by the TV-series Star Trek and crossed my path in the early 80's. This version is from 1978.

## Project

My idea was to do the same as back in the days and type it in but translate it to Java. I do not have a Basic interpreter and even if I had one, it would probably be of a different dialect.

### Basic language

Basic was the language of the home computers. It is an interpreted language that was designed to be easy to learn. It was also designed to be used on TTY. To edit a statement, you entered its line number and typed the statement. That is why a typically Basic program has line numbers that are 10, 20 and then 30 since you
wanted to be able to insert lines between 10 and 20. (There was often a renumber function to use when you had inserted too many lines).

Variables in Basic are a letter and an optional number. Yes, two characters! They are also global even if there is a subroutine concept. The type is either a number or a string, the latter is indicated by a dollar sign after the name.

The global variables has an effect on subroutines. Whenever a subroutine is called, it may change global variabels. In fact, since there is no parameter concept, so to pass values to a subroutine, a number of variabels need to be set.

For example, to see if a specific position has some value, the string variable A$ is set to the value while Z1 and Z2 describe the position.

Arrays are also possible in most dialects. Although a number may not necessarily be integer, it can be used to index an array.

Array indexes starts at 1, more human but everywhere else it is 0.

A common pattern with conditional statement is using a goto statement to skip the next few statements. Which becomes a challenge when writing in a modern language where instead you say that during the condition, execute the next few statements.

### Challenges

The code was printed using some printer terminal and then somehow put in the book. So the reading was not straight forward. It was hard to see the difference between an "1" and a "I", for example.
The "Q" character I have mistaken for "O" sometimes.

I have tried using OCR, it helps for messages but not for code.

Variable names can not be more than two characters so descriptive names are not possible. Added to that, the author did not write comments on what they are used for, in most cases. 

The flow of the program can be hard to follow at times. There are no boundaries between parts of the program other than the sparse comments the author provided. Sometimes the flow jumps back to a line earlier in the program. Is it a loop? 

Many calculations are hard to understand partly to naming of variables and partly lack of documentation. It is also core of the game logic, so it is essential to get right.

Since there is no way to express complex values in other ways than arrays, workarounds has to be used. For example, the state of the quadrant is represented as a string where three characters represents content in a position. Content being empty space, a star, starbase, the Enterprise or a Klingon.

There are functions that takes parameters, like `MID$` which extracts a substring but those are all system defined. It is not always clear what they do, exactly. Does `INT` do the same as `Math.round` in Java?

Correctness will also be a challenge, if I have the stamina to type it all in. If it seems to work, is it as intended?

Update: I had the stamina, it turned out, but there are of course issues. It is interesting to see how different programming has become. Today, nobody would put the number of enemies in a variable and all the enemies positions in another place. As of now, when ironed out enough to play the game, the number of enemies left to kill is more than the number of existing enemies. An impossible task. Turned out to be a bug when loosing and volunteering for a new mission.

### Bugs and like

The Basic code is well tested by playing and I believe no true bugs are there.

However, bugs are created in the translation so that takes extensive testing to find out. Since I don't remember much about how to play and never known the inner workings, it is a challenge.

I have discovered some minor things when using subroutines that will probably not cause any errors. 

Discovered however code that is weird. The damage repair is not available unless the damage control itself is damaged. There is also a check that at least one device is damaged, which will always be true. So that has to change.

### Testing

Now that I have got it to work somewhat, I need to start testing more extensively. I am thinking that the random number generator could get a seed so that the same game is played each time. Also, a feature that would be nice is a log of events. Can be used in verifying but perhaps also entertaining. 

### Ultimate goal

The ultimate goal would be to put this online for others to enjoy and get a glimpse of what playing a game would be like back then.

## A new beginning

I discovered that there are other people out there who has this sentiment for these retro games. Actually, the whole book I mentioned has been ported to Vintage Basic. All the programs run in the browser and have been translated to different languages. This game, Super trek, is no exception and has a Java implementation. The author even made a video about the process. He did however, not take it the whole way to well styled Java program, in my opinion. I suspect that there are bugs, I have not verified it fully.

The last thing led to an idea, let's define the game in Gherkin (BDD) and use Cucumber to execute the specification. 

The repository for all games : [https://github.com/coding-horror/basic-computer-games](https://github.com/coding-horror/basic-computer-games)

Another thing, I started is a port to Elm. The challenge is that original program picks up a random number whenever it is needed. In Elm, getting a random number is a side effect which must be requested in a callback fashion. It takes a while to get used to. 

So given that there is a Gherkin specification for the game, I would like to use that with Elm, but I need to find a tool for that. Maybe create one myself.

Checking out [elm-spec](https://package.elm-lang.org/packages/brian-watkins/elm-spec/latest/) which claims to support BDD. It has a different philosophy, though, the specification is written in Elm with the fixture code in the same file. A possible solution would be to translate Gherkin to elm-spec.

Checking out [elm-test-bdd-style](https://package.elm-lang.org/packages/rogeriochaves/elm-test-bdd-style/latest/) which is more of syntactic sugar on top of elm-test. 

How does elm-test really work? It manages to run elm code at least. If I could rely on elm-test, that would feel stable. So an approach could be to do a manual translation of Gherkin -> elm-test and then take it from there.

