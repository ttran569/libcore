package server.services;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    AuthenticationServiceTest.class,
    SessionManagerTest.class,
    ResourceManagerTest.class,
    TransactionManagerTest.class,
    UserManagerTest.class
})
public class AllTests {}