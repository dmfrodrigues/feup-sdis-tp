#!/bin/bash

TIMEOUT=20
SERVICE_PORT=4040
MCAST_ADDR=230.0.0.1
MCAST_PORT=8888

test () {
    echo -en "$1\t"
    output=$($2)
    if [ $? == 0 ] && [ "$output" == "$3" ]; then
        echo -e "\e[1m\e[32m[Passed]\e[0m"
    else
        echo $output
        echo -e "\e[1m\e[31m[Failed]\e[0m"
        kill $PID
        exit 1
    fi
}

cd bin
timeout $TIMEOUT java Server $SERVICE_PORT $MCAST_ADDR $MCAST_PORT > /dev/null & PID=$!
echo "Started server with PID $PID"
sleep 3
test "test1-01" "timeout $TIMEOUT java Client $MCAST_ADDR $MCAST_PORT register www.fe.up.pt 192.168.0.1" "multicast: 230.0.0.1 8888: 0.0.0.0 4040
Client: register www.fe.up.pt 192.168.0.1 : 1"
test "test1-02" "timeout $TIMEOUT java Client $MCAST_ADDR $MCAST_PORT register www.fe.up.pt 192.168.0.1" "multicast: 230.0.0.1 8888: 0.0.0.0 4040
Client: register www.fe.up.pt 192.168.0.1 : 1"
test "test1-03" "timeout $TIMEOUT java Client $MCAST_ADDR $MCAST_PORT register www.google.com 123.123.123.123" "multicast: 230.0.0.1 8888: 0.0.0.0 4040
Client: register www.google.com 123.123.123.123 : 2"
test "test1-04" "timeout $TIMEOUT java Client $MCAST_ADDR $MCAST_PORT register www.google.com 123.123.123.123" "multicast: 230.0.0.1 8888: 0.0.0.0 4040
Client: register www.google.com 123.123.123.123 : 2"
test "test1-05" "timeout $TIMEOUT java Client $MCAST_ADDR $MCAST_PORT register web.fe.up.pt 128.128.128.128" "multicast: 230.0.0.1 8888: 0.0.0.0 4040
Client: register web.fe.up.pt 128.128.128.128 : 3"
test "test1-06" "timeout $TIMEOUT java Client $MCAST_ADDR $MCAST_PORT lookup www.fe.up.pt" "multicast: 230.0.0.1 8888: 0.0.0.0 4040
Client: lookup www.fe.up.pt : www.fe.up.pt 192.168.0.1"
test "test1-07" "timeout $TIMEOUT java Client $MCAST_ADDR $MCAST_PORT lookup www.fe.up.pt" "multicast: 230.0.0.1 8888: 0.0.0.0 4040
Client: lookup www.fe.up.pt : www.fe.up.pt 192.168.0.1"
test "test1-08" "timeout $TIMEOUT java Client $MCAST_ADDR $MCAST_PORT lookup www.google.com" "multicast: 230.0.0.1 8888: 0.0.0.0 4040
Client: lookup www.google.com : www.google.com 123.123.123.123"
test "test1-09" "timeout $TIMEOUT java Client $MCAST_ADDR $MCAST_PORT lookup web.fe.up.pt" "multicast: 230.0.0.1 8888: 0.0.0.0 4040
Client: lookup web.fe.up.pt : web.fe.up.pt 128.128.128.128"
kill $PID
