package org.lightspring.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.lightspring.test.v1.V1AllTests;
import org.lightspring.test.v2.V2AllTests;
import org.lightspring.test.v3.V3AllTests;
import org.lightspring.test.v4.V4AllTests;
import org.lightspring.test.v5.V5AllTests;
import org.lightspring.test.v6.V6AllTests;

@RunWith(Suite.class)
@SuiteClasses({V1AllTests.class, V2AllTests.class,
        V3AllTests.class, V4AllTests.class,
        V5AllTests.class, V6AllTests.class})
public class AllTests {

}
