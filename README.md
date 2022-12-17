![](https://i.imgur.com/SJJEDF4.png)

<p align="center">
JBridge facilitates communication in minecraft bedrock networks, between Nukkit servers and WaterdogPE proxies using Redis

## Features 📃

- [x] Server management system available for both clients, [look here](https://github.com/Josscoder/JBridge/blob/release/jbridge-common/src/main/java/me/josscoder/jbridge/service/ServiceHandler.java) and [here](https://github.com/Josscoder/JBridge/blob/release/jbridge-common/src/main/java/me/josscoder/jbridge/service/ServiceInfo.java)



- [x] Packet system using message broker, [look here](https://github.com/Josscoder/JBridge/blob/release/jbridge-common/src/main/java/me/josscoder/jbridge/packet/PacketManager.java) and [here](https://github.com/Josscoder/JBridge/blob/release/jbridge-common/src/main/java/me/josscoder/jbridge/packet/DataPacket.java)



- [x] Lobby System, [look here]()



- [x] Custom commands to manage servers/services



- [x] Custom Query and Ping events



- [x] Server Cluster System



- [x] Automatic server registration at WaterdogPE without the need to specify servers manually in the Waterdog config.

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

- [DinamycServers: cache system & redis implementation](https://github.com/theminecoder/DynamicServers)
- [PacketListenerAPI: packet handler system](https://www.spigotmc.org/resources/api-packetlistenerapi.2930/)
- [NetherGames: Inspiration](https://forums.nethergames.org/threads/netsys-network-communication-system.10514/)

## License 🚩
JBridge is licensed under the permissive GNU General Public License v2.0. Please see [`LICENSE`](https://github.com/Josscoder/JBridge/blob/release/LICENSE) for more information.