package com.cdpn.jsonautorepair;


import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JSONAutoRepairerTest {
    private JSONAutoRepairer jsonAutoRepairer;
    @BeforeEach
    public void setup() {
        jsonAutoRepairer = new JSONAutoRepairer();
    }
    @Test
    public void repair_should_return_null_string_when_the_string_is_empty() {
        assertNull(jsonAutoRepairer.repair(""));
    }

    @Test
    public void repair_should_cleanup_markdown_code_block_when_string_contain_json_code_block() {
        String originalJSON = """
                ```json
                {
                    "name": "Alice",
                    "age": 30
                }
                ```
                """;
        assertEquals(JsonParser.parseString("""
                {
                    "name": "Alice",
                    "age": 30
                }
                """).toString(), jsonAutoRepairer.repair(originalJSON));

    }

    @Test
    public void repair_should_return_original_string_when_the_string_is_a_valid_JSON() {
        String originalJSON = """
                {
                    "name": "Alice",
                    "age": 30
                }
                """;
        assertEquals(JsonParser.parseString(originalJSON.trim()).toString(), jsonAutoRepairer.repair(originalJSON));
        assertEquals("[]", jsonAutoRepairer.repair("[]"));
        assertEquals("[\"sea\",\"fish\"]", jsonAutoRepairer.repair("[\"sea\",\"fish\"]"));
        assertEquals("[\"sea\",\"Alice's fish\"]", jsonAutoRepairer.repair("[\"sea\",\"Alice's fish\"]"));
    }



    @Test
    public void repair_should_wrap_string_in_brackets_when_the_string_is_not_a_JSON_array() {
        String originalJSON = """
                   "name": "Alice",
                   "age": 30
               """;
        assertEquals(JsonParser.parseString("{" + originalJSON.trim() + "}").toString(),
                jsonAutoRepairer.repair(originalJSON));
    }

    @Test
    public void repair_should_not_wrap_string_in_brackets_when_the_string_is_a_JSON_array() {
        assertEquals("[\"sea\",\"fish\"]", jsonAutoRepairer.repair("[\"sea\",\"fish\"]"));
    }

    @Test
    public void repair_should_add_quotes_around_unquoted_keys() {
        assertEquals("{\"name\":\"Alice\",\"age\":30}",
                jsonAutoRepairer.repair("{ name: \"Alice\",age: 30 }"));
        assertEquals("{\"name\":\"Alice\",\"age\":30}",
                jsonAutoRepairer.repair("{name: \"Alice\", age: 30 }"));
    }

    @Test
    public void repair_should_escape_internal_quote_for_both_key_and_value() {
        String originalJSON = """
                {
                    ""sentence"": "Alice said: "hello"",
                    "sentiment": "normal"
                }
                """;

        assertEquals(JsonParser.parseString("""
                {
                    "\\"sentence\\"": "Alice said: \\"hello\\"",
                    "sentiment": "normal"
                }
                """).toString(),
                jsonAutoRepairer.repair(originalJSON));
    }

    @Test
    public void repair_should_escape_special_characters() {
        String originalJSON = "{ \"address\": \"123 Andrew Street,\n \tward 3, district 4,\n \t\tABC city\" }";
        assertEquals("{\"address\":\"123 Andrew Street,\\n \\tward 3, district 4,\\n \\t\\tABC city\"}",
                jsonAutoRepairer.repair(originalJSON));
    }


    @Test
    public void repair_should_escape_already_escaped_character() {
        String originalJSON ="{\"address\":\"123 Andrew Street,\\n \\tward 3\"}";
        assertEquals("{\"address\":\"123 Andrew Street,\\n \\tward 3\"}",
                jsonAutoRepairer.repair(originalJSON));
    }

    @Test
    public void repair_should_auto_add_comma_before_a_new_start_quote_to_fix_missing_comma_case() {
        String originalJSON = """
                {
                    "name": "Alice",
                    "age": 30
                    "address": "123 Andrew Street,\\n \\tward 3"
                }
                """;
        assertEquals(JsonParser.parseString("""
                {
                    "name": "Alice",
                    "age": 30,
                    "address": "123 Andrew Street,\\n \\tward 3"
                }
                """).toString() , jsonAutoRepairer.repair(originalJSON));

    }

    @Test
    public void repair_should_return_null_when_cannot_fix_the_JSON() {
        assertNull(jsonAutoRepairer.repair("This is not a valid {} JSON"));
    }

}
