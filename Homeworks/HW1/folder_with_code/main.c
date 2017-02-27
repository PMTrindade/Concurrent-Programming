#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <assert.h>
#include "config.h"
#include "game.h"

int main(int argc, char* argv[]) {
    GameConfig *gc = game_config_new_from_cli(argc, argv);

    int d = game_config_get_debug(gc);
    int n = game_config_get_generations(gc);
    int s = game_config_get_silent(gc);

    Game *g = game_new();

    int error = game_parse_board(g, gc);
    if(error == 1)
        printf("The board file could not be parsed.\n");

    int i;
    int tick = 0;
    if(s == 1) {
        for(i = 0; i < n; i++) {
            if(tick < 1)
                tick = game_tick(g);
        }
    } else if(d == 1) {
        for(i = 0; i < n; i++) {
            if(tick < 1) {
                printf("state: %d\n", i);
                game_print_board(g);
				tick = game_tick(g);
            }
        }
		printf("state: %d\n", n);
		game_print_board(g);
    } else {
        printf("state: 0\n");
        game_print_board(g);
        for(i = 0; i < n; i++) {
            if(tick < 1)
                tick = game_tick(g);
        }
        printf("state: %d\n", n);
        game_print_board(g);
    }

    game_free(g);
    game_config_free(gc);
}
