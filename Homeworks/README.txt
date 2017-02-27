Notas de implementação:

Homework 1:
Neste Homework, tive de implementar o game of life em C. O professor providenciou duas interfaces com métodos definidos e o que tive de fazer foi, basicamente, implementar estes métodos.
Destacam-se 3 partes importantes que tive de implementar:
. Método game_config_new_from_cli na classe config.c, que recebe argumentos da linha de comandos e cria uma struct GameConfig. Implementei usando a função getopt.
. Método game_tick na classe game.c, que avança o tabuleiro para uma nova geração. Basicamente é aqui que se encontra a parte mais importante do problema.
Na minha implementação, criei um ciclo para primeiro preencher dois vectores auxiliares com os pontos que devem alterar o estado (de dead para alive e de alive para dead) e só posteriormente é que efectuiei as alterações.
. Criei uma Main para correr o programa.

Homework 2:
Basicamente usei o que tinha feito no Homework 1, acrescentei o import do cilk na classe game.c e mudei os ciclos dentro do método game_tick para cilk_for, de modo a o programa funcionar concorrentemente.

Homework 3:
Como validação usei o problema do produtor-consumidor que o professor apresentou nas aulas. Criei um método dentro da hash table para verificar esta condição.
Criei, também, 4 classes alternativas para a hash table:
. Classe SynchronizedHashTable, que usa os métodos synchronized do Java.
. Classe GlobalRWLockHashTable, que usa um read-write lock global para dar lock em cada um dos métodos (put, remove, get), sendo possível fazer gets de forma concurrente.
. Classe MediumGrainedPlainLocksHashTable, que usa um array de plain locks para dar lock a cada posição da tabela (collision lists).
. Classe MediumGrainedRWLocksHashTable, que usa um array de read-write locks para dar lock a cada posição da tabela (collision lists).

Importante: Para testar cada classe da hash table é necessário alterar a variável sharedMap na classe worker para o tipo desejado.

Homework 4:
Neste Homework, basicamente segui as indicações que o professor providenciou no enunciado, apenas tive de fazer download e instalar/configurar o ant e por o import org.deuce.Atomic e a flag @Atomic nos métodos put, remove e get da hash table do projecto.
Para configurar o ant bastou criar a environment variable ANT_HOME e adiciona-la a PATH (%ANT_HOME%\bin).