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

import tech.pegasys.pantheon.util.bytes.BytesValue;

import java.util.Optional;
import java.util.function.Function;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

public class BytesScalar extends GraphQLScalarType {

  public BytesScalar() {
    this("Bytes", "A Bytes Scalar");
  }

  BytesScalar(final String name, final String description) {
    super(
        name,
        description,
        new Coercing<BytesValue, String>() {
          @Override
          public String serialize(final Object input) throws CoercingSerializeException {
            Optional<BytesValue> bytes;
            if (input instanceof String)
              bytes = Optional.of(parseBytes(input.toString(), CoercingSerializeException::new));
            else bytes = toBytes(input);
            if (bytes.isPresent()) {
              return bytes.toString();
            }
            throw new CoercingSerializeException(
                "Expected a 'Bytes' like object but was '" + input + "'.");
          }

          @Override
          public BytesValue parseValue(final Object input) throws CoercingParseValueException {
            String bytesStr;
            if (input instanceof String) {
              bytesStr = String.valueOf(input);
            } else {
              Optional<BytesValue> bytes = toBytes(input);
              if (!bytes.isPresent()) {
                throw new CoercingParseValueException(
                    "Expected a 'Bytes' like object but was '" + input + "'.");
              }
              return bytes.get();
            }
            return parseBytes(bytesStr, CoercingParseValueException::new);
          }

          @Override
          public BytesValue parseLiteral(final Object input) throws CoercingParseLiteralException {
            if (!(input instanceof StringValue)) {
              throw new CoercingParseLiteralException(
                  "Expected AST type 'StringValue' but was '" + input + "'.");
            }
            return parseBytes(((StringValue) input).getValue(), CoercingParseLiteralException::new);
          }

          private BytesValue parseBytes(
              final String input, final Function<String, RuntimeException> exceptionMaker) {
            try {
              return BytesValue.fromHexString(input);
            } catch (IllegalArgumentException e) {
              throw exceptionMaker.apply("Invalid Bytes value : '" + input + "'.");
            }
          }
        });
  }

  private static Optional<BytesValue> toBytes(final Object input) {
    if (input instanceof BytesValue) return Optional.of((BytesValue) input);
    return Optional.empty();
  }
}
