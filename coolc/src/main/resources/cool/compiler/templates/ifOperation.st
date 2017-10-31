ifOperation(testExpression, loopExpression, loopLabel, exitLabel) ::= <<
<!
I assume r0 was pushed into the stack on entry to this method, 
so I can overwrite it here so the standard is that subexpressions
leave result in r0
!>

<loopLabel>
<testExpression>
    teq     r4, inst_constant5
    mvnmi   r0, inst_constant1 <exitLabel>
    movpl   r0, inst_constant2 <exitLabel>
<loopExpression>
    bne     <loopeLabel>
<exitLabel>
    mov     r0, #0
>>