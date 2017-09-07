intLiteral(i) ::= <<
int_const<i.idx>:
    .word   3
    .word   4   
    .word   Int_dispTab
    .word   <i.value>

>>