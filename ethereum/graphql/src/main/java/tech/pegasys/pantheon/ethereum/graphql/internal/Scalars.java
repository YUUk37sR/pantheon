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
package tech.pegasys.pantheon.ethereum.graphql.internal;

import tech.pegasys.pantheon.ethereum.core.Address;
import tech.pegasys.pantheon.util.bytes.Bytes32;
import tech.pegasys.pantheon.util.bytes.BytesValue;

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.primitives.UnsignedLong;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Scalars {
  private static final Logger LOG = LogManager.getLogger();
  public static GraphQLScalarType AddressScalar =
      new GraphQLScalarType(
          "Address",
          "An Address Scalar",
          new Coercing<Address, String>() {
            @Override
            public String serialize(final Object input) throws CoercingSerializeException {
              Optional<Address> address;
              if (input instanceof String)
                address =
                    Optional.of(parseAddress(input.toString(), CoercingSerializeException::new));
              else address = toAddress(input);

              if (address.isPresent()) {
                return address.get().toString();
              }
              throw new CoercingSerializeException(
                  "Expected a 'Address' like object but was '" + input + "'.");
            }

            @Override
            public Address parseValue(final Object input) throws CoercingParseValueException {
              String addressStr;
              if (input instanceof String) {
                addressStr = String.valueOf(input);
              } else {
                Optional<Address> address = toAddress(input);
                if (!address.isPresent()) {
                  throw new CoercingParseValueException(
                      "Expected a 'Address' like object but was '" + input + "'.");
                }
                return address.get();
              }
              return parseAddress(addressStr, CoercingParseValueException::new);
            }

            @Override
            public Address parseLiteral(final Object input) throws CoercingParseLiteralException {
              if (!(input instanceof StringValue)) {
                throw new CoercingParseLiteralException(
                    "Expected AST type 'StringValue' but was '" + input + "'.");
              }
              return parseAddress(
                  ((StringValue) input).getValue(), CoercingParseLiteralException::new);
            }

            private Address parseAddress(
                final String input, final Function<String, RuntimeException> exceptionMaker) {
              Address address;
              try {
                address = Address.fromHexString(input);
              } catch (IllegalArgumentException e) {
                throw exceptionMaker.apply("Invalid Address value : '" + input + "'.");
              }
              return address;
            }
          });
  public static GraphQLScalarType BytesScalar =
      new GraphQLScalarType(
          "Bytes",
          "A Bytes Scalar",
          new Coercing<BytesValue, String>() {
            @Override
            public String serialize(final Object input) throws CoercingSerializeException {
              Optional<BytesValue> bytes;
              if (input instanceof String)
                bytes = Optional.of(parseBytes(input.toString(), CoercingSerializeException::new));
              else bytes = toBytes(input);
              if (bytes.isPresent()) {
                return bytes.get().toString();
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
            public BytesValue parseLiteral(final Object input)
                throws CoercingParseLiteralException {
              if (!(input instanceof StringValue)) {
                throw new CoercingParseLiteralException(
                    "Expected AST type 'StringValue' but was '" + input + "'.");
              }
              return parseBytes(
                  ((StringValue) input).getValue(), CoercingParseLiteralException::new);
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
  public static GraphQLScalarType Bytes32Scalar =
      new GraphQLScalarType(
          "Bytes32",
          "A Bytes32 Scalar",
          new Coercing<Bytes32, String>() {
            @Override
            public String serialize(final Object input) throws CoercingSerializeException {
              Optional<Bytes32> bytes32;
              if (input instanceof String)
                bytes32 =
                    Optional.of(parseBytes32(input.toString(), CoercingSerializeException::new));
              else bytes32 = toBytes32(input);
              if (bytes32.isPresent()) {
                return bytes32.get().toString();
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
  public static GraphQLScalarType BigIntScalar =
      new GraphQLScalarType(
          "BigInt",
          "A BigInteger Scalar",
          new Coercing<BigInteger, String>() {
            @Override
            public String serialize(final Object input) throws CoercingSerializeException {
              Optional<BigInteger> bigInt;
              if (input instanceof String)
                bigInt =
                    Optional.of(parseBigInteger(input.toString(), CoercingSerializeException::new));
              else bigInt = toBigInteger(input);
              if (bigInt.isPresent()) {
                return bigInt.get().toString();
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
            public BigInteger parseLiteral(final Object input)
                throws CoercingParseLiteralException {
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
  public static GraphQLScalarType LongScalar =
      new GraphQLScalarType(
          "Long",
          "A Long Scalar",
          new Coercing<UnsignedLong, String>() {
            @Override
            public String serialize(final Object input) throws CoercingSerializeException {
              Optional<UnsignedLong> unsignedLong;
              if (input instanceof String)
                unsignedLong =
                    Optional.of(
                        parseUnsignedLong(input.toString(), CoercingSerializeException::new));
              else unsignedLong = toUnsignedLong(input);
              if (unsignedLong.isPresent()) {
                return unsignedLong.get().toString();
              }
              throw new CoercingSerializeException(
                  "Expected a 'UnsignedLong' like object but was '" + input + "'.");
            }

            @Override
            public UnsignedLong parseValue(final Object input) throws CoercingParseValueException {
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
            public UnsignedLong parseLiteral(final Object input)
                throws CoercingParseLiteralException {
              String literal = null;
              LOG.info("UnsignedLong parseLiteral" + input.toString());
              if (input instanceof StringValue) {
                literal = ((StringValue) input).getValue();
              } else if (input instanceof IntValue) {
                literal = ((IntValue) input).getValue().toString();
              } else {
                throw new CoercingParseLiteralException(
                    "Expected AST type 'Int Value or StringValue' but was '" + input + "'.");
              }

              return parseUnsignedLong(literal, CoercingParseLiteralException::new);
            }

            private UnsignedLong parseUnsignedLong(
                final String input, final Function<String, RuntimeException> exceptionMaker) {
              LOG.info("UnsignedLong parseUnsignedLong" + input);
              try {
                return UnsignedLong.valueOf(input);
              } catch (NumberFormatException e) {
                throw exceptionMaker.apply("Invalid UnsignedLong value : '" + input + "'.");
              }
            }
          });

  private static Optional<Address> toAddress(final Object input) {
    if (input instanceof Address) return Optional.of((Address) input);
    return Optional.empty();
  }

  private static Optional<BigInteger> toBigInteger(final Object input) {
    if (input instanceof BigInteger) return Optional.of((BigInteger) input);
    return Optional.empty();
  }

  private static Optional<BytesValue> toBytes(final Object input) {
    if (input instanceof BytesValue) return Optional.of((BytesValue) input);
    return Optional.empty();
  }

  private static Optional<Bytes32> toBytes32(final Object input) {
    if (input instanceof Bytes32) return Optional.of((Bytes32) input);
    return Optional.empty();
  }

  private static Optional<UnsignedLong> toUnsignedLong(final Object input) {
    if (input instanceof UnsignedLong) return Optional.of((UnsignedLong) input);
    return Optional.empty();
  }
}
