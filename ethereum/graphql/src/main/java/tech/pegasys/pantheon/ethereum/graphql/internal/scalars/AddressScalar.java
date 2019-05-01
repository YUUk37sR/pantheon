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

import tech.pegasys.pantheon.ethereum.core.Address;

import java.util.Optional;
import java.util.function.Function;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

public class AddressScalar extends GraphQLScalarType {

  public AddressScalar() {
    this("Address", "An Address Scalar");
  }

  AddressScalar(String name, String description) {
    super(
        name,
        description,
        new Coercing<Address, String>() {
          @Override
          public String serialize(Object input) throws CoercingSerializeException {
            Optional<Address> address;
            if (input instanceof String)
              address =
                  Optional.of(parseAddress(input.toString(), CoercingSerializeException::new));
            else address = toAddress(input);

            if (address.isPresent()) {
              return input.toString();
            }
            throw new CoercingSerializeException(
                "Expected a 'Address' like object but was '" + input + "'.");
          }

          @Override
          public Address parseValue(Object input) throws CoercingParseValueException {
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
          public Address parseLiteral(Object input) throws CoercingParseLiteralException {
            if (!(input instanceof StringValue)) {
              throw new CoercingParseLiteralException(
                  "Expected AST type 'StringValue' but was '" + input + "'.");
            }
            return parseAddress(
                ((StringValue) input).getValue(), CoercingParseLiteralException::new);
          }

          private Address parseAddress(
              String input, Function<String, RuntimeException> exceptionMaker) {
            Address address;
            try {
              address = Address.fromHexString(input);
            } catch (IllegalArgumentException e) {
              throw exceptionMaker.apply("Invalid Address value : '" + input + "'.");
            }
            return address;
          }
        });
  }

  private static Optional<Address> toAddress(Object input) {
    if (input instanceof Address) return Optional.of((Address) input);
    return Optional.empty();
  }
}
