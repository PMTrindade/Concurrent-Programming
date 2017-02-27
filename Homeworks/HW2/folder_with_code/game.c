/*
 * Copyright (C) 2009 Raphael Kubo da Costa <kubito@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
#ifndef __GAME_H
#define __GAME_H

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <assert.h>
#include <cilk/cilk.h>
#include "config.h"
#include "game.h"

/**
 * The main structure used by the game.
 */
typedef struct {
  char *board; /**< The board as an array of 0's and 1's. */
  size_t cols; /**< The number of columns. */
  size_t rows; /**< The number of rows. */
} Game;

/**
 * Frees memory allocated to a Game structure.
 *
 * @param game Pointer to be freed.
 */
void game_free(Game *game) {
	assert(game != NULL);

	free(game);
}

/**
 * Checks whether a given board position is in an alive state.
 *
 * @param game Pointer to a Game structure.
 * @param row  The row number.
 * @param col  The column number.
 *
 * @retval 0 The position is in a dead state.
 * @retval 1 The position is in an alive state.
 */
int game_cell_is_alive(Game *game, size_t row, size_t col) {
	assert(game != NULL);

	int c = game -> cols;
	int i = (row*c)+col;
	int cell = 2;

	if(game -> board[i] == 1)
		cell = 1;
	else if(game -> board[i] == 0)
		cell = 0;

    return cell;
}

/**
 * Checks whether a given board position is in a dead state.
 *
 * @param game Pointer to a Game structure.
 * @param row  The row number.
 * @param col  The column number.
 *
 * @retval 0 The position is in an alive state.
 * @retval 1 The position is in a dead state.
 */
int game_cell_is_dead(Game *game, size_t row, size_t col) {
	assert(game != NULL);

	int c = game -> cols;
	int i = (row*c)+col;
	int cell = 2;

	if(game -> board[i] == 0)
		cell = 1;
	else if(game -> board[i] == 1)
		cell = 0;

    return cell;
}

/**
 * Allocates memory for a new Game structure.
 *
 * @return A new Game pointer.
 */
Game *game_new(void) {
	Game *g = (Game *) malloc(sizeof(Game));

	return g;
}

/**
 * Parses a board file into an internal representation.
 *
 * Currently, this function only parses the custom file format
 * used by the program, but it should be trivial to add other
 * file formats.
 *
 * @param game Pointer to a Game structure.
 * @param config Pointer to a GameConfig structure.
 *
 * @retval 0 The board file was parsed correctly.
 * @retval 1 The board file could not be parsed.
 */
int game_parse_board(Game *game, GameConfig *config) {
    assert(game != NULL);
    assert(config != NULL);

    FILE *f = config -> input_file;
    int val = 0;

    long unsigned int row;
    if(fscanf(f, "Rows:%lu\n", &row) == 0)
        val = 1;

    game -> rows = row;

    long unsigned int col;
    if(fscanf(f, "Cols:%lu\n", &col) == 0)
        val = 1;

    game -> cols = col;

    game -> board = (char *) malloc((row*col)*sizeof(char));
    char line[col*2];

	int i = 0;
    int j, k;
    char c;
    for(j = 0; j < row; j++) {
        if(fgets(line, col*2, f) == NULL)
            val = 1;
        for(k = 0; k < col; k++) {
            c = line[k];
            if(c == '#')
                game -> board[i] = 1;
            else if(c == '.')
                game -> board[i] = 0;

            i++;
        }
    }

    return val;
}

/**
 * Prints the current state of the board.
 *
 * @param game Pointer to a Game structure.
 */
void game_print_board(Game *game) {
	assert(game != NULL);

	int r = game -> rows;
    int c = game -> cols;
    int i = 0;

	int j, k;
	for(j = 0; j < r; j++) {
		for(k = 0; k < c; k++) {
            if(game -> board[i] == 1)
                printf("#");
            else if(game -> board[i] == 0)
                printf(".");

			i++;
		}

		printf("\n");
	}

	printf("\n");
}

/**
 * Sets a specific position in the board to an alive state.
 *
 * @param game Pointer to a Game structure.
 * @param row  The row number.
 * @param col  The column number.
 */
void game_cell_set_alive(Game *game, size_t row, size_t col) {
	assert(game != NULL);

	int c = game -> cols;
	int i = (row*c)+col;

	game -> board[i] = 1;
}

/**
 * Sets a specific position in the board to a dead state.
 *
 * @param game Pointer to a Game structure.
 * @param row  The row number.
 * @param col  The column number.
 */
void game_cell_set_dead(Game *game, size_t row, size_t col) {
	assert(game != NULL);

	int c = game -> cols;
	int i = (row*c)+col;

	game -> board[i] = 0;
}

/**
 * Advances the cell board to a new generation (causes a 'tick').
 *
 * @param game Pointer to a Game structure.
 *
 * @retval 0 The tick has happened successfully.
 * @retval 1 The tick could not happen correctly.
 */
int game_tick(Game *game) {
    assert(game != NULL);

    size_t t_rows = game -> rows;
    size_t t_cols = game -> cols;
    int set_alive[t_rows*t_cols];
    int set_dead[t_rows*t_cols];

    size_t r, c, prev_r, prev_c, next_r, next_c;
    int i = 0;
    int j = 0;
    int k = 0;
    int doa = 0;
    int alive = 0;
    int tick = 1;

    cilk_for(r = 0; r < t_rows; r++) {
        if(r == 0)
            prev_r = t_rows-1;
        else
            prev_r = r-1;

        if(r == (t_rows-1))
            next_r = 0;
        else
            next_r = r+1;
        for(c = 0; c < t_cols; c++) {
            if(c == 0)
                prev_c = t_cols-1;
            else
                prev_c = c-1;

            if(c == (t_cols-1))
                next_c = 0;
            else
                next_c = c+1;

            doa += game_cell_is_alive(game, prev_r, prev_c);
            doa += game_cell_is_alive(game, r, prev_c);
            doa += game_cell_is_alive(game, next_r, prev_c);
            doa += game_cell_is_alive(game, next_r, c);
            doa += game_cell_is_alive(game, next_r, next_c);
            doa += game_cell_is_alive(game, r, next_c);
            doa += game_cell_is_alive(game, prev_r, next_c);
            doa += game_cell_is_alive(game, prev_r, c);

            alive = game_cell_is_alive(game, r, c);
            if(alive == 1) {
                if(doa < 2 || doa > 3) {
                    set_dead[j] = i;
                    j++;
                    tick = 0;
                }
            } else if(doa == 3) {
                set_alive[k] = i;
                k++;
                tick = 0;
            }

            i++;
            doa = 0;
        }
    }

    int l;
    cilk_for(l = 0; l < j; l++) {
        i = set_dead[l];
        game -> board[i] = 0;
    }

    cilk_for(l = 0; l < k; l++) {
        i = set_alive[l];
        game -> board[i] = 1;
    }

    return tick;
}

#endif
