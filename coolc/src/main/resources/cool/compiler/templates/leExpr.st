leExpr(left, right) ::= <<
<!
I assume r0 was pushed into the stack on entry to this method, 
so I can overwrite it here so the standard is that subexpressions
leave result in r0
!>
<left>
	push {r0, r1}
<right>
	
	ldr, r2, [r0, #12]
	pop {r0, r1}
	ldr, r0, [r0, #12]

	cmp r0, r2
	jle etiq_menorigual<x>
	j etiq_mayor<x>

etiq_menorigual<x>:
	ldr =bool_const1
	j exit

etiq_mayor<x>:
	ldr =bool_const0
	j exit

exit<x>:

>>