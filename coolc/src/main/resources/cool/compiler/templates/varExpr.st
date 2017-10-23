<!var load!>
ldr r0, [r1, -#4]

<!var store!>
str r0, [r1, #4]

Object:
0	tag
1	size
2	dispatch_table
3	<attributes>

<!Attributes!>
ldr r0, -Object
ldr r0, [r0, #12]

<!Parameters/local variables!>
str r0, [fp, -#12]