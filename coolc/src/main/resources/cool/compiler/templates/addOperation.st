addOperation(n1, n2) ::= <<
<!
I assume r0 was pushed into the stack on entry to this method, 
so I can overwrite it here so the standard is that subexpressions
leave result in r0
!>
<n1>
    mov r0, int_const5
<n2>
    mov r1, int_const4
    add r0, r0, r1
>>
