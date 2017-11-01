methodDeclarations(method) ::= <<

/*prolog
<klass.name>
    sub sp, sp, #<n1>
<method.code>
*/
/*<method.klass>.<method.name>:*/

/*postlog*/
    add sp, sp, #<n2>
    ldr fp, sp, #-8 
    ldr lr, sp, #-4
    jlx lr
     
	
>>