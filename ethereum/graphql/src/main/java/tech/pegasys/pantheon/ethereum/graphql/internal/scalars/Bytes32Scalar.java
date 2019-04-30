package tech.pegasys.pantheon.ethereum.graphql.internal.scalars;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import graphql.language.StringValue;

import java.util.function.Function;
import java.util.Optional;

import tech.pegasys.pantheon.util.bytes.Bytes32;;

public class Bytes32Scalar extends GraphQLScalarType {
	
	public Bytes32Scalar() {
		this("Bytes32", "A Bytes32 Scalar");
	}
	
	Bytes32Scalar(String name, String description) {
		super(name, description, new Coercing<Bytes32, String>() {
			@Override
            public String serialize(Object input) throws CoercingSerializeException {
				Optional<Bytes32> bytes32;
				if(input instanceof String)
					bytes32 = Optional.of(parseBytes32(input.toString(),CoercingSerializeException::new));
				else 
					bytes32 = toBytes32(input);
				if (bytes32.isPresent()) {
                    return bytes32.toString();
                }		
				throw new CoercingSerializeException(
                        "Expected a 'Bytes32' like object but was '" + input + "'."
                );
            }

            @Override
            public Bytes32 parseValue(Object input) throws CoercingParseValueException {
            	String bytes32Str;
                if (input instanceof String) {
                	bytes32Str = String.valueOf(input);
                } else {
                    Optional<Bytes32> bytes32 = toBytes32(input);
                    if (!bytes32.isPresent()) {
                        throw new CoercingParseValueException(
                                "Expected a 'Bytes32' like object but was '" + input + "'."
                        );
                    }
                    return bytes32.get();
                }
                return parseBytes32(bytes32Str, CoercingParseValueException::new);
            }

            @Override
            public Bytes32 parseLiteral(Object input) throws CoercingParseLiteralException {
            	if (!(input instanceof StringValue)) {
                    throw new CoercingParseLiteralException(
                            "Expected AST type 'StringValue' but was '" + input + "'."
                    );
                }
                return parseBytes32(((StringValue) input).getValue(), CoercingParseLiteralException::new);
            }
            
            private Bytes32 parseBytes32(String input, Function<String, RuntimeException> exceptionMaker) {
            	try {
            		return Bytes32.fromHexStringStrict(input);
            	} catch(IllegalArgumentException e) {
            		throw exceptionMaker.apply("Invalid Bytes32 value : '" + input + "'.");
            	}
            }
		});
		
	}
	
	private static Optional<Bytes32> toBytes32(Object input) {
        if (input instanceof Bytes32)
            return Optional.of((Bytes32) input);
        return Optional.empty();
    }
}
