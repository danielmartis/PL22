OPTS=-Wall -g -Wno-write-strings -Wno-unused-function -Wno-sign-compare -std=c++11
OBJS=lex.yy.o plp4.tab.o TablaSimbolos.o
CC=g++

plp4: $(OBJS)
	$(CC) $(OPTS) $(OBJS) -o plp4

lex.yy.o: lex.yy.c comun.h plp4.tab.h
	$(CC) $(OPTS) -c lex.yy.c

plp4.tab.o: plp4.tab.c lex.yy.c comun.h
	$(CC) $(OPTS) -c plp4.tab.c

TablaSimbolos.o: TablaSimbolos.cc TablaSimbolos.h
	$(CC) $(OPTS) -c TablaSimbolos.cc

lex.yy.c : plp4.l comun.h
	flex plp4.l
	
plp4.tab.c plp4.tab.h: plp4.y lex.yy.c comun.h TablaSimbolos.h
	bison -d plp4.y	


clean:
	rm -f $(OBJS)
	rm -f plp4
