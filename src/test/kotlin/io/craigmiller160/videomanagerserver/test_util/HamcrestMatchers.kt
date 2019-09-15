package io.craigmiller160.videomanagerserver.test_util

import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.springframework.mock.web.MockHttpServletResponse

fun responseBody(matcher: Matcher<String>): FeatureMatcher<MockHttpServletResponse, String> {
    return object: FeatureMatcher<MockHttpServletResponse, String>(matcher, "responseBody", "responseBody") {
        override fun featureValueOf(resp: MockHttpServletResponse?): String {
            return resp?.contentAsString ?: ""
        }
    }
}

fun header(name: String, matcher: Matcher<String>): FeatureMatcher<MockHttpServletResponse, String> {
    return object : FeatureMatcher<MockHttpServletResponse, String>(matcher, "cookie", "cookie") {
        override fun featureValueOf(resp: MockHttpServletResponse?): String {
            return resp?.getHeader(name) ?: ""
        }
    }
}