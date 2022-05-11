OPTS=-Wall -g -Wno-write-strings -Wno-unused-function -Wno-sign-compare -std=c++11
OBJS=lex.yy.o plp5.tab.o TablaSimbolos.o TablaTipos.o
CC=g++

plp5: $(OBJS)
	$(CC) $(OPTS) $(OBJS) -o plp5

lex.yy.o: lex.yy.c comun.h plp5.tab.h
	$(CC) $(OPTS) -c lex.yy.c

plp5.tab.o: plp5.tab.c lex.yy.c comun.h
	$(CC) $(OPTS) -c plp5.tab.c

TablaSimbolos.o: TablaSimbolos.cc TablaSimbolos.h
	$(CC) $(OPTS) -c TablaSimbolos.cc

TablaTipos.o: TablaTipos.cc TablaTipos.h TablaSimbolos.h
	$(CC) $(OPTS) -c TablaTipos.cc

lex.yy.c : plp5.l comun.h
	flex plp5.l
	
plp5.tab.c plp5.tab.h: plp5.y lex.yy.c comun.h TablaSimbolos.h TablaTipos.h
	bison -d plp5.y	


clean:
	rm -f $(OBJS)
	rm -f plp5
