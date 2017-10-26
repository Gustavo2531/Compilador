    .data

     .global class_nameTab
     .global Main_protObj
     .global Int_protObj
     .global String_protObj
     .global bool_const0
     .global bool_const1
     .global _int_tag
     .global _bool_tag
     .global _string_tag
     
 	word(-1)
 int_const0:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   5
 	word(-1)
 int_const1:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   3
 

bool_const0:
    .word    4
    .word    4
    .word    Bool_dispTab
    .word    0
bool_const1:
    .word    4
    .word    4
    .word    Bool_dispTab
    .word    1



    .text

Main.main:

    ldr r0, =int_const0
    mov r0, int_const5
    ldr r0, =int_const1
    mov r1, int_const4
    sub r0, r0, r1

	ldr r0, [sp #24]
	