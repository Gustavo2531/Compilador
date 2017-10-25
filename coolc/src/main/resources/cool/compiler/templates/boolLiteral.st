boolLiteral(i) ::= <<
bool_const<i>:
    .word   4
    .word   4   
    .word   Bool_dispTab
    .word   <i>

>>