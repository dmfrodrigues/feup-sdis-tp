#!/bin/bash
set -e

TIMEOUT=30

test () {
    echo -en "$1\t"
    output=$($2)
    if [ "$output" == "$3" ]; then
        echo -e "\e[1m\e[32m[Passed]\e[0m"
    else
        echo -e "\e[1m\e[31m[Failed]\e[0m"
        echo "Ran: $1"
        echo "Expected:"
        echo "$2"
        echo "Got:"
        echo "$output"
        kill $PID
        exit 1
    fi
}

cd bin
timeout $TIMEOUT rmiregistry & RMI_PID=$!
sleep 1
timeout $TIMEOUT java Server WorkInterface > /dev/null & PID=$!
echo "Started server with PID $PID"
sleep 1
test "test1-01" "java Client localhost WorkInterface register www.fe.up.pt 192.168.0.1" "Client: register www.fe.up.pt 192.168.0.1 : 1"
test "test1-02" "java Client localhost WorkInterface register www.fe.up.pt 192.168.0.1" "Client: register www.fe.up.pt 192.168.0.1 : 1"
test "test1-03" "java Client localhost WorkInterface register www.google.com 123.123.123.123" "Client: register www.google.com 123.123.123.123 : 2"
test "test1-04" "java Client localhost WorkInterface register www.google.com 123.123.123.123" "Client: register www.google.com 123.123.123.123 : 2"
test "test1-05" "java Client localhost WorkInterface register web.fe.up.pt 128.128.128.128" "Client: register web.fe.up.pt 128.128.128.128 : 3"
test "test1-06" "java Client localhost WorkInterface lookup www.fe.up.pt" "Client: lookup www.fe.up.pt : www.fe.up.pt 192.168.0.1"
test "test1-07" "java Client localhost WorkInterface lookup www.fe.up.pt" "Client: lookup www.fe.up.pt : www.fe.up.pt 192.168.0.1"
test "test1-08" "java Client localhost WorkInterface lookup www.google.com" "Client: lookup www.google.com : www.google.com 123.123.123.123"
test "test1-09" "java Client localhost WorkInterface lookup web.fe.up.pt" "Client: lookup web.fe.up.pt : web.fe.up.pt 128.128.128.128"
kill $PID
kill $RMI_PID
