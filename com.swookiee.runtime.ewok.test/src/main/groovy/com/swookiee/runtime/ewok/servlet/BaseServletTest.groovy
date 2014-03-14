package com.swookiee.runtime.ewok.servlet

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * This base class can be used in JUnit test cases to add additional logging. It logs which test method will be executed.
 */
public abstract class BaseServletTest {
    final protected Logger logger = LoggerFactory.getLogger(this.getClass())

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            logger.info(" +++ Starting Test: {} +++", description.getMethodName())
        }
    }
}
