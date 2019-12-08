Integer x = 5;
Integer z = 2;
String y = "foobar";
Void foo( Integer x ){ String y = "baz";
                       x = x - 1; z = z - 1;
                       print( x ); print( y ); print( z ); }
print( x );
print( y );
print( z );
foo( 7 );
print( x );
print( y );
print( z );