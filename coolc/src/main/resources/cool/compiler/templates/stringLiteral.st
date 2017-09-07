stringLiteral(s) ::= << 
str_const<s.idx>:
    .word   5
    .word   <s.size>
    .word   String_dispTab
    .word   int_const<s.sizeIdx>
    .asciz  "<s.value>"
    .balign  4
>>
