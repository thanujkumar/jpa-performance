package jpa.config.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class LocalInitialContextFactory implements InitialContextFactory {

	//Ensure this is static
	private static  LocalJndiContext ctx;
	
	@Override
	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
		synchronized (this) {
			if (ctx == null) {
				ctx = new LocalJndiContext();
			}
		}
		return ctx;
	}

}
