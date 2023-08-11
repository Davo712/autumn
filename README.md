
# Autumn (Web and DB Lib For Java)

Supports only Java 17.


Please add -parameters flag in javac (if you use Intellij IDEA - Settings > Build, Execution, Deployment > Compiler > Java Compiler > Additional command line parameters).




## Usage/Examples

You can see all examples in the test package.

```java
public class Main {
    public static void main(String[] args) {
        DynamicWebApp.setParams(8080, "localhost"); // optional
        DynamicWebApp.run();
    }
}
```

```java
@Register
@EnableJWT(secretKey = "MySecretKey", timeoutHours = 7200)          // need for only one any class, this annotation is global
public class MainRegister {

    @EndPoint(mappingPath = "/login", type = "post")
    @NoJWT
    public Resp login(@RequiredParam String username, @RequiredParam String password) {
        if (true /* user exist**/) {
            Map map = new HashMap();
            map.put("username", username);
            map.put("password", password);
            return Resp.response("OK, your token is: " + AutumnJWT.createJWT(map));
        } else {
            return Resp.response("Bad request");
        }
    }

}
```

```java
 @EndPoint(mappingPath = "/paramWA")
    public Resp paramsWithoutAnnotation(String username) {
        System.out.println(username);
        return Resp.response("OK");
    }
```

```java
 @EndPoint(mappingPath = "/getJWTToken")
    public Resp getJWTToken(@GetTokenJWT String token) {
        System.out.println(AutumnJWT.getParamMap(token));
        System.out.println(AutumnJWT.getParam("username", token));
        return Resp.response("OK");
    }
```


```java
 @EndPoint(mappingPath = "/getJWTParams")
    public Resp getJWTParams(@GetParamJWT String username) {
        System.out.println(username);
        return Resp.response("OK");
    }
```


```java
  @EndPoint(mappingPath = "/redirect", redirectPath = "/login")
    public void redirect() {
    }
```
 
```java
 @EndPoint(mappingPath = "/getJWTToken")
    public Resp getJWTToken(@GetTokenJWT String token) {
        System.out.println(AutumnJWT.getParamMap(token));
        System.out.println(AutumnJWT.getParam("username", token));
        return Resp.response("OK");
    }
```



## Libraries

This project uses the following libraries:

- Vertx
- Lombok
- Reflections
- Jackson
- Java JWT
- JDBI


## Installation

Install autumn with jitpack repo

https://jitpack.io/#Davo712/autumn

```maven
        <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

        <dependency>
	    <groupId>com.github.Davo712</groupId>
	    <artifactId>autumn</artifactId>
	    <version>Version</version>
	</dependency>
```




## Support

For support, email gevorgyandavo77@gmail.com.
