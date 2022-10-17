# File-Transmitter
**What is it?**

It is app which allows you to send files via network.

**Launching**

1. Download repository
2. Move to repository folder
3. Launch server first
```
java -jar target/Server.jar <server_port>
```
4. Now server is listening, you can launch clients
```
java -jar target/Client.jar <file_path> <server_address> <server_port>
```

**Notes**

There are 3 modules in repository: Server, Client and Common. </br>
The last one contains message classes which server and client use to communicate. </br>
Each 3 seconds server sends instance speed (bytes read in a second) and session speed. </br>
Upon completing transfer server sends status to client.
