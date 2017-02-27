#ifndef __CONFIG_H
#define __CONFIG_H

/**
 * Set of structures and functions to parse and manage the
 * program's configuration options.
 */

#include <stdlib.h>
#include <stdio.h>

/**
 * Structure to hold the game options.
 */
typedef struct {
  int debug;          /* Boolean flag for debug. */
  int silent;         /* Boolean flag for silent. */
  size_t generations; /* Number of generations for which to run the game. */
  FILE *input_file;   /* The file with the seed board. */
} GameConfig;

/**
 * Frees memory allocated for a GameConfig structure.
 *
 * @param config Pointer to a GameConfig structure.
 */
void game_config_free(GameConfig *config);

/**
 * Returns the value of the flag debug.
 *
 * @param config Pointer to a GameConfig structure.
 *
 * @return The value of the flag debug.
 */
int game_config_get_debug(GameConfig *config);

/**
 * Returns the value of the flag silent.
 *
 * @param config Pointer to a GameConfig structure.
 *
 * @return The value of the flag silent.
 */
int game_config_get_silent(GameConfig *config);

/**
 * Returns the number of generations for which to run the game.
 *
 * @param config Pointer to a GameConfig structure.
 *
 * @return The number of generations for which to run.
 */
size_t game_config_get_generations(GameConfig *config);

/**
 * Parses the command line and create a new GameConfig from it.
 *
 * @param argc Number of command line arguments.
 * @param argv Array of command line arguments.
 *
 * @return A new GameConfig pointer.
 */
GameConfig *game_config_new_from_cli(int argc, char *argv[]);

#endif
