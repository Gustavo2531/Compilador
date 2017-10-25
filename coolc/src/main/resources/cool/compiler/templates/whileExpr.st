whileExpr(testExpression, loopExpression, loopLabel, exitLabel) ::= <<
<!
I assume r0 was pushed into the stack on entry to this method, 
so I can overwrite it here so the standard is that subexpressions
leave result in r0
!>

<loopLabel>
<testExpression> 
    check r0
	if false b <exitLabel>
<loopExpression>
	branch to <loopLabel>
<exitLabel>
	mov r0, #0
 
>>