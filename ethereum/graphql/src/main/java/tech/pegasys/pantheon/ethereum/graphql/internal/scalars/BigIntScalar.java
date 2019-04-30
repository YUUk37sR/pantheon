package tech.pegasys.pantheon.ethereum.graphql.internal.scalars;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

import java.util.function.Function;
import java.util.Optional;
import java.math.BigInteger;

import graphql.language.StringValue;


public class BigIntScalar extends GraphQLScalarType {
	
	public BigIntScalar() {
		this("BigInteger", "A BigInteger Scalar");
	}
	
	BigIntScalar(String name, String description) {
		super(name, description, new Coercing<BigInteger, BigInteger>() {
			@Override
            public BigInteger serialize(Object input) throws CoercingSerializeException {
				Optional<BigInteger> bigInt;
				if(input instanceof String)
					bigInt = Optional.of(parseBigInteger(input.toString(),CoercingSerializeException::new));
				else 
					bigInt = toBigInteger(input);
				if (bigInt.isPresent()) {
                    return bigInt.get();
                }		
				throw new CoercingSerializeException(
                        "Expected a 'BigInteger' like object but was '" + input + "'."
                );
            }

            @Override
            public BigInteger parseValue(Object input) throws CoercingParseValueException {
            	String bigIntStr;
                if (input instanceof String) {
                	bigIntStr = String.valueOf(input);
                } else {
                    Optional<BigInteger> bigInt = toBigInteger(input);
                    if (!bigInt.isPresent()) {
                        throw new CoercingParseValueException(
                                "Expected a 'BigInteger' like object but was '" + input + "'."
                        );
                    }
                    return bigInt.get();
                }
                return parseBigInteger(bigIntStr, CoercingParseValueException::new);
            }

            @Override
            public BigInteger parseLiteral(Object input) throws CoercingParseLiteralException {
            	if (!(input instanceof StringValue)) {
                    throw new CoercingParseLiteralException(
                            "Expected AST type 'StringValue' but was '" + input + "'."
                    );
                }
                return parseBigInteger(((StringValue) input).getValue(), CoercingParseLiteralException::new);
            }
            
            private BigInteger parseBigInteger(String input, Function<String, RuntimeException> exceptionMaker) {
            	try {
            		BigInteger bigInt = new BigInteger(input);
                    return bigInt;
            	} catch(NumberFormatException e) {
            		throw exceptionMaker.apply("Invalid BigInteger value : '" + input + "'.");
            	}
            }
		});
		
	}
	
	private static Optional<BigInteger> toBigInteger(Object input) {
        if (input instanceof BigInteger)
            return Optional.of((BigInteger) input);
        return Optional.empty();
    }
}