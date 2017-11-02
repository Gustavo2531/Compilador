letDecl(loadedExpr, letName, letWhatever) ::= <<

<loadedExpr>
   push into stack /*push {r4, r5, r6, lr}*/
   add r1, r1, 1/* add 1 to decl counter*/
   <letName>=<letWhatever>

>>