runbenchmark(){
    language=$1
    testName=$2
    cmd=$3
    input=$4
    inputSize=$5

    #start message
    time=$(date)
    echo --- Starting $language --- "time:" $time

    # run benchmark
    $cmd $input

    # waiting for file
    sleep 5s

    #adding input or inputSize, depending on whether inputSize is present.
    if [ -n "$inputSize" ]; then
        bash utils/append_to_latest_csv.sh "${language}_${testName}_${inputSize}"
    else
        bash utils/append_to_latest_csv.sh "${language}_${testName}_${input}"
    fi

    # stop message
    time=$(date)
    echo --- $language Done --- "time:" $time
    echo
}

#Structure of runbenchmark call (remember to include this file at the top of bechmark sh file): 
#runbenchmark language testName cmd (input) (inputSize)