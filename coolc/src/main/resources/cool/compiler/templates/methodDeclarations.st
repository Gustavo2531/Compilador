methodDeclarations(method) ::= <<

<method.klass>.<method.name>:
<method.code>

Prolog
   <method.klass>.<method.name>:
   sub sp, sp, #<method.counterLV>
   <method.code>

Postlog
   add sp, sp, #<method.outFramePointer>
   ldr fp, sp, #<method.inFramePointer>
   ldr lr, sp, #<method.lr>
   bx lr

>>