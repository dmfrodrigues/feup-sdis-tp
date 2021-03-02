## Lab2 - UDP Multicast

[![test-lab2](https://github.com/dmfrodrigues/feup-sdis-tp/actions/workflows/test-lab2.yml/badge.svg)](https://github.com/dmfrodrigues/feup-sdis-tp/actions/workflows/test-lab2.yml)

[Guide](https://web.fe.up.pt/~pfs/aulas/sd2021/labs/l02/mcast_l02.html)

### Question

- **Q:** What are the advantages of the server broadcasting the socket address/port, compared to that used in the previous lab?
- **A:** The advantage is that the server does not need to be at a fixed IP address or use a fixed port, as any computer in the local network that wants to use the service can listen to the hardcoded multicast group and check the address through which the server can be accessed.
