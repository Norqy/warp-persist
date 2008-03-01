package com.wideplay.warp.jpa;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: dprasanna
 * Date: 31/05/2007
 * Time: 15:26:06
 * <p/>
 *
 * A placeholder that frees me from having to use statics to make a singleton EM factory,
 * so I can use per-injector singletons vs. per JVM/classloader singletons (which doesnt really work
 * for several reasons).
 *
 * @author dprasanna
 * @since 1.0
 */
@ThreadSafe
class EntityManagerFactoryHolder {
    @GuardedBy("setEntityManagerFactory") //BUT read concurrently, so has to be volatile...
    private volatile EntityManagerFactory entityManagerFactory;

    //A hack to provide the EntityManager factory statically to non-guice objects (interceptors), that can be thrown away come guice1.1
    private static volatile EntityManagerFactoryHolder singletonEmFactoryHolder;

    //have to manage the em oursevles--not neat like hibernate =(
    private final ThreadLocal<EntityManager> entityManager = new ThreadLocal<EntityManager>();

    //store singleton
    public EntityManagerFactoryHolder() {
        singletonEmFactoryHolder = this;
    }

    EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    synchronized void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        if (null != this.entityManagerFactory)
            throw new RuntimeException("Duplicate EntityManager factory creation! Only one EntityManager factory is allowed per injector");

        this.entityManagerFactory = entityManagerFactory;
    }

    static EntityManagerFactory getCurrentEntityManagerFactory() {
        return singletonEmFactoryHolder.getEntityManagerFactory();
    }





    //@ThreadLocal
    static void closeCurrentEntityManager() {
        EntityManager em = singletonEmFactoryHolder.entityManager.get();

        if (null != em) {
            try {
                if (em.isOpen())
                    em.close();
            } finally {
                singletonEmFactoryHolder.entityManager.remove();
            }
        }
    }


    //@ThreadLocal
    static EntityManager checkCurrentEntityManager() {
        return singletonEmFactoryHolder.entityManager.get();
    }


    //@ThreadLocal
    static EntityManager getCurrentEntityManager() {
        EntityManager em = singletonEmFactoryHolder.entityManager.get();

        //create if absent
        if (null == em ) {
            em = getCurrentEntityManagerFactory().createEntityManager();
            singletonEmFactoryHolder.entityManager.set(em);
        }

        return em;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof EntityManagerFactoryHolder)) return false;

        EntityManagerFactoryHolder that = (EntityManagerFactoryHolder) o;

        return (entityManagerFactory == null ? that.entityManagerFactory == null : entityManagerFactory.equals(that.entityManagerFactory));

    }

    @Override
    public int hashCode() {
        return (entityManagerFactory != null ? entityManagerFactory.hashCode() : 0);
    }


    //@ThreadLocal
    public EntityManager getEntityManager() {
        return getCurrentEntityManager();
    }
}
