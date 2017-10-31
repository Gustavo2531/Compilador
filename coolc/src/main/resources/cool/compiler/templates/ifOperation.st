ifOperation(testExpression, trueExpression,falseExpression, exitLabel) ::= <<
<testExpression> 
    cmp r0, #0 
	beq <trueExpression>
	beq <falseExpression>
<trueExpression>
	str r0, #1 
	beq <exitLabel>
<falseExpression>
	str r0, #0 
	beq <exitLabel>
<exitLabel>
	mov r0, [r0,#12]
>>