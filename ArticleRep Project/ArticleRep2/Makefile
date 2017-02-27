COMPILER=javac
BIN=bin
SRC=src

# All files with .java
ALLJAVAFILES='*.java'

# Main location
MAIN=cp/articlerep/Main

# Time in seconds
TIME=10

# N of threads
THREADS=1

# size of the hash table
SIZE=1000
# OUTPUT terminologies
OUTPUTTESTS=output/
OUTTERM=.csv
OUTFILE=$(OUTPUTTESTS)tests$(OUTTERM)
REPEAT=5

all:
	find $(SRC) -name $(ALLJAVAFILES) -print | xargs $(COMPILER) -d $(BIN)

validate_run: all
	java -Dcp.articlerep.validate=true -cp bin cp/articlerep/MainRep $(TIME) $(THREADS) $(SIZE) 35 35 30 100 100 5

run: all
	java -cp bin cp/articlerep/MainRep $(TIME) $(THREADS) $(SIZE) 10 10 80 100 100 5

clean :
	rm -rf bin/*
	rm -rf $(OUTPUTTESTS)

test: clean all
	mkdir $(OUTPUTTESTS)
	echo "Threads,DicSize,Operation rate (ops/s)" >> $(OUTFILE)
	for i in 1 2 4; do\
		for j in 500 1000 2500 5000; do\
			for ((k=0; k<$(REPEAT); k++)); do\
				echo $$i,$$j,$$($(MAKE) -s run THREADS=$$i SIZE=$$j |\
					sed -nr "s/^Operation rate: ([0-9]+) ops\/s$$/\1/p") >> $(OUTFILE);\
			done;\
		done;\
	done

try:
	echo "$1"
