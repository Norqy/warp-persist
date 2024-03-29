package com.wideplay.warp.hibernate;

import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.wideplay.warp.persist.TransactionStrategy;
import com.wideplay.warp.persist.PersistenceService;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public class HibernateBindingSupport {
    public static void addBindings(Binder binder) {

        binder.bind(SessionFactoryHolder.class).in(Singleton.class);

        binder.bind(SessionFactory.class).toProvider(SessionFactoryProvider.class);
        binder.bind(Session.class).toProvider(SessionProvider.class);

        binder.bind(PersistenceService.class).to(HibernatePersistenceService.class).in(Singleton.class);
    }

    public static MethodInterceptor getInterceptor(TransactionStrategy transactionStrategy) {
        switch (transactionStrategy) {
            case LOCAL:
                return new HibernateLocalTxnInterceptor();
            case JTA:
                return new HibernateJtaTxnInterceptor();
        }
        
        return null;
    }

    public static MethodInterceptor getFinderInterceptor() {
        return new HibernateFinderInterceptor();
    }
}
