#include <iostream>
#include <stdio.h>
     #include "mjcc_driver.hh"
     
     int
     main (int argc, char *argv[])
     {
       mjcc_driver driver;
       driver.parse();
	 std::cout << driver.result << std::endl;
     }
