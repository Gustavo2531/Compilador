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
     .word   4
 	word(-1)
 int_const2:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   3
 	word(-1)
 int_const3:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   10
 

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


 class_nameTab:
     .word   str_const3
     .word   str_const4
     .word   str_const5
     .word   str_const6
     .word   str_const7
     .word   str_const8 
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

    .text

Main.main:

    ldr r0, =int_const0
    push into stack
    ldr r0, =int_const1
    push into stack
    ldr r0, =int_const2
    push into stack
    ldr r0, =int_const3 /* Execute block inside let expr */
    pop from stack
    pop from stack
    pop from stack


	ldr r0, [sp #24]
	