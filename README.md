Web server test suite
=====================

This validates the requirements specified by [this Gist](https://gist.github.com/e366bc45d186def1993f).

Running
-------

This test suite is built using Maven. To run it against port `8081` on `test-vm.mest.cc`, for example, execute:

    mvn test -DargLine='-Dtest.http.host=test-vm.mest.cc -Dtest.http.port=8081'