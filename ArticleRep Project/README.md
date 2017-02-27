ArticleRep
============

**Students:**

*Filipe Sena - 41924*

*Pedro Trindade - 41661*

**Input arguments to run the program:**
```
time       for how long will the program run (in seconds).
nthreads   the number of threads that will operate concurrently on the “in-memory database”.
nkeys      size of the hash-map (smaller size implies more collisions).
put        percentage of insert operations.
del        percentage of remove operations.
get        percentage of lookup operations (put + del + get = 100).
nauthors   average number of co-authors for each article (higher number implies more conflicts).
nkeywords  average number of keywords for each article (higher number implies more conflicts).
nfindlist  number of authors (or keywords) in the find list (read carefully the explanation of the get operation below).
```

Examples:
---------

```sh
java -cp bin cp/articlerep/MainRep 30 4 1000 10 10 80 100 100 5
```

**Contents of the repository:**

*ArticleRep* - Our default implementation, using *Java reentrant read and write locks* to lock each collision list in the hash table. In this case, concurrent reads are possible.

*ArticleRep2* - Variation of the first implementation, using *Java reentrant locks*.

*ArticleRep3* - Implementation using *Java synchronized methods* to make each insert and remove operation atomic.

*ArticleRep4* - Alternative version that uses *Java reentrant locks* to lock each element
of the collisions lists instead of the whole list as in ArticleRep.