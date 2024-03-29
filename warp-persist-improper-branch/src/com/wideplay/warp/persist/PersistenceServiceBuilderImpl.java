package com.wideplay.warp.persist;

import com.google.inject.Module;
import com.google.inject.matcher.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: dprasanna
 * Date: 31/05/2007
 * Time: 11:54:45
 * <p/>
 *
 * Configures and builds a Module for use in a Guice injector to enable the PersistenceService.
 *
 * @author dprasanna
 * @since 1.0
 */
class PersistenceServiceBuilderImpl implements SessionStrategyBuilder, PersistenceModuleBuilder, TransactionStrategyBuilder {
    private final PersistenceModule persistenceModule;

    PersistenceServiceBuilderImpl(PersistenceModule persistenceModule) {
        this.persistenceModule = persistenceModule;
    }

    public TransactionStrategyBuilder across(UnitOfWork unitOfWork) {
        persistenceModule.setUnitOfWork(unitOfWork);

        return this;
    }

    public Module buildModule() {
        return persistenceModule;
    }


    public TransactionStrategyBuilder transactedWith(TransactionStrategy transactionStrategy) {
        persistenceModule.setTransactionStrategy(transactionStrategy);

        return this;
    }


    public TransactionStrategyBuilder addAccessor(Class<?> daoInterface) {
        persistenceModule.addAccessor(daoInterface);

        return this;
    }

    public PersistenceModuleBuilder forAll(Matcher<? super Class<?>> classMatcher) {
        persistenceModule.setClassMatcher(classMatcher);

        return this;
    }
}
