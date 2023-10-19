// general benchmark imports
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
// benchmark specific imports
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

// OLD:
// java --enable-native-access=ALL-UNNAMED --enable-preview --source 21 .\benchmarks\fibjava\Bench.java 10 10

// Testing with Java library path:
// java -Djava.library.path=./target/release --enable-native-access=ALL-UNNAMED --enable-preview --source 21 ./benchmarks/FibSequence/fibjava/Bench.java 10 10

// Latest working version:
// java --enable-native-access=ALL-UNNAMED --enable-preview --source 21 ./benchmarks/FibSequence/fibjava/Bench.java 10 10

class Bench {
    public static void main(String[] args) {

        // Finding os
        var os = System.getProperty("os.name");

        // Finding the path of library (and loading it)
        var dll_path = System.getProperty("user.dir") + "/target/release/";
        if (os.equals("Linux")) {
            dll_path = dll_path + "librapl_lib.so";
        } else if (os.equals("Windows 11")) {
            dll_path = dll_path + "rapl_lib.dll";
        } else {
            System.out.println("OS not supported");
            return;
        }

        System.load(dll_path);

        // Loading functions
        MemorySegment start_rapl_symbol = SymbolLookup.loaderLookup().find("start_rapl").get();
        MethodHandle start_rapl = Linker.nativeLinker().downcallHandle(start_rapl_symbol,
                    FunctionDescriptor.of(ValueLayout.JAVA_INT));

        MemorySegment stop_rapl_symbol = SymbolLookup.loaderLookup().find("stop_rapl").get();
        MethodHandle stop_rapl = Linker.nativeLinker().downcallHandle(stop_rapl_symbol,
                    FunctionDescriptor.of(ValueLayout.JAVA_INT));

        
        // Getting arguments
        // converting json array to java array
        String[] data = args[0].replace("{","").replace("}","").split(",");
        List<Long> mergeParam = Arrays.stream(data).map(String::trim).map(Long::valueOf).toList();
        int loop_count = Integer.parseInt(args[1]);

        /*
        // works without arena as seen below, but not sure if it is correct to do so
        // the code is commented out here in case it is needed later

        try (Arena arena = Arena.ofConfined()) {
            start_rapl.invoke();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        */

        // instantiting sorter
        Merge sorter = new Merge();


        // Running benchmark
        // Note that this could potentially be optimized away
        // by the compiler due to the fact that the result is not used.
        for (int i = 0; i < loop_count; i++) {
            try {
                start_rapl.invoke();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            List<Long> sorted = sorter.mergeSort(mergeParam);

            try {
                stop_rapl.invoke();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            System.out.println(sorted.toString());
        }

        /*
        try (Arena arena = Arena.ofConfined()) {
            stop_rapl.invoke();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        */
    }

    // Test class (implementation was a class in rosetta)
    public class Merge{
        public static <E extends Comparable<? super E>> List<E> mergeSort(List<E> m){
            if(m.size() <= 1) return m;
    
            int middle = m.size() / 2;
            List<E> left = m.subList(0, middle);
            List<E> right = m.subList(middle, m.size());
    
            right = mergeSort(right);
            left = mergeSort(left);
            List<E> result = merge(left, right);
    
            return result;
        }
    
        public static <E extends Comparable<? super E>> List<E> merge(List<E> left, List<E> right){
            List<E> result = new ArrayList<E>();
            Iterator<E> it1 = left.iterator();
            Iterator<E> it2 = right.iterator();
    
            E x = it1.next();
            E y = it2.next();
            while (true){
                //change the direction of this comparison to change the direction of the sort
                if(x.compareTo(y) <= 0){
            result.add(x);
            if(it1.hasNext()){
                x = it1.next();
            }else{
                result.add(y);
                while(it2.hasNext()){
                result.add(it2.next());
                }
                break;
            }
            }else{
            result.add(y);
            if(it2.hasNext()){
                y = it2.next();
            }else{
                result.add(x);
                while (it1.hasNext()){
                result.add(it1.next());
                }
                break;
            }
            }
            }
            return result;
        }
    }
}