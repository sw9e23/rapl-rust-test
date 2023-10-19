from ctypes import *
import sys
import platform

fib_param = int(sys.argv[1])
test_count = int(sys.argv[2])
lib_path = "target\\release\\rapl_lib.dll" if platform.system(
) == "Windows" else "target/release/librapl_lib.so"

# start lib
dll = cdll.LoadLibrary(lib_path)

for i in range(test_count):
    dll.start_rapl()
    dll.stop_rapl()

print("job done")
