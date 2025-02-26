/*
 * Copyright 2013-2025 Real Logic Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe.ir;

import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.InputStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.co.real_logic.sbe.Tests.getLocalResource;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

class SinceVersionValidationTest
{
    @Test
    void shouldErrorOnTypeWithGreaterSinceVersion() throws Exception
    {
        final ParserOptions options = ParserOptions.builder().suppressOutput(true).build();
        try (InputStream in = getLocalResource("error-handler-since-version.xml"))
        {
            parse(in, options);
        }
        catch (final IllegalStateException ex)
        {
            assertEquals("had 5 errors", ex.getMessage());
            return;
        }

        fail("expected IllegalStateException");
    }

    @Test
    void shouldApplySinceVersionOnComposites() throws Exception
    {
        try (InputStream in = getLocalResource("issue967.xml"))
        {
            final MessageSchema schema = parse(in, ParserOptions.DEFAULT);
            final IrGenerator irg = new IrGenerator();
            final Ir ir = irg.generate(schema);

            final List<Token> priceNull9Tokens = ir.getType("PRICENULL9");
            assertThat(priceNull9Tokens.get(0).version(), lessThan(13));
        }
    }
}
