isVoidExpr(testExpression, nLabel) ::= <<
<testExpression> 
	cmp r0, #0
	bne falseLabel<nLabel> 		 
	b trueLabel<nLabel>			 
	
falseLabel<nLabel>:
	ldr r0, bool_const0 <! LOAD FALSE BOOLEAN !>
	b exitLabel<nLabel>

trueLabel<nLabel>:
	ldr r0, bool_const1 <! LOAD TRUE BOOLEAN !>
	b exitLabel<nLabel>
	
exitLabel<nLabel>:
>>