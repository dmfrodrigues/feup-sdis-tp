#!/bin/bash
set -e

TIMEOUT=40
KEYSTORE_PASSWORD="123456"
TRUSTSTORE_PATH="../test/truststore"
TRUSTSTORE_PASSWORD="123456"
SERVER_KEYS_PATH="../test/server"
CLIENT_KEYS_PATH="../test/client"
SERVER_KEYS="-Djavax.net.ssl.keyStore=$SERVER_KEYS_PATH -Djavax.net.ssl.keyStorePassword=$KEYSTORE_PASSWORD -Djavax.net.ssl.trustStore=$TRUSTSTORE_PATH -Djavax.net.ssl.trustStorePassword=$TRUSTSTORE_PASSWORD"
CLIENT_KEYS="-Djavax.net.ssl.keyStore=$CLIENT_KEYS_PATH -Djavax.net.ssl.keyStorePassword=$KEYSTORE_PASSWORD -Djavax.net.ssl.trustStore=$TRUSTSTORE_PATH -Djavax.net.ssl.trustStorePassword=$TRUSTSTORE_PASSWORD"
CYPHERS="TLS_AES_256_GCM_SHA384 TLS_AES_128_GCM_SHA256 TLS_CHACHA20_POLY1305_SHA256 TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384 TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256"

test () {
    echo -en "$1\t"
    expected=$3
    output=$($2)
    if [ $? != 0 ]; then
        echo -e "\e[1m\e[31m[Failed]\e[0m: return code is not zero"
        kill $PID
        exit 1
    fi
    echo $expected > expected.txt
    echo $output > output.txt
    if ! diff expected.txt output.txt > /dev/null ; then
        echo -e "\e[1m\e[31m[Failed]\e[0m: expected different from output"
        kill $PID
        exit 1
    fi
    echo -e "\e[1m\e[32m[Passed]\e[0m"
}

cd bin
timeout $TIMEOUT java $SERVER_KEYS SSLServer 4040 $CYPHERS > /dev/null  & PID=$!
echo "Started server with PID $PID"
sleep 1
test "test1-01" "java $CLIENT_KEYS SSLClient localhost 4040 register www.fe.up.pt 192.168.0.1 $CYPHERS" "SSLClient: register www.fe.up.pt 192.168.0.1 : 1"
test "test1-02" "java $CLIENT_KEYS SSLClient localhost 4040 register www.fe.up.pt 192.168.0.1 $CYPHERS" "SSLClient: register www.fe.up.pt 192.168.0.1 : 1"
test "test1-03" "java $CLIENT_KEYS SSLClient localhost 4040 register www.google.com 123.123.123.123 $CYPHERS" "SSLClient: register www.google.com 123.123.123.123 : 2"
test "test1-04" "java $CLIENT_KEYS SSLClient localhost 4040 register www.google.com 123.123.123.123 $CYPHERS" "SSLClient: register www.google.com 123.123.123.123 : 2"
test "test1-05" "java $CLIENT_KEYS SSLClient localhost 4040 register web.fe.up.pt 128.128.128.128 $CYPHERS" "SSLClient: register web.fe.up.pt 128.128.128.128 : 3"
test "test1-06" "java $CLIENT_KEYS SSLClient localhost 4040 lookup www.fe.up.pt $CYPHERS" "SSLClient: lookup www.fe.up.pt : www.fe.up.pt 192.168.0.1"
test "test1-07" "java $CLIENT_KEYS SSLClient localhost 4040 lookup www.fe.up.pt $CYPHERS" "SSLClient: lookup www.fe.up.pt : www.fe.up.pt 192.168.0.1"
test "test1-08" "java $CLIENT_KEYS SSLClient localhost 4040 lookup www.google.com $CYPHERS" "SSLClient: lookup www.google.com : www.google.com 123.123.123.123"
test "test1-09" "java $CLIENT_KEYS SSLClient localhost 4040 lookup web.fe.up.pt $CYPHERS" "SSLClient: lookup web.fe.up.pt : web.fe.up.pt 128.128.128.128"
kill $PID
