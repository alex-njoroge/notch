# Notch
Real-time leaderboards backed by Redis in Java.

## Getting started
To get started with Notch, add it as a dependency in your project.

With Maven:
```xml
<dependency>
	<groupId>notch</groupId>
	<artifactId>notch</artifactId>
	<version>0.1.0</version>
</dependency>
```

Clone the repository.

Make sure your Redis server is running.

Run `mvn install` and you're good to go.

## Usage
```java
import notch.Notch;

Notch notch = new Notch("game_leaderboard", "localhost", 6379);
notch.rankMember("player_x", 93);
Long rank = notch.rankOf("player_x");
notch.removeMember("player_x");
```
For more usage examples check the tests.

## What you can do with Notch
- Create a new leaderboard or attach to an existing one
- Rank one or multiple members `rankMember()`
- Check whether a member exists in the leaderboard `checkMember()`
- Get the score of a member `scoreOf()`
- Get the ranking of a member `rankOf()`
- Retrieve information about all members `allMembers()`
- Retrieve information about members within a specific range `membersinScoreRange()` ...
- Retrieve information about members in a certain page `firstPage()` ...
- Retrieve information about members above and below a specific member `around()`
- Modify the score of a member `changeScore()`
- Remove one or multiple members from the leaderboard `removeMember()`
- Remove members with and below a specific score `removeMembersBelow()`
- Get the total number of members in the leaderboard `totalMembers()`
- Delete a leaderboard `deleteLeaderboard()`

...and way more functionality than you could ever need.

## Configuration
Configuration options have been exposed through various setter and getter methods
in the `Notch` class.

To configure the host, port and database to use for Redis, utilize `setHost()`,
`setPort()` and `setDatabase()` respectively.

A leaderboard is split into multiple pages for easier member retrieval.

The number of members in a page can be configured through `setPageSize()`

## Documentation
You can check out the [latest Notch Javadoc](https://alex-njoroge.github.io/notch)

## Testing
To execute the tests use:

`mvn test`
