#include <iostream>
     #include "mjcc_driver.hh"
     
     int
     main (int argc, char *argv[])
     {
       mjcc_driver driver;
       driver.parse(stdin);
	std::count << driver.result;
     }
