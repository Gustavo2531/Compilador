    .data

 	word(-1)
 int_const0:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   987654321
 	word(-1)
 int_const1:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   111
 	word(-1)
 int_const2:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   222
 	word(-1)
 int_const3:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   333
 	word(-1)
 int_const4:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   444
 	word(-1)
 int_const5:
     .word   3
     .word   4   
     .word   Int_dispTab
     .word   123456789
 



    .text

Main.main:
loopLabel1
    ldr r0, =int_const1 
    check r0
	if false b exitLabel2
    ldr r0, =int_const2
	branch to loopLabel1
exitLabel2
	mov r0, #0
 loopLabel3
    ldr r0, =int_const3 
    check r0
	if false b exitLabel4
    ldr r0, =int_const4
	branch to loopLabel3
exitLabel4
	mov r0, #0
 
Main.f:
    ldr r0, =int_const5
