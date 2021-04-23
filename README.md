# Tools for Alternative Vote

Implementation of [Instant-runoff voting](https://en.wikipedia.org/wiki/Instant-runoff_voting), also called alternative vote, including a variant to draw a list of winners.

## Usage
Download the latest release.
First parameter must point to the file containing the vote lists, second is the number of winners to draw (optional, default is 5).
The vote lists file must be a json array containing an array for each voter.
These arrays specify the preferences of the respective voters.

For "normal" alternative vote, set the number of winners to draw (second parameter) to 1.

### Example:
```bash
$ cat vote_lists.json
[['firstVoterPreference1', 'firstVoterPreference2', ...], ...]
$ ./votebot vote_lists.json 3
['firstWinner', 'secondWinner', 'thirdWinner']
```

## From Source / Compilation
The program is written in Scala and compiled with `scala-native` using `sbt`.
So, you need to have `sbt` and `clang` installed.
Then, you can run sbt commands such as `sbt test`, `sbt run` or `sbt console`.
You may also want to have a look at the [test](.github/workflows/scala-test.yml) workflow.

## Note on Ties
If in any step, two candidates have the same number of votes, this is a tie.
For that, we use a tie breaking algorithm that first compares the overall number of votes for the relevant candidates.
If they are also equal, the lists get iteratively trimmed from their ends (to equal length), and the counts are compared again.
This way, in a tie, a vote close to the front of a list has more influence than one at the end.
If this is still not sufficient to break the tie, *randomly* one candidate is chosen and kicked out.
Although this is a random choice, it is deterministically, as the seed is based on the input, so multiple runs with the same input give the same result.
If such a random decision is made, it is reported to `stderr`.
