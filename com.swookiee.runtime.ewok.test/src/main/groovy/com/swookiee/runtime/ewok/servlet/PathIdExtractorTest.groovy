/*******************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lars Pfannenschmidt - initial API and implementation, ongoing development and documentation
 *******************************************************************************/

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
