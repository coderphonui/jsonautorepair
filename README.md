# LLM JSON auto repair

![Build](https://github.com/coderphonui/jsonautorepair/actions/workflows/maven.yml/badge.svg)

![Coverage](badges/jacoco.svg)


A tiny library to repair JSON string output from LLM. It fixes most of the common issues from the LLM JSON output, eg:

* Remove the ```json``` code block
* Add missing commas
* Add missing double quotes when possible
* Replace single quotes with double quotes
* Escape special characters \t \n
* and many other common issues

## Usage

## Add dependency
    
```xml
    <dependency>
        <groupId>com.github.coderphonui</groupId>
        <artifactId>jsonautorepair</artifactId>
        <version>1.0.0</version>
    </dependency>
```

Your Java code

```java

import com.cdpn.jsonautofix.JSONAutoFixer;

JSONAutoFixer jsonAutoRepairer = new JSONAutoFixer();
String originalJSON = """
                ```json
                {
                    "name": "Alice",
                    "sex": "female"
                    "address": "123 Andrew Street,
                    ward 3, district 10"
                }
                ```
                """;
String fixedJSON = jsonAutoRepairer.repair(originalJSON);
```

It will automatically fix the JSON string and return the fixed JSON string if possible. In case the JSON string cannot be fixed, it returns null
