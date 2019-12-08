Integer a = 5;
Void p( Integer y ) { print( y ); }
p( 5 );
p( 6 );
print( a );
while( a > 0 ) { p( a ); a = a - 1; }
print( a );
a = 5;
Void x( Integer b ) { if( b > 0 ) { p( b ); x( b - 1 ); } }
x( a );
print( a );