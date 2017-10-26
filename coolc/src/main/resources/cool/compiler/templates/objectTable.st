objectTable(o) ::= <<
class_objTab:
    .word   Object_protObj
    .word   Object_init
    .word   IO_protObj
    .word   IO_init
    .word   Int_protObj
    .word   Int_init
    .word   Bool_protObj
    .word   Bool_init
    .word   String_protObj
    .word   String_init
    .word   Main_protObj
    .word   Main_init
>>