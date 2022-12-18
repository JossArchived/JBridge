![](https://i.imgur.com/SJJEDF4.png)

<p align="center">
JBridge facilitates communication in minecraft bedrock networks, between Nukkit servers and WaterdogPE proxies using Redis

## Features 📃

- [x] Server management system available for both clients


  ![](https://i.imgur.com/3BmcS9L.png)
  Example: This code is used in the same way for jbridge-lobby-nukkit and jbridge-lobby-waterdogpe.

as the first parameter a list with the group of services for the lobby and as the second parameter the sort mode, in this case LOWEST, the algorithm will choose the lobby server with the lowest number of players to balance the lobby servers


- [x] Packet system using message broker

We have a packet system in which you can create your own packets and manage them in a simple way, of course if you are a developer, as an additional note you can create async packets, especially for tasks that can cause lag,
when a packet arrives at a service, it will execute the async by calling the method "onReceive(DataPacket packet);" of your PacketHandler

- [x] Lobby System, [JBirdgeLobby](https://github.com/Josscoder/JBridge/tree/release/jbridge-lobby)

As an example to use jbridge, we have a lobby balancing system, compatible with nukkit and waterdogpe, from nukkit you will have the /hub command available,
From waterdogpe you will have a Custom JoinHandler (which will look for a balanced server in the lobby service list and send the player) and a Custom ReconnectHandler (which will be used when the player is disconnected from an old service for some reason, and will be sent to a balanced lobby).

- [x] Custom commands to manage servers/services

By WaterdogPE we have the commands:

- /wdlist: command waterdgope, overwritten for more compatibility with JBridge, will show the list of servers, with their ids, players, group, region and branch (PERMISSION: waterdog.command.list.permission, ALIASES: "servicelist", "services", "servers", "glist", "rglist")
- /whereami: Provide information about the proxy you are on (ALIASES: "connection")

By Nukkit we have the commands:

- /transfer: It will send you to the server that you write, you can write the first two letters of the server and it will autocomplete (PERMISSION: jbrdige.command.transfer)
- /whereami: Provide information about the server you are on (ALIASES: "connection")


- [x] Custom Query and Ping events

Each time a new service is added, the new available slots are added

![](https://i.imgur.com/G2e9aKF.png)
![](https://i.imgur.com/YQJrVsA.png)
![](https://i.imgur.com/qBUSlX8.png)
![](https://i.imgur.com/EzED4Oi.png)

- [x] Server Cluster System

As I mentioned before, jbridge can manage services by groups, you can see more about it [here](https://github.com/Josscoder/JBridge/blob/release/jbridge-common/src/main/java/me/josscoder/jbridge/service/ServiceHandler.java)

- [x] Automatic server registration at WaterdogPE without the need to specify servers manually in the Waterdog config.

![](https://i.imgur.com/rAxqlCw.png)
![](https://i.imgur.com/Wy5ouEa.png)

## Download and setup 🛒

<details>
    <summary>Nukkit</summary>

1) 
2)
3)
4)
5)
</details>

<details>
    <summary>Waterdog</summary>

1)
2)
3)
4)
5)
</details>

## For developers 🧑‍💻
### Add JBridge to your project

<details>
    <summary>Maven</summary>

```xml
```
</details>

<details>
    <summary>Gradle</summary>

```gradle
```
</details>

### Code examples

<details>
    <summary>Register packets</summary>

```java
```
</details>
<details>
    <summary>Register PacketHandlers</summary>

```java
```
</details>
<details>
    <summary>Using ServiceHandler</summary>

```java
```
</details>

### Included libraries 🛠️

- [Lombok: Used as Annotation library](https://projectlombok.org/)
- [Jedis: Used as Message Broker](https://github.com/redis/jedis)
- [Google Guava: Used as coder, decoder & cache handler](https://github.com/google/guava)
- [WaterdogPE: Minecraft Bedrock Proxy](https://github.com/WaterdogPE/WaterdogPE)
- [Nukkit: Minecraft Bedrock Software](https://github.com/CloudburstMC/Nukkit)

## Credits 🙋‍♂️🙋‍♀️

- [DinamycServers: cache handler & redis implementation](https://github.com/theminecoder/DynamicServers)
- [PacketListenerAPI: packet handler system](https://www.spigotmc.org/resources/api-packetlistenerapi.2930/)
- [NetherGames: Inspiration](https://forums.nethergames.org/threads/netsys-network-communication-system.10514/)

## License 🚩
JBridge is licensed under the permissive GNU General Public License v2.0. Please see [`LICENSE`](https://github.com/Josscoder/JBridge/blob/release/LICENSE) for more information.