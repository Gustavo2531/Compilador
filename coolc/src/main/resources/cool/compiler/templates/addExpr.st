addExpr(left, right) ::= <<
<!
I assume r0 was pushed into the stack on entry to this method, 
so I can overwrite it here so the standard is that subexpressions
leave result in r0
!>
<left>
    mov r4, r0
<right>
    jl Object.copy
    ldr r4, [r4, #12]
    ldr r5, [r0, #12]
    add r4, r4, r5
    str r4, [r0, #12]    
>>