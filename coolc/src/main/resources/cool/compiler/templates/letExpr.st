letExpr(resultExpr) ::= <<
 
b <Lcheck_loop_let>
<Lloop_let>
ldr r5, [r1, r4, LSL #2]   
mov r5, r5, <resultExpr>       
str r5, [r1, r4, LSL #2]
add r4, r4, #1    
<Lcheck_loop_let>
cmp r4, r0           
bne <Lloop_let>
be<end>
<end>
pop {r4, r5, r6, lr}
>>