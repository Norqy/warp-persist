package com.wideplay.warp.jpa;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.UnitOfWork;
import com.wideplay.warp.persist.TransactionStrategy;
import com.wideplay.warp.hibernate.HibernateTestEntity;
import com.wideplay.codemonkey.web.startup.Initializer;
import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * Created with IntelliJ IDEA.
 * On: 2/06/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
@Test(suiteName = "jpa")
public class EntityManagerFactoryDuplicationAwareTest {
    private Injector injector;

    @BeforeTest
    public void pre() {
        injector = Guice.createInjector(PersistenceService.usingJpa()
            .across(UnitOfWork.TRANSACTION)
            .transactedWith(TransactionStrategy.LOCAL)
            .forAll(Matchers.any())
            .buildModule(),
                new AbstractModule() {

                    protected void configure() {
                        //tell Warp the name of the jpa persistence unit
                        bindConstant().annotatedWith(JpaUnit.class).to("testUnit");
                    }
                });
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testEMFactoryDuplicateAvoidance() {
        //startup persistence
        injector.getInstance(PersistenceService.class)
                .start();

        //startup persistence again (should fail!)
        injector.getInstance(PersistenceService.class)
                .start();

        //obtain sessionfactory
        assert false: "EntityManagerfactory duplication!!!";
    }
}