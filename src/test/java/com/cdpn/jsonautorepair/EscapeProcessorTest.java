package com.cdpn.jsonautorepair;

import com.cdpn.jsonautorepair.internal.EscapeProcessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EscapeProcessorTest {
    @Test
    public void process_should_escape_internal_quotes() {
      EscapeProcessor processor = new EscapeProcessor("{\"\"sentence\"\": \"Alice said: \"Please take him out\"\"}");
      assertEquals("{\"\\\"sentence\\\"\": \"Alice said: \\\"Please take him out\\\"\"}",
                processor.process());
    }

    @Test
    public void process_should_escape_internal_quotes_when_there_is_special_characters_in_string() {
        EscapeProcessor processor = new EscapeProcessor("{\"\"sentence\"\": \"Alice said: \"Please take him out\" \t\n\"}");
        assertEquals("{\"\\\"sentence\\\"\": \"Alice said: \\\"Please take him out\\\" \\t\\n\"}",
                processor.process());
        System.out.println(processor.process());
    }


    @Test
    public void process_should_escape_internal_quotes_when_string_in_an_array() {
        EscapeProcessor processor = new EscapeProcessor("[ \"banana\",\"cake\",\"A special \"food\" for you\" ]");
        assertEquals("[ \"banana\",\"cake\",\"A special \\\"food\\\" for you\" ]",
                processor.process());
    }


    @Test
    public void process_should_return_same_string_when_there_is_no_internal_quote() {
        EscapeProcessor processor = new EscapeProcessor("\"key\": \"value\"");
        assertEquals("\"key\": \"value\"",
                processor.process());
    }

    @Test
    public void process_should_escape_already_escaped_character() {
        String originalJSON ="{\"address\":\"123 Andrew Street,\\n \\tward 3\"}";
        EscapeProcessor processor = new EscapeProcessor(originalJSON);
        assertEquals("{\"address\":\"123 Andrew Street,\\n \\tward 3\"}",
                processor.process());
    }

}
