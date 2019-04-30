package tech.pegasys.pantheon.ethereum.graphql.internal.scalars;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import graphql.language.StringValue;

import java.util.function.Function;
import java.util.Optional;

import com.google.common.primitives.UnsignedLong;

public class LongScalar extends GraphQLScalarType {
	
	public LongScalar() {
		this("LongScalar","A Long Scalar");
	}
	
	private LongScalar(String name, String description) {
	  super(name, description, new Coercing<UnsignedLong, String>() {
		  @Override
          public String serialize(Object input) throws CoercingSerializeException {
				Optional<UnsignedLong> unsignedLong;
				if(input instanceof String)
					unsignedLong = Optional.of(parseUnsignedLong(input.toString(),CoercingSerializeException::new));
				else 
					unsignedLong = toUnsignedLong(input);
				if (unsignedLong.isPresent()) {
                  return unsignedLong.toString();
              }		
				throw new CoercingSerializeException(
                      "Expected a 'UnsignedLong' like object but was '" + input + "'."
              );
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
                              "Expected a 'UnsignedLong' like object but was '" + input + "'."
                      );
                  }
                  return unsignedLong.get();
              }
              return parseUnsignedLong(unsignedLongStr, CoercingParseValueException::new);
          }

          @Override
          public UnsignedLong parseLiteral(Object input) throws CoercingParseLiteralException {
          	if (!(input instanceof StringValue)) {
                  throw new CoercingParseLiteralException(
                          "Expected AST type 'StringValue' but was '" + input + "'."
                  );
              }
              return parseUnsignedLong(((StringValue) input).getValue(), CoercingParseLiteralException::new);
          }
          
          private UnsignedLong parseUnsignedLong(String input, Function<String, RuntimeException> exceptionMaker) {
          	try {
          		return UnsignedLong.valueOf(input);
          	} catch(NumberFormatException e) {
          		throw exceptionMaker.apply("Invalid UnsignedLong value : '" + input + "'.");
          	}
          }
		});
		
	}
	
	private static Optional<UnsignedLong> toUnsignedLong(Object input) {
      if (input instanceof UnsignedLong)
          return Optional.of((UnsignedLong) input);
      return Optional.empty();
  }
}
