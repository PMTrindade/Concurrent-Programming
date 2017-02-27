CC=gcc
CFLAGS=-Wall -g -ansi -pedantic
LDFLAGS=

OUTPUT=glife
SRC=config.c game.c main.c
OBJ=$(patsubst %.c,%.o,$(SRC))

all: $(OUTPUT)
	$(CC) $(LDFLAGS) -o $(OUTPUT) $(OBJ)

%.o: %.c
	$(CC) $(CFLAGS) $<

clean:
	rm -f $(OUTPUT) $(OBJ)