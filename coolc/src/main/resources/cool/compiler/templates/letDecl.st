letDecl(loadedExpr, letName, letWhatever) ::= <<

<loadedExpr>

	
	 ldr r0, <letName> /*Save address of <letName> in R0*/
	 ldr r1, <loadedExpr> /*Save address of the result of the expression in r1*/
	 str r1, [r0] /*assign r1 to the address of r0*/
	     
  

>>
addr_of_number_1: .word <letWhatever>
	addr_of_pointer_to_name: .word <letName>
 push into stack /*push {r4, r5, r6, lr}*/
   add r1, r1, 1/* add 1 to decl counter*/
   <letName>=<letWhatever>