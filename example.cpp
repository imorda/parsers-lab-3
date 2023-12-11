   #include   "iostream"

int
    outer_num

    =   -1
;

  int    magic  (   int  args   )    {
      long

      answer
      =
      42
;
              return

              answer*args +


                    42     ;
   }

   int      main (    )    {
          int num

          =
          outer_num
;
       num     =    outer_num   +

           2   /

               3   *
4   +
    (-2- 3+magic(0))
;

while(
        num

> 0)
{
 num = num - 1;
}


      printf
      (
      "Hello world!" )
;


printf
("%d",num)
;

    bool

    logic  =    !  (   num  >2!=   1
    <=      magic  (1       ))

;

    printf

    (   "%d"
            ,logic)
;
    return
0
;
}
