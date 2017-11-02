letDecl(loadedExpr, letName, letWhatever) ::= <<

<loadedExpr>
	.align 4
	<letWhatever>  : .word 4
	addr_of_number_1: .word <letWhatever>
	<letName>: .word 4
	addr_of_pointer_to_name: .word <letName>
	 ldr r0, addr_of_pointer_to_name
	 ldr r1, addr_of_number_1
	 str r1, [r0]
	     
   push into stack /*push {r4, r5, r6, lr}*/
   add r1, r1, 1/* add 1 to decl counter*/
   <letName>=<letWhatever>

>>