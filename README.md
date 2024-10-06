# LLM JSON auto repair

A tiny library to repair JSON string output from LLM. It fixes most of the common issues from the LLM JSON output, eg:
* Remove the ```json``` code block
* Add missing commas
* Add missing double quotes
* Escape special characters \t \n

## Usage

```java
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