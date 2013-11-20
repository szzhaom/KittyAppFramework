package kitty.kaf.pools.jndi.lookupers;

import javax.naming.Context;
import javax.naming.NamingException;

import kitty.kaf.pools.jndi.Lookuper;

public class Jboss7JndiLookuper extends Lookuper {

	@SuppressWarnings("unchecked")
	@Override
	public <E> E ejbLookup(Context context, String name, Class<E> clazz)
			throws NamingException {
		return (E) context.lookup("java:app/kaf-testapp-ejb/" + name);
	}

}
