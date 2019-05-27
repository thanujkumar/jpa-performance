package jpa.config.jndi;

import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

public class LocalNameParser implements NameParser {

	@Override
	public Name parse(String name) throws NamingException {

		return new LocalName(name);
	}

	class LocalName extends CompositeName {

		public LocalName(String name) throws InvalidNameException {
			super(name);
		}
		
	}
}
