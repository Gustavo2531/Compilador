negOperation(n1) ::= <<
<!
I assume r0 was pushed into the stack on entry to this method, 
so I can overwrite it here so the standard is that subexpressions
leave result in r0
!>
<n1>
    mov r0, inst_constant5
    jl Object.copy
    ldr r1, [r0, #12]
    neg r1, r1
    str r1, [r0, #12]
>>