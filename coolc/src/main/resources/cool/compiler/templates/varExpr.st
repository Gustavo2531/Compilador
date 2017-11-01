varExpr(var)::= <<
<!var load!>
ldr r0, [r1, -#4]

<!var store!>
str r0, [r1, #4]


<!Attributes!>
ldr r0, -Object
ldr r0, [r0, #12]

<!Parameters/local variables!>
str r0, [sp, -#12]

>>