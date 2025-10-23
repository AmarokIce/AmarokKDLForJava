# Amarok KDL for Java

Amarok KDL for Java is a KDL parsing library written in Java. 

[Come and learn about KDL](https://kdl.dev/)

## Quick Start

```java
public static void myMethod() {
  final KDoc doc = KDL.parse(new File("./MyKDLFile.kdl"));
  
  final KArray array = doc.get(0, 0).asArrayOrEmpty();
  final Map<String, List<KNode<?>>> mapByDoc = doc.get(0, 1).asDocOrEmpty();
}
```

## Usage
Maven Central:

```groovy
repositories {
	mavenCentral()
}

dependencies {
    implementation "club.snowlyicewolf:amarok-kdl-for-java:1.0.1"
}
```