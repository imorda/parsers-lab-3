grammar cpp;

code: (statement)*;

statement: directive
         | functionDeclaration
         | variableDeclaration SEMICOLON
         | variableAssignment SEMICOLON;

functionDeclaration: NAME NAME LBRACKET ((variableDeclaration COMMA)* variableDeclaration)* RBRACKET
                     LCURLY functionBody RCURLY;

functionBody: ( expression SEMICOLON
              | statement
              | return)*;

return: RETURN expression SEMICOLON;

directive: INCLUDE STRING;

variableDeclaration: NAME variableAssignment;

variableAssignment: NAME (ASSIGN expression)?;

expression: functionInvocation
          | NOT expression
          | expression (MULTIPLY | DIVIDE) expression
          | expression (PLUS | MINUS) expression
          | expression (LE | LANGLE | GE | RANGLE) expression
          | expression (EQ | NE) expression
          | expression (AND | OR) expression
          | term;

term: NUMBER
    | STRING
    | NAME
    | LBRACKET expression RBRACKET;

functionInvocation: NAME LBRACKET (expression COMMA)* expression RBRACKET;


SEMICOLON : ';';
LBRACKET : '(';
COMMA : ',';
RBRACKET : ')';
LCURLY : '{';
RCURLY : '}';
RETURN : 'return';
INCLUDE : '#include';
LANGLE : '<';
RANGLE : '>';
QUOTE : '"';
ASSIGN : '=';
NOT : '!';
MULTIPLY : '*';
DIVIDE : '/';
PLUS : '+';
MINUS : '-';
LE : '<=';
GE : '>=';
EQ : '==';
NE : '!=';
AND : '&&';
OR : '||';
NAME : (([_a-zA-Z])([_\-a-zA-Z0-9])* '::')* ([_a-zA-Z])([_\-a-zA-Z0-9])*;
STRING : '"'~["]+'"';
NUMBER : ('-')? [0-9]+;
WS: [ \t\r\n]+ -> skip;
