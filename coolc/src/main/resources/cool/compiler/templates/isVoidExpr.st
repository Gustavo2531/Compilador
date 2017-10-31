isVoidExpr(testExpression, testExpression,stringExpression, boolExpression, trueLabel,falseLabel, exitLabel) ::= <<
<testExpression> 
    ldr r1, [r0, #X] <! qué offset necesitamos para saber el tipo de dato X !>
	cmp r1, 0x00
	beq<falseLabel> <! si cmp es 0, vete a false porque es ya sabemos que tiene algun tipo de dato !>
	beq<trueLabel> <! si no, es nulo y es void!>
<falseLabel>
	str r0, #0
	beq <exitLabel>
<trueLabel>
	str r0, #1
	beq <exitLabel>
<exitLabel>
	mov r0, [r0, #12]
>>
