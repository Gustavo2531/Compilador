addOperation(n1, n2) ::= <<
<n1>
    mov r1, r0
<n2>
    jl Object.copy
    ldr r1, [r1, #12]
    ldr r2, [r0, #12]
    add r0, r1, r2
    str r0, [r0, #12]
>>