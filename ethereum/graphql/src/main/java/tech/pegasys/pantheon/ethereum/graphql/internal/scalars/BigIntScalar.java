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

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Function;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

public class BigIntScalar extends GraphQLScalarType {

  public BigIntScalar() {
    this("BigInt", "A BigInteger Scalar");
  }

  BigIntScalar(final String name, final String description) {
    super(
        name,
        description,
        new Coercing<BigInteger, BigInteger>() {
          @Override
          public BigInteger serialize(final Object input) throws CoercingSerializeException {
            Optional<BigInteger> bigInt;
            if (input instanceof String)
              bigInt =
                  Optional.of(parseBigInteger(input.toString(), CoercingSerializeException::new));
            else bigInt = toBigInteger(input);
            if (bigInt.isPresent()) {
              return bigInt.get();
            }
            throw new CoercingSerializeException(
                "Expected a 'BigInteger' like object but was '" + input + "'.");
          }

          @Override
          public BigInteger parseValue(final Object input) throws CoercingParseValueException {
            String bigIntStr;
            if (input instanceof String) {
              bigIntStr = String.valueOf(input);
            } else {
              Optional<BigInteger> bigInt = toBigInteger(input);
              if (!bigInt.isPresent()) {
                throw new CoercingParseValueException(
                    "Expected a 'BigInteger' like object but was '" + input + "'.");
              }
              return bigInt.get();
            }
            return parseBigInteger(bigIntStr, CoercingParseValueException::new);
          }

          @Override
          public BigInteger parseLiteral(final Object input) throws CoercingParseLiteralException {
            if (!(input instanceof StringValue)) {
              throw new CoercingParseLiteralException(
                  "Expected AST type 'StringValue' but was '" + input + "'.");
            }
            return parseBigInteger(
                ((StringValue) input).getValue(), CoercingParseLiteralException::new);
          }

          private BigInteger parseBigInteger(
              final String input, final Function<String, RuntimeException> exceptionMaker) {
            try {
              BigInteger bigInt = new BigInteger(input);
              return bigInt;
            } catch (NumberFormatException e) {
              throw exceptionMaker.apply("Invalid BigInteger value : '" + input + "'.");
            }
          }
        });
  }

  private static Optional<BigInteger> toBigInteger(final Object input) {
    if (input instanceof BigInteger) return Optional.of((BigInteger) input);
    return Optional.empty();
  }
}
