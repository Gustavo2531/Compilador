ifOperation(testExpression, trueExpression,falseExpression, nLabel) ::= <<
<testExpression>
    cmp r0, #0
    bne truelabel<nLabel>
    b falseLabel<nLabel>
    
trueLabel<nLabel>:
<trueExpression>
   	b exitLabel<nLabel>
   	
falseLabel<nLabel>:
<falseExpression>
    b exitLabel<nLabel>
    
exitLabel<nLabel>:
 >>