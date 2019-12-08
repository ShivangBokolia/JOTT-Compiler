Integer x = 11;
Integer foo( Integer x, Integer y, Double z ) { print( x ); print( y ); x = 10; print( z ); return x; }
print( foo( 5, 4, 3.2 ) );
print( x );
Void bar( Integer x ) { if( x > 5 ) { print( "foo" ); } else { print( "bar" ); } }
bar( 6 );
bar( 2 );