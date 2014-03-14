package com.swookiee.runtime.ewok.servlet

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import org.junit.Test

import com.swookiee.runtime.ewok.util.HttpErrorException
import com.swookiee.runtime.ewok.util.PathIdExtractor

public class PathIdExtractorTest extends BaseServletTest {

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
