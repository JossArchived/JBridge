![](https://i.imgur.com/SJJEDF4.png)

<p align="center">
JBridge facilitates communication in minecraft bedrock networks, between Nukkit servers and WaterdogPE proxies using Redis

## Features 📃

- [x] Server management system available for both clients


  ![](https://i.imgur.com/ymmYZm9.png)
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

![](https://i.imgur.com/Tt0j4w7.png)
![](https://i.imgur.com/gpkr0M6.png)

- [ ] Multi-group support 

## Download and setup 🛒

<details>
    <summary>Nukkit</summary>

1) Download the latest jbridge-nukkit.jar [here](https://github.com/Josscoder/JBridge/releases/latest)
2) Place the .jar in the plugins/ folder of your server
3) Start your server
4) Configure the service and your redis client in plugins/JBridge/config.yml
</details>

<details>
    <summary>Waterdog</summary>

1) Download the latest jbridge-waterdogpe.jar [here](https://github.com/Josscoder/JBridge/releases/latest)
2) Place the .jar in the plugins/ folder of your server
3) Start your server
4) Configure the service and your redis client in plugins/JBridge/config.yml
</details>

<details>
    <summary>How to configure</summary>

```yml
debug: true #Just for development, to show the internal process of JBridge commons
#recommendation to set this to false to avoid saturating the console with logs

redis: #the configuration of your redis
  hostname: "localhost"
  port: 6379
  password: "yourpasswordhere"

service: #configuration of your service
  id: "hub-1" #the id of your service, if you remove this section, the system will generate a custom id each time the plugin is enabled
  group: "hub" #the group your service belongs to
  region: "us" #the region to which your service belongs
  branch: "dev" #the branch of your service, if it is "dev" the address will automatically change to "127.0.0.1"
  address: "0.0.0.0" #the address of your service, if you remove this section you will get the address that is in server.propierties
```
</details>

## For developers 🧑‍💻
### Add JBridge to your project

[![](https://jitpack.io/v/Josscoder/JBridge.svg)](https://jitpack.io/#Josscoder/JBridge)
[![Java CI with Maven](https://github.com/Josscoder/JBridge/actions/workflows/maven.yml/badge.svg)](https://github.com/Josscoder/JBridge/actions/workflows/maven.yml)  

### Common
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Josscoder.JBridge</groupId>
        <artifactId>jbridge-common</artifactId>
        <version>TAG</version>
    </dependency>
</dependencies>
```

### Nukkit
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Josscoder.JBridge</groupId>
        <artifactId>jbridge-nukkit</artifactId>
        <version>TAG</version>
    </dependency>
</dependencies>
```

### WaterdogPE
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Josscoder.JBridge</groupId>
        <artifactId>jbridge-waterdogpe</artifactId>
        <version>TAG</version>
    </dependency>
</dependencies>
```

### Code examples

<details>
    <summary>Register packets</summary>

```java
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.packet.DataPacket;
import me.josscoder.jbridge.packet.PacketManager;

public class Test {
    
    static class HelloWorldPacket extends DataPacket {
        
        public String message;

        public HelloWorldPacket() {
            super((byte) 0x53);
        }

        @Override
        public void encode(ByteArrayDataOutput output) {
            output.writeUTF(message);
        }

        @Override
        public void decode(ByteArrayDataInput input) {
            message = input.readUTF();
        }
    }

    public static void main(String[] args) {
        PacketManager packetManager = JBridgeCore.getInstance().getPacketManager();
        
        packetManager.subscribePacket(new HelloWorldPacket());
    }
}
```
</details>
<details>
    <summary>Register PacketHandlers</summary>

```java
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.packet.DataPacket;
import me.josscoder.jbridge.packet.PacketHandler;
import me.josscoder.jbridge.packet.PacketManager;

public class Test {

  static class HelloWorldPacket extends DataPacket {

    public String message;

    public HelloWorldPacket() {
      super((byte) 0x53);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
      output.writeUTF(message);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
      message = input.readUTF();
    }
  }

  public static void main(String[] args) {
    PacketManager packetManager = JBridgeCore.getInstance().getPacketManager();

    packetManager.subscribePacket(new HelloWorldPacket());

    packetManager.addPacketHandler(new PacketHandler() {
      @Override
      public void onSend(DataPacket packet) {
        if (packet instanceof HelloWorldPacket) {
          System.out.println("Sending hello world message!!");
        }
      }

      @Override
      public void onReceive(DataPacket packet) {
        if (packet instanceof HelloWorldPacket) {
          System.out.println(((HelloWorldPacket) packet).message);
        }
      }
    });
  }
}
```
</details>
<details>
    <summary>Using AsyncPacket</summary>

```java
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.packet.AsyncPacket;
import me.josscoder.jbridge.packet.DataPacket;
import me.josscoder.jbridge.packet.PacketHandler;
import me.josscoder.jbridge.packet.PacketManager;

public class Test {
    
    static class HelloWorldPacket extends DataPacket implements AsyncPacket {
        
        public String message;

        public HelloWorldPacket() {
            super((byte) 0x53);
        }

        @Override
        public void encode(ByteArrayDataOutput output) {
            output.writeUTF(message);
        }

        @Override
        public void decode(ByteArrayDataInput input) {
            message = input.readUTF();
        }
    }

    public static void main(String[] args) {
        PacketManager packetManager = JBridgeCore.getInstance().getPacketManager();
        
        packetManager.subscribePacket(new HelloWorldPacket());
        
        packetManager.addPacketHandler(new PacketHandler() {
            @Override
            public void onSend(DataPacket packet) {
                if (packet instanceof HelloWorldPacket) {
                    System.out.println("Sending hello world message!!");
                }
            }

            @Override
            public void onReceive(DataPacket packet) {
                if (packet instanceof HelloWorldPacket) {
                    System.out.println("HI!! receiving async packet " + ((HelloWorldPacket) packet).message);
                }
            }
        });
    }
}
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
