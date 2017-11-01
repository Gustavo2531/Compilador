negOperation(n1) ::= <<

<n1>
    mov r0, inst_constant5
    jl Object.copy
    ldr r1, [r0, #12]
    neg r1, r1
    str r1, [r0, #12]
>>