divOperation(n1, n2, substract) ::= <<
<!
I assume r0 was pushed into the stack on entry to this method, 
so I can overwrite it here so the standard is that subexpressions
leave result in r0
!>
<n1>
    mov r1, [r0, #12]
<n2>
    mov r2, [r0, #12]
    mov r0, #0
<substract>
    subs r1, r1, r2
    add r0, r0, #1
    str r0, [r0, #12]
    bhi <substract>
>>