# 基于Mixin Network的Java比特币开发教程
![cover](https://github.com/wenewzhang/mixin_labs-java-bot/raw/master/bitcoin_wallet-java/mixin-bitcoin-java.jpg)

[Mixin Network](https://mixin.one) 是一个免费的 极速的端对端加密数字货币交易系统.
在本章中，你可以按教程在Mixin Messenger中创建一个bot来接收用户消息, 学到如何给机器人转**比特币** 或者 让机器人给你转**比特币**.

## 课程简介
1. [创建一个接受消息的机器人](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README-zhchs.md)
2. [机器人接受比特币并立即退还用户](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/README2-zhchs.md)

## 安装Java
如果你运行的是macOS, 手动到此[下载JDK12](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html), 下载完成后，双击 jdk-11.0.2_osx-x64_bin.dmg, 在弹出的新窗口中，点击 JDK 11.0.2.pkg文件，依提示一步一步完成安装， Java会安装在 /Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home/bin/ 目录中，将这个路径加入到$PATH

```bash
echo 'export PATH=/Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home/bin/:$PATH' >> ~/.bash_profile
source ~/.bash_profile
```
安装成功后，执行 **java --version** 将得到如下信息:

```bash
wenewzha:mixin_labs-java-bot wenewzhang$ java --version
java 11.0.2 2019-01-15 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.2+9-LTS)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.2+9-LTS, mixed mode)
```
Ubuntu
```bash
apt update
apt upgrade
apt install unzip
java --version
```
以Ubuntu 16.04为例， openjdk版本的Java已经安装好了， 执行 **java --version**来验证安装情况。
```bash
root@ubuntu:~# java --version
openjdk 10.0.2 2018-07-17
OpenJDK Runtime Environment (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4)
OpenJDK 64-Bit Server VM (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4, mixed mode)
```
## 在操作系统中安装最新版本的Gradle
本教程采用Gradle来构建，你可从下面的地址来下载安装！[Gradle下载](https://gradle.org/install/#manually)

macOS
```bash
brew update
brew install gradle
```
Ubuntu下的Gradle太旧，我们手动安装它:
```bash
cd ~/Downloads
wget https://services.gradle.org/distributions/gradle-5.1.1-bin.zip
unzip gradle-5.1.1-bin.zip
```
解压gradle-5.1.1-bin.zip后，增加安装目录到$PATH中：
```bash
echo 'export PATH=/root/gradle-5.1.1/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```
当Gradle安装成功后， 执行**gradle -v**来验证安装情况：
```bash
root@ubuntu:~# gradle -v
------------------------------------------------------------
Gradle 5.1.1
------------------------------------------------------------
...
```

### 创建第一个机器人APP
按下面的提示，到mixin.one创建一个APP[tutorial](https://mixin-network.gitbook.io/mixin-network/mixin-messenger-app/create-bot-account).

### 生成相应的参数
记下这些[生成的参数](https://mixin-network.gitbook.io/mixin-network/mixin-messenger-app/create-bot-account#generate-secure-parameter-for-your-app)
它们将用于Config.java中.

![mixin_network-keys](https://github.com/wenewzhang/mixin_labs-php-bot/blob/master/mixin_network-keys.jpg)

## Hello,World!
进入到你的工作目录，创建mixin_labs-java-bot目录, 执行**gradle init**来生成基本信息资料.
```bash
gradle init --dsl kotlin --type java-application --test-framework junit --project-name mixin_labs-java-bot
```
进入src/main/java/mixin_labs/java/bot目录，新建一个Config.java, 填写如下内容：
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
用你创建的APP的参数，替换文件中的内容: CLIENT_ID, client_id, CLIENT_SECRET, and the PIN, PIN_TOKEN, SESSION_ID.

创建App.java文件，内容如下：
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

          // 请求获取所有 pending 的消息
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
进入src/main/resources, 新建文件：rsa_private_key.txt, 填写私钥信息:
> rsa_private_key.txt
```java
-----BEGIN RSA PRIVATE KEY-----
...
-----END RSA PRIVATE KEY-----
```
本教程依赖 [mixin-java-sdk](https://github.com/wenewzhang/mixin-java-sdk)
现在回到项目目录，从github下载mixin-java-sdk

```bash
mkdir libs
cd libs
wget https://github.com/wenewzhang/mixin-java-sdk/releases/download/v2/mixin-java-sdk.jar
```
增加依赖到build.gradle.kts，增加编译文件compile(files("libs/mixin-java-sdk.jar")), 完整的依赖包如下：
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
    // Use JUnit test framework
    testImplementation("junit:junit:4.12")
}
```
进入到 src/test/java/mixin_labs/java/bot, 注释掉下面的代码
> AppTest.java
```java
        // assertNotNull("app should have a greeting", classUnderTest.getGreeting());
```
最后一步，回到项目目录mixin_labs-java-bot, 编译并运行.
```bash
gradle build
gradle run
```
如果你看到如下信息，表示已经连接成功了，机器人小程序已经就绪，你可以发信息给他了！

```bash
response:Response{protocol=http/1.1, code=101, message=Switching Protocols, url=https://blaze.mixin.one/}
[onMessage !!!]
json: {"id":"4ee01b68-817e-4f29-bcb4-b40f7c163f61","action":"LIST_PENDING_MESSAGES"}
LIST_PENDING_MESSAGES
```
![mixin_messenger](https://github.com/wenewzhang/mixin_labs-php-bot/blob/master/helloworld.jpeg)
信息如上图所示！

## 源代码解释
```java
MixinBot.connectToRemoteMixin(new WebSocketListener() {
@Override
public void onOpen(WebSocket webSocket, Response response) {
  MixinBot.sendListPendingMessages(webSocket);
}
```
连接到Mixin Network并发送"LISTPENDINGMESSAGES"消息，服务器以后会将收到的消息转发给此程序!
#### 消息处理回调函数
```java
        public void onMessage(WebSocket webSocket, ByteString bytes) {
          try {
            System.out.println("[onMessage !!!]");
            String msgIn = MixinUtil.bytesToJsonStr(bytes);

```
当服务器给机器人推送消息的时候，机器人的onMessage函数会被调用

#### 发送消息响应
```java
String messageId = obj.get("data").getAsJsonObject().get("message_id").getAsString();
MixinBot.sendMessageAck(webSocket, messageId);
```
机器人收到消息后需要发送响应给服务器，这样服务器就知道消息已经收到，不会再发一遍

#### 消息回应
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

好了，你的第一个机器人小程序已经运行起来了， 你有什么新的想法，来试试吧！

完整的代码 [这儿](https://github.com/wenewzhang/mixin_labs-java-bot/blob/master/src/main/java/mixin_labs/java/bot/App.java)
