# NOTE MUST BE CALLED FROM ROOT

from ctypes import *
import sys
import platform

fib_param = int(sys.argv[1])
test_count =  int(sys.argv[2])
lib_path = "target\\debug\\rapl_lib.dll" if platform.system() == "Windows" else "target/debug/librapl_lib.so"

# test method
def fibIter(n):
    if n < 2:
        return n
    fibPrev = 1
    fib = 1
    for _ in range(2, n):
        fibPrev, fib = fib, fib + fibPrev
    return fib

# start lib
dll = cdll.LoadLibrary(lib_path)

for i in range(test_count):
    # start recording
    dll.start_rapl()

    # run test
    fibIter(fib_param)

    # stop recording
    dll.stop_rapl()

print("job done")