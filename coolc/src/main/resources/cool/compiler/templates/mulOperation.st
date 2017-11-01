mulOperation(n1, n2) ::= <<
<!
I assume r0 was pushed into the stack on entry to this method, 
so I can overwrite it here so the standard is that subexpressions
leave result in r0
!>
<n1>
    mov r0, inst_constant5
<n2>
    jl Object.copy
    mov r1, inst_constant4
    mul r0, r0, r1
   
>>
