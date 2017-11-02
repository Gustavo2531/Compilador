methodDeclarations(method, countParameters) ::= <<

<method.klass>.<method.name>:
<method.code>

prolog
   <method.klass>.<method.name>:
   sub sp, sp, #<countParameters>
   <method.code>

postlog
   add sp, sp, #2
   ldr fp, sp, #
   ldr lr, sp, #
   bx lr

>>