package com.intuit.data.runtime.client;

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import org.junit.Test

import com.intuit.data.runtime.client.servlet.util.HttpErrorException
import com.intuit.data.runtime.client.servlet.util.PathIdExtractor

public class PathIdExtractorTest {

    @Test
    void 'extract Id from valid path'(){

        PathIdExtractor idExtractor = new PathIdExtractor()
        def id = idExtractor.getId("/42")

        assertThat id, is(42L)
    }

    @Test(expected=HttpErrorException)
    void 'extract Id from invalid path and expect Exception'(){

        PathIdExtractor idExtractor = new PathIdExtractor()
        def id = idExtractor.getId("/foobar")
    }
}
