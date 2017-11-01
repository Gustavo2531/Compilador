mulOperation(n1, n2) ::= <<

<n1>
    mov r0, [r0, #12]
<n2>
    jl Object.copy
    mov r1, [r0, #12]
    mul r0, r0, r1
    str r0, [r0, #12]
>>