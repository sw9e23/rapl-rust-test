source ./benchRunners/bench_func.sh

testName="fib"
folder="fibsequence"
count=100
fibInput=47

echo "!!! Starting $testName !!!"
echo

#   Node
cmd="node ./benchmarks/$folder/javascript/bench.js $count"
runbenchmark "Node" $testName "$cmd" "$fibInput"

#   Python
cmd="python3 ./benchmarks/$folder/python/bench.py 20" # custom count for python !!! 
runbenchmark "Python" $testName "$cmd" "$fibInput"

#   Pypy
cmd="pypy ./benchmarks/$folder/python/bench.py $count"
runbenchmark "Pypy" $testName "$cmd" "$fibInput"

#   C#
cmd="dotnet run --project ./benchmarks/$folder/csharp/Bench.csproj --configuration Release $count"
runbenchmark "Csharp" $testName "$cmd" "$fibInput"

#   Java
cmd="java --enable-native-access=ALL-UNNAMED --enable-preview --source 21 ./benchmarks/$folder/java/Bench.java $count"
runbenchmark "Java" $testName "$cmd" "$fibInput"

#   C
gcc benchmarks/$folder/c/bench.c -O3 -o benchmarks/$folder/c/bench -L./target/release -lrapl_lib -Wl,-rpath=./target/release
cmd="./benchmarks/$folder/c/bench $count"
runbenchmark "C" $testName "$cmd" "$fibInput"


#   C++
g++ benchmarks/$folder/cpp/bench.cpp -O3 -o benchmarks/$folder/cpp/bench -L./target/release -lrapl_lib -Wl,-rpath=./target/release
cmd="./benchmarks/$folder/cpp/bench $count"
runbenchmark "Cpp" $testName "$cmd" "$fibInput"

#   C
gcc benchmarks/$folder/chead/bench.c -O3 -o benchmarks/$folder/chead/bench -L./target/release -lrapl_lib -Wl,-rpath=./target/release
cmd="./benchmarks/$folder/chead/bench $count"
runbenchmark "C" "fibHead" "$cmd" "$fibInput"

#   C++
g++ benchmarks/$folder/cpphead/bench.cpp -O3 -o benchmarks/$folder/cpphead/bench -L./target/release -lrapl_lib -Wl,-rpath=./target/release
cmd="./benchmarks/$folder/cpphead/bench $count"
runbenchmark "Cpp" "fibHead" "$cmd" "$fibInput"

echo "!!! Finished $testName !!!"
