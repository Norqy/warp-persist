package com.wideplay.warp.hibernate;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Inject;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.wideplay.codemonkey.web.startup.Initializer;
import com.wideplay.warp.persist.*;

import java.util.Date;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public class ManagedLocalTransactionsTest {
    private Injector injector;
    private static final String UNIQUE_TEXT = "some unique text" + new Date();
    private static final String TRANSIENT_UNIQUE_TEXT = "some other unique text" + new Date();

    @BeforeClass
    public void pre() {
        injector = Guice.createInjector(PersistenceService.usingHibernate()
            .across(UnitOfWork.TRANSACTION)
            .transactedWith(TransactionStrategy.LOCAL)
            .forAll(Matchers.any())
            .buildModule(),
                new AbstractModule() {

                    protected void configure() {
                        bind(Configuration.class).toInstance(new AnnotationConfiguration()
                            .addAnnotatedClass(HibernateTestEntity.class)
                            .setProperties(Initializer.loadProperties("spt-persistence.properties")));
                    }
                }
        );

        //startup persistence
        injector.getInstance(PersistenceService.class)
                .start();
    }

    @AfterClass void post() {
        injector.getInstance(SessionFactory.class).close();
    }

    @Test
    public void testSimpleTransaction() {
        injector.getInstance(TransactionalObject.class).runOperationInTxn();

        Session session = injector.getInstance(Session.class);
        assert !session.getTransaction().isActive() : "Session was not closed by transactional service";

        //test that the data has been stored
        session.beginTransaction();
        Object result = session.createCriteria(HibernateTestEntity.class).add(Expression.eq("text", UNIQUE_TEXT)).uniqueResult();
        session.getTransaction().commit();

        assert result instanceof HibernateTestEntity : "odd result returned fatal";

        assert UNIQUE_TEXT.equals(((HibernateTestEntity)result).getText()) : "queried entity did not match--did automatic txn fail?";
    }

    @Test
    public void testSimpleTransactionRollbackOnChecked() {
        try {
            injector.getInstance(TransactionalObject.class).runOperationInTxnThrowingChecked();
        } catch(IOException e) {
            //ignore
        }

        Session session = injector.getInstance(Session.class);
        assert !session.getTransaction().isActive() : "Session was not closed by transactional service (rollback didnt happen?)";

        //test that the data has been stored
        session.beginTransaction();
        Object result = session.createCriteria(HibernateTestEntity.class).add(Expression.eq("text", TRANSIENT_UNIQUE_TEXT)).uniqueResult();
        session.getTransaction().commit();

        assert null == result : "a result was returned! rollback sure didnt happen!!!";
    }

    @Test
    public void testSimpleTransactionRollbackOnUnchecked() {
        try {
            injector.getInstance(TransactionalObject.class).runOperationInTxnThrowingUnchecked();
        } catch(RuntimeException re) {
            //ignore
        }

        Session session = injector.getInstance(Session.class);
        assert !session.getTransaction().isActive() : "Session was not closed by transactional service (rollback didnt happen?)";

        //test that the data has been stored
        session.beginTransaction();
        Object result = session.createCriteria(HibernateTestEntity.class).add(Expression.eq("text", TRANSIENT_UNIQUE_TEXT)).uniqueResult();
        session.getTransaction().commit();

        assert null == result : "a result was returned! rollback sure didnt happen!!!";
    }


    public static class TransactionalObject {
        @Inject Session session;

        @Transactional
        public void runOperationInTxn() {
            HibernateTestEntity entity = new HibernateTestEntity();
            entity.setText(UNIQUE_TEXT);
            session.persist(entity);
        }

        @Transactional(rollbackOn = IOException.class)
        public void runOperationInTxnThrowingChecked() throws IOException {
            HibernateTestEntity entity = new HibernateTestEntity();
            entity.setText(TRANSIENT_UNIQUE_TEXT);
            session.persist(entity);
            
            throw new IOException();
        }
        
        @Transactional
        public void runOperationInTxnThrowingUnchecked() {
            HibernateTestEntity entity = new HibernateTestEntity();
            entity.setText(TRANSIENT_UNIQUE_TEXT);
            session.persist(entity);

            throw new IllegalStateException();
        }
    }
}
