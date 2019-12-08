Integer bar( Integer x ) { return x; }
Integer x = bar( 10 );
Void foo( Integer x ) { print( x ); }
Void baz( Integer x, String y ) { print( x ); print( y ); }
foo( 3 );
print( x );
foo( 5 );
baz( 4, "foo" );