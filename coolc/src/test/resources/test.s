    .data

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
     .word   6
 	word(-1)
 int_const2:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   1
 	word(-1)
 int_const3:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   3
 	word(-1)
 int_const4:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   1
 



    .text

Main.main:
    ldr r0, =int_const0
   push into stack /*push {r4, r5, r6, lr}*/
   add r1, r1, 1/* add 1 to decl counter*/
    ldr r0, =int_const1
   push into stack /*push {r4, r5, r6, lr}*/
   add r1, r1, 1/* add 1 to decl counter*/
 
   push into stack /*push {r4, r5, r6, lr}*/
   add r1, r1, 1/* add 1 to decl counter*/
    ldr r0, =int_const3
   push into stack /*push {r4, r5, r6, lr}*/
   add r1, r1, 1/* add 1 to decl counter*/
 
