Tic Tac Toe
===

In this exercise you'll implement a classic game - tic tac toe - in a distributed version. This means you'll have a game server and a game client. Just in case you're still unsure how this game works, please check this wiki [page](https://en.wikipedia.org/wiki/Tic-tac-toe). 

During the exercise you'll make use of the [http4s](https://http4s.org/) framework that is applied often in real world applications. You should be able to apply the techniques, you've learned during this week, within this framework to solve the following tasks. Another important framework, heavily used by `http4s`, is [cats-effect](https://typelevel.org/cats-effect/datatypes/). Whenever necessary, a task will point you to the relevant documentation. 

The project is build using `sbt` and it's spilt into three major parts. 

First, there is the game server. This part offers a REST API, used to play a game of tic tac toe. Furthermore, the server can manage multiple users and thus multiple games at the same time.
 
Second, is the game client. It's a command-line tool used to authenticate a user against the server and play tic tac toe games in the end.

Third, a shared `model` component. It contains all classes shared between the server and the client. These shared classes are exchanged via the REST API by encoding and decoding them to JSON. 

Caveats
===

Often it's very important to have the right imports in your classes to get access to all necessary methods. So please keep an eye on those, if you read the documentation. Furthermore, whenever a method definition cannot be found, check your imports first.

Tasks
===

## User API

Let's start with a simple API to create, read, delete and authenticate user accounts. The API is implemented in the `UserService` class. To actually provide this API to the outside world, it's initialized in the `TicTacToeServer` class, which also starts the HTTP server. For now you don't need to worry about concurrency and shared state. Just start with a local mutable to get to know the framework. Please implement the following endpoints:

* GET /users -> Returns a list of all users
* GET /users/${username} -> Validates whether the user with the given username exists
* PUT /users -> Takes the query parameters ${username} and ${password} to create a new user account.
* DELETE /users/${username} -> Deletes the user with the given username
* POST /users/authenticate -> Takes the query parameters ${username} and ${password}, if the user exists and the password matches, this endpoint should return a random token that authenticates the user

Some tests for those endpoints can be found in `UserServiceSpec`. Feel free to extend those tests.

Useful links:

* [HTTP API definition](https://http4s.org/v0.19/service/)

### Validation

Now it's time to properly validate the input received by the REST API. To do so, you can start by using some helper methods from the `Validation` class. You should validate the following things:

* The username and password should be at least 3 characters long
* Has a password at least one upper case and one lower case character
* Test you validation methods (see `ValidationSpec`)

Useful links:

* [Validation](https://typelevel.org/cats/datatypes/validated.html)

### Shared state

The next step to improve your API is to add proper support for shared state. This is necessary to support concurrent access to the API. Thus, instead of using a mutable map to store the server's local state, try to use a shared reference. 

Useful links:

* [Shared references](https://typelevel.org/cats-effect/concurrency/ref.html)

## Game API

Now it's time to finally implement the game. A default implementation is already given in the `GameLogic` class, but it's not very smart when it comes down to selecting good game moves. For now you can leave it as it is to test your server implementation and improve on it later. You should now add a second API to enable the server to actually make game moves on client requests. It should implement the following endpoints:

* POST /tictactoe -> Accepts a `MoveRequest` - encoded in JSON - and replies with an updated game field. Furthermore, the authentication of the user should be checked.

Please note that you might need to change the constructor of your service classes as well as the `TicTacToeServer` to gain access to the objects that hold the shared state.

As a second step, try to run the validation of the user authentication and the computation of the AI's move in parallel. Of course, these are usually sequential steps, but for the sake of low response times (and this exercise), we want to run them concurrently.

Useful links:

* [Concurrent execution](https://typelevel.org/cats-effect/datatypes/fiber.html)

## Client 

The implementation of the client is completely up to you. It can be as simple or as complex as you want. Even though I would suggest to keep it simple for now ;) The minimum requirements for the client command-line tool are:

* Create a user account
* Authenticate against the server
* Play a match of tic tic toe against the server's "AI"

Try to apply what you learned during this week. Of course, you could come up with a bash script that solves the tasks, but where is the fun in that?

Useful links:

* [HTTP Client](https://http4s.org/v0.19/client/)
* [IO operations](https://typelevel.org/cats-effect/datatypes/io.html)

## Optional 

Feel free to extend the server and client however you see fit. Parts, that could probably be improved, are the "AI", validation of user input, tests, etc. Some method documentations contain an "Optional TODO". You can also work on them.

