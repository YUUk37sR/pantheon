/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.graphql.internal.scalars;

import java.util.Optional;
import java.util.function.Function;

import com.google.common.primitives.UnsignedLong;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

public class LongScalar extends GraphQLScalarType {

  public LongScalar() {
    this("LongScalar", "A Long Scalar");
  }

  private LongScalar(String name, String description) {
    super(
        name,
        description,
        new Coercing<UnsignedLong, String>() {
          @Override
          public String serialize(Object input) throws CoercingSerializeException {
            Optional<UnsignedLong> unsignedLong;
            if (input instanceof String)
              unsignedLong =
                  Optional.of(parseUnsignedLong(input.toString(), CoercingSerializeException::new));
            else unsignedLong = toUnsignedLong(input);
            if (unsignedLong.isPresent()) {
              return unsignedLong.toString();
            }
            throw new CoercingSerializeException(
                "Expected a 'UnsignedLong' like object but was '" + input + "'.");
          }

          @Override
          public UnsignedLong parseValue(Object input) throws CoercingParseValueException {
            String unsignedLongStr;
            if (input instanceof String) {
              unsignedLongStr = String.valueOf(input);
            } else {
              Optional<UnsignedLong> unsignedLong = toUnsignedLong(input);
              if (!unsignedLong.isPresent()) {
                throw new CoercingParseValueException(
                    "Expected a 'UnsignedLong' like object but was '" + input + "'.");
              }
              return unsignedLong.get();
            }
            return parseUnsignedLong(unsignedLongStr, CoercingParseValueException::new);
          }

          @Override
          public UnsignedLong parseLiteral(Object input) throws CoercingParseLiteralException {
            if (!(input instanceof StringValue)) {
              throw new CoercingParseLiteralException(
                  "Expected AST type 'StringValue' but was '" + input + "'.");
            }
            return parseUnsignedLong(
                ((StringValue) input).getValue(), CoercingParseLiteralException::new);
          }

          private UnsignedLong parseUnsignedLong(
              String input, Function<String, RuntimeException> exceptionMaker) {
            try {
              return UnsignedLong.valueOf(input);
            } catch (NumberFormatException e) {
              throw exceptionMaker.apply("Invalid UnsignedLong value : '" + input + "'.");
            }
          }
        });
  }

  private static Optional<UnsignedLong> toUnsignedLong(Object input) {
    if (input instanceof UnsignedLong) return Optional.of((UnsignedLong) input);
    return Optional.empty();
  }
}
