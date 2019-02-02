# Mixin Messenger application development tutorial in java
This tutorial will let you know how to write a Mixin Messenger bot in Java. The bot can receive and response to user's message. User can pay Bitcoin to bot and bot can transfer Bitcoin to user immediately.

## Index
1. [Create bot and receive message from user](https://github.com/wenewzhang/mixin_labs-java-bot)
2. [Receive and send Bitcoin](https://github.com/wenewzhang/mixin_labs-php-bot/blob/master/README2.md)

## Install java on your OS
On macOS, download java jdk from [here](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html),double click jdk-11.0.2_osx-x64_bin.dmg, and then click on JDK 11.0.2.pkg in the pop window, follow the prompt to finish the installation, the java could be installed in /Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home/bin/ directory, add this path to environment variable $PATH,
```bash
echo 'export PATH=/Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home/bin/:$PATH' >> ~/.bash_profile
source ~/.bash_profile
```
If installed successfully, execute command of **java --version** that will get message like below:
```bash
wenewzha:mixin_labs-java-bot wenewzhang$ java --version
java 11.0.2 2019-01-15 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.2+9-LTS)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.2+9-LTS, mixed mode)
```
on Ubuntu
```bash
apt update
apt upgrade
apt install unzip
java --version
```
On Ubuntu 16.04, the openjdk edition java has been installed default with OS, execute command of **java --version** that will get message like below:
```bash
root@ubuntu:~# java --version
openjdk 10.0.2 2018-07-17
OpenJDK Runtime Environment (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4)
OpenJDK 64-Bit Server VM (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4, mixed mode)
```
## Install Gradle on your OS
This tutorial use Gradle to build, you can download the latest gradle [here](https://gradle.org/install/#manually)
on macOS
```bash
brew update
brew install gradle
```
on Ubuntu, The gradle is too old, so we need to download it by manual.
```bash
cd ~/Downloads
wget https://services.gradle.org/distributions/gradle-5.1.1-bin.zip
unzip gradle-5.1.1-bin.zip
```
After unzip the gradle-5.1.1-bin.zip, Let's add the path to $PATH environment variable
```bash
echo 'export PATH=/root/gradle-5.1.1/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```
When gradle installed, execute command of  **gradle -v** could output message like below:
```bash
root@ubuntu:~# gradle -v
------------------------------------------------------------
Gradle 5.1.1
------------------------------------------------------------
...
```
### Create you first app in developer dashboard
Create an app by following [tutorial](https://mixin-network.gitbook.io/mixin-network/mixin-messenger-app/create-bot-account).

### Generate parameter for your app
Remember to [generate parameter](https://mixin-network.gitbook.io/mixin-network/mixin-messenger-app/create-bot-account#generate-secure-parameter-for-your-app)
and write down required information, they are required in config.java file soon.

![mixin_network-keys](https://github.com/wenewzhang/mixin_labs-php-bot/blob/master/mixin_network-keys.jpg)

## Hello,World!
Go to your workspace, create the project mixin_labs-java-bot directory, execute command of **gradle init** which to generate the project basic information.
```bash
gradle init --dsl kotlin --type java-application --test-framework junit --project-name mixin_labs-java-bot
```

Go into src/main/java/mixin_labs/java/bot, create a file: Config.java. fill the following content in it.
> Config.java
```java
package mixin_labs.java.bot;
import mixin.java.sdk.MixinUtil;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import mixin.java.sdk.PrivateKeyReader;
public class Config {

public static final String CLIENT_ID     = "b1ce2967-a534-417d-bf12-c86571e4eefa";
public static final String CLIENT_SECRET = "e6b14c6bbb20a43c603c468e225e6e4c666c940792cde43e41b34c3f1dd45713";
public static final String PIN           = "536071";
public static final String SESSION_ID    = "2f1c44a3-d4d2-4dd2-bdb6-8eda67694b91";
public static final String PIN_TOKEN     = "ajJJngHmWgIfH3S2mgH4bAsoPeoXV6hI1KoTZW9AvFUK1R8e28X1zVRCcrOMVeXkvBKQeEMgRdX1kRgH3ksITTBm2mgK5eUnfBHUuRC85oKoQGB9e2Bp4O4ZKGg/6bqLeD66pnBPcO2s7VtgLSAK0tHa2jMzmGlWuxsO6Wo5JHE=";

  private static RSAPrivateKey loadPrivateKey() {
    try {

      PrivateKey key =
        new PrivateKeyReader(Config.class.getClassLoader().getResourceAsStream("rsa_private_key.txt"))
          .getPrivateKey();
      System.out.println(key);
      return (RSAPrivateKey) key;
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  public static final RSAPrivateKey RSA_PRIVATE_KEY = loadPrivateKey();
  public static final byte[] PAY_KEY = MixinUtil.decrypt(RSA_PRIVATE_KEY, PIN_TOKEN, SESSION_ID);
}

```
Replace the value with **YOUR APP**'s  CLIENT_ID, client_id, CLIENT_SECRET, and the PIN, PIN_TOKEN, SESSION_ID,
Create App.java, fill the below content in it
> App.java
```java
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package mixin_labs.java.bot;
import mixin.java.sdk.MixinBot;
import mixin.java.sdk.MixinUtil;
import mixin.java.sdk.MIXIN_Category;
import mixin.java.sdk.MIXIN_Action;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
// import java.util.Base64;
import org.apache.commons.codec.binary.Base64;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class App {

    public static void main(String[] args) {
        MixinBot.connectToRemoteMixin(new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
          System.out.println("[onOpen !!!]");
          System.out.println("request header:" + response.request().headers());
          System.out.println("response header:" + response.headers());
          System.out.println("response:" + response);

          // Request unread messages
          MixinBot.sendListPendingMessages(webSocket);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
          System.out.println("[onMessage !!!]");
          System.out.println("text: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
          try {
            System.out.println("[onMessage !!!]");
            String msgIn = MixinUtil.bytesToJsonStr(bytes);
            System.out.println("json: " + msgIn);
            JsonObject obj = new JsonParser().parse(msgIn).getAsJsonObject();
            MIXIN_Action action = MIXIN_Action.parseFrom(obj);
            System.out.println(action);
            MIXIN_Category category = MIXIN_Category.parseFrom(obj);
            System.out.println(category);
            if (action == MIXIN_Action.CREATE_MESSAGE && obj.get("data") != null &&
                category != null ) {
              String userId;
              String messageId = obj.get("data").getAsJsonObject().get("message_id").getAsString();
              MixinBot.sendMessageAck(webSocket, messageId);
              switch (category) {
                case PLAIN_TEXT:
                    String conversationId =
                      obj.get("data").getAsJsonObject().get("conversation_id").getAsString();
                    userId =
                      obj.get("data").getAsJsonObject().get("user_id").getAsString();
                    byte[] msgData = Base64.decodeBase64(obj.get("data").getAsJsonObject().get("data").getAsString());
                    MixinBot.sendText(webSocket,conversationId,userId,new String(msgData,"UTF-8"));
                    break;
                default:
                    System.out.println("Category: " + category);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
          System.out.println("[onClosing !!!]");
          System.out.println("code: " + code);
          System.out.println("reason: " + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
          System.out.println("[onClosed !!!]");
          System.out.println("code: " + code);
          System.out.println("reason: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
          System.out.println("[onFailure !!!]");
          System.out.println("throwable: " + t);
          System.out.println("response: " + response);
        }
      }, Config.RSA_PRIVATE_KEY, Config.CLIENT_ID, Config.SESSION_ID);
    }
}

```
Go into src/main/resources, create a file: rsa_private_key.txt, Fill the private key which you have already generated in dashboard.
> rsa_private_key.txt
```java
-----BEGIN RSA PRIVATE KEY-----
...
-----END RSA PRIVATE KEY-----
```

Go back to the project directory, download the mixin-java-sdk from github,

```bash
mkdir libs
cd libs
wget https://github.com/wenewzhang/mixin-java-sdk/releases/download/v2/mixin-java-sdk.jar
```
Add dependencies package info into build.gradle.kts
```kotlin
dependencies {
    // This dependency is found on compile classpath of this component and consumers.
    implementation("com.google.guava:guava:26.0-jre")
    // dependent on mixin-java-sdk, copy it to libs directory
    compile(files("libs/mixin-java-sdk.jar"))
    implementation("commons-codec:commons-codec:1.11")
    implementation("com.auth0:java-jwt:3.5.0")
    implementation("com.squareup.okio:okio:2.2.1")
    implementation("com.squareup.okhttp3:okhttp:3.12.1")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.20")
    // Use JUnit test framework
    testImplementation("junit:junit:4.12")
}
```
Go to the directory src/test/java/mixin_labs/java/bot, comment the test code,
> AppTest.java
```java
        // assertNotNull("app should have a greeting", classUnderTest.getGreeting());
```
The last step, go back in mixin_labs-java-bot directory, build it and run,
```bash
gradle build
gradle run
```
If you look message like below, well done. Congratulations!

```bash
response:Response{protocol=http/1.1, code=101, message=Switching Protocols, url=https://blaze.mixin.one/}
[onMessage !!!]
json: {"id":"4ee01b68-817e-4f29-bcb4-b40f7c163f61","action":"LIST_PENDING_MESSAGES"}
LIST_PENDING_MESSAGES
```

![mixin_messenger](https://github.com/wenewzhang/mixin_labs-php-bot/blob/master/helloworld.jpeg)

## Source code explanation
#### Connect to Mixin Messenger Server
```java
MixinBot.connectToRemoteMixin(new WebSocketListener() {
@Override
public void onOpen(WebSocket webSocket, Response response) {
  MixinBot.sendListPendingMessages(webSocket);
}
```
Connect to Mixin Messenger server then send message "LISTPENDINGMESSAGES" to it. Server will send unread message to bot.

#### Receive message callback
```java
        public void onMessage(WebSocket webSocket, ByteString bytes) {
          try {
            System.out.println("[onMessage !!!]");
            String msgIn = MixinUtil.bytesToJsonStr(bytes);

```
onMessage func will be called when server push message to bot

#### Send message response
```java
String messageId = obj.get("data").getAsJsonObject().get("message_id").getAsString();
MixinBot.sendMessageAck(webSocket, messageId);
```

Send the message "READ"  to the server let it knows this message has already been read by bot.

#### Echo chat contant
```java
              switch (category) {
                case PLAIN_TEXT:
                    String conversationId =
                      obj.get("data").getAsJsonObject().get("conversation_id").getAsString();
                    userId =
                      obj.get("data").getAsJsonObject().get("user_id").getAsString();
                    byte[] msgData = Base64.decodeBase64(obj.get("data").getAsJsonObject().get("data").getAsString());
                    MixinBot.sendText(webSocket,conversationId,userId,new String(msgData,"UTF-8"));
```

### End
Now your bot is running. You can try your idea now, enjoy!

A full code is [here](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/src/main/java/mixin_labs/java/bot/App.java)
