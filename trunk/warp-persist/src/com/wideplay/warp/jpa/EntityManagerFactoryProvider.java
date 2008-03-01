package com.wideplay.warp.jpa;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.persistence.EntityManagerFactory;

import net.jcip.annotations.Immutable;

/**
 * Created with IntelliJ IDEA.
 * On: 30/04/2007
 *
 * @author Dhanji R. Prasanna <a href="mailto:dhanji@gmail.com">email</a>
 * @since 1.0
 */
@Immutable
class EntityManagerFactoryProvider implements Provider<EntityManagerFactory> {
    private final EntityManagerFactoryHolder emFactoryHolder;

    @Inject
    public EntityManagerFactoryProvider(EntityManagerFactoryHolder sessionFactoryHolder) {
        this.emFactoryHolder = sessionFactoryHolder;
    }

    public EntityManagerFactory get() {
        return this.emFactoryHolder.getEntityManagerFactory();
    }
}