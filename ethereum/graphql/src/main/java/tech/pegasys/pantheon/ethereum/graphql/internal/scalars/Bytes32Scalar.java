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

import tech.pegasys.pantheon.util.bytes.Bytes32;

import java.util.Optional;
import java.util.function.Function;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

public class Bytes32Scalar extends GraphQLScalarType {

  public Bytes32Scalar() {
    this("Bytes32", "A Bytes32 Scalar");
  }

  Bytes32Scalar(final String name, final String description) {
    super(
        name,
        description,
        new Coercing<Bytes32, String>() {
          @Override
          public String serialize(final Object input) throws CoercingSerializeException {
            Optional<Bytes32> bytes32;
            if (input instanceof String)
              bytes32 =
                  Optional.of(parseBytes32(input.toString(), CoercingSerializeException::new));
            else bytes32 = toBytes32(input);
            if (bytes32.isPresent()) {
              return bytes32.toString();
            }
            throw new CoercingSerializeException(
                "Expected a 'Bytes32' like object but was '" + input + "'.");
          }

          @Override
          public Bytes32 parseValue(final Object input) throws CoercingParseValueException {
            String bytes32Str;
            if (input instanceof String) {
              bytes32Str = String.valueOf(input);
            } else {
              Optional<Bytes32> bytes32 = toBytes32(input);
              if (!bytes32.isPresent()) {
                throw new CoercingParseValueException(
                    "Expected a 'Bytes32' like object but was '" + input + "'.");
              }
              return bytes32.get();
            }
            return parseBytes32(bytes32Str, CoercingParseValueException::new);
          }

          @Override
          public Bytes32 parseLiteral(final Object input) throws CoercingParseLiteralException {
            if (!(input instanceof StringValue)) {
              throw new CoercingParseLiteralException(
                  "Expected AST type 'StringValue' but was '" + input + "'.");
            }
            return parseBytes32(
                ((StringValue) input).getValue(), CoercingParseLiteralException::new);
          }

          private Bytes32 parseBytes32(
              final String input, final Function<String, RuntimeException> exceptionMaker) {
            try {
              return Bytes32.fromHexStringStrict(input);
            } catch (IllegalArgumentException e) {
              throw exceptionMaker.apply("Invalid Bytes32 value : '" + input + "'.");
            }
          }
        });
  }

  private static Optional<Bytes32> toBytes32(final Object input) {
    if (input instanceof Bytes32) return Optional.of((Bytes32) input);
    return Optional.empty();
  }
}
