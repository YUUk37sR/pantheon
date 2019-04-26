package tech.pegasys.pantheon.ethereum.graphql.internal.scalars;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

import tech.pegasys.pantheon.ethereum.core.Address;

public class AddressScalar extends GraphQLScalarType {
	
	public AddressScalar() {
		this("Address", "An Address Scalar");
	}
	
	AddressScalar(String name, String description) {
		super(name, description, new Coercing<Object, Object>() {
			@Override
            public Object serialize(Object input) throws CoercingSerializeException {
				if(input instanceof Address)
					return input.toString();
				else
				
            }

            @Override
            public Object parseValue(Object input) throws CoercingParseValueException {
                return input;
            }

            @Override
            public Object parseLiteral(Object input) throws CoercingParseLiteralException {
                return parseLiteral(input, Collections.emptyMap());
            }
		});
		
	}
}
