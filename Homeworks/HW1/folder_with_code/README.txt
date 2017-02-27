-d              DEBUG print the <number of the stage>, nl, board state, nl, nl
-n #            # iterations (if omitted assume 20)
-s              Silent mode. Do not print ANYTHING at all.

Cannot use -d and -s simultaneously!

glife -n 10 file.in      does 10 iterations and prints states 0 (initial) and 10 (final)

glife -d file.in         does 20 iterations and prints 21 states (0...20)

glife -s file.in         does not print anything at all (useful for performance evaluation)