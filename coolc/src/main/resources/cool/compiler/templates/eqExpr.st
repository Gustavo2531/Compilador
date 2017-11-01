eqExpr(left, right) ::= <<
<!
I assume r0 was pushed into the stack on entry to this method, 
so I can overwrite it here so the standard is that subexpressions
leave result in r0
!>

<right>
	mov r3, r0
<left>
	mov r2, r0
	ldr r0, bool_const1
	ldr r1, bool_const0
	jle equality_test

>>