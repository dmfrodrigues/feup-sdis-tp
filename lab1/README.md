## Lab1 - Programming with Unicast Datagram Sockets

[![test-lab1](https://github.com/dmfrodrigues/feup-sdis-tp/actions/workflows/test-lab1.yml/badge.svg)](https://github.com/dmfrodrigues/feup-sdis-tp/actions/workflows/test-lab1.yml)

[Guide](https://web.fe.up.pt/~pfs/aulas/sd2021/labs/l01/udp_l01.html)

### Food for thought

- **Q:** How can you prevent the client from hanging indefinitely in case of failure of the server or of lost messages?
- **A:** 

- **Q:** Is this a problem for the server?
- **A:** This is not a problem for the server, because the server runs indefinitely, and failure is ignored so that it is the client that is responsible to repeat the request if it does not receive a response.
