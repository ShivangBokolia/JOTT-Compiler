Integer i = 1;
Integer j = 1;
for (Integer x = 0; x < 5; x = x + 1){
    while (i < 5) {
        while(j < 5){
            print("Hello");
            j = j + 1;
        }
        if ( i < j ){ x = x + 1; }
        else {
            if (j > i){
                i = i + 1;
            }
        }
        i = i + 1;
    }
}