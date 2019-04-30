package tech.pegasys.pantheon.ethereum.graphql.internal.scalars;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import graphql.language.StringValue;

import java.util.function.Function;
import java.util.Optional;

import tech.pegasys.pantheon.util.bytes.BytesValue;

public class BytesScalar extends GraphQLScalarType {
	
	public BytesScalar() {
		this("Bytes", "A Bytes Scalar");
	}
	
	BytesScalar(String name, String description) {
		super(name, description, new Coercing<BytesValue, String>() {
			@Override
            public String serialize(Object input) throws CoercingSerializeException {
				Optional<BytesValue> bytes;
				if(input instanceof String)
					bytes = Optional.of(parseBytes(input.toString(),CoercingSerializeException::new));
				else 
					bytes = toBytes(input);
				if (bytes.isPresent()) {
                    return bytes.toString();
                }		
				throw new CoercingSerializeException(
                        "Expected a 'Bytes' like object but was '" + input + "'."
                );
            }

            @Override
            public BytesValue parseValue(Object input) throws CoercingParseValueException {
            	String bytesStr;
                if (input instanceof String) {
                	bytesStr = String.valueOf(input);
                } else {
                    Optional<BytesValue> bytes = toBytes(input);
                    if (!bytes.isPresent()) {
                        throw new CoercingParseValueException(
                                "Expected a 'Bytes' like object but was '" + input + "'."
                        );
                    }
                    return bytes.get();
                }
                return parseBytes(bytesStr, CoercingParseValueException::new);
            }

            @Override
            public BytesValue parseLiteral(Object input) throws CoercingParseLiteralException {
            	if (!(input instanceof StringValue)) {
                    throw new CoercingParseLiteralException(
                            "Expected AST type 'StringValue' but was '" + input + "'."
                    );
                }
                return parseBytes(((StringValue) input).getValue(), CoercingParseLiteralException::new);
            }
            
            private BytesValue parseBytes(String input, Function<String, RuntimeException> exceptionMaker) {
            	try {
            		return BytesValue.fromHexString(input);
            	} catch(IllegalArgumentException e) {
            		throw exceptionMaker.apply("Invalid Bytes value : '" + input + "'.");
            	}
            }
		});
		
	}
	
	private static Optional<BytesValue> toBytes(Object input) {
        if (input instanceof BytesValue)
            return Optional.of((BytesValue) input);
        return Optional.empty();
    }
}
