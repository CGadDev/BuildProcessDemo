// Christina Gadson
// 10/09/2026

package com.christina.builddemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BuildServerTest {

    // Tests the content returned by the home page.
    @Test
    void homePageReturnsBuildInformation() {

        String response =
                BuildServer.getResponseBody("/");

        assertTrue(response.contains("Build Process Demo"));
        assertTrue(response.contains("automated Maven build"));
        assertTrue(response.contains("Jenkins CI"));
    }

    // Tests the content returned by the status page.
    @Test
    void statusPageReportsServerRunning() {

        String response =
                BuildServer.getResponseBody("/status");

        assertTrue(response.contains("Status"));
        assertTrue(response.contains("Server is running"));
    }

    // Tests status codes for multiple valid routes.
    @Test
    void validRoutesReturnStatus200() {

        assertEquals(
                200,
                BuildServer.getStatusCode("/"));

        assertEquals(
                200,
                BuildServer.getStatusCode("/status"));

        assertEquals(
                200,
                BuildServer.getStatusCode("/greet"));
    }

    // Tests the behavior of an invalid route.
    @Test
    void invalidRouteReturns404() {

        assertEquals(
                404,
                BuildServer.getStatusCode("/invalid"));

        String response =
                BuildServer.getResponseBody("/invalid");

        assertTrue(response.contains("404"));
    }

    // Tests the new personalized greeting feature.
    @Test
    void greetingPageReturnsPersonalizedMessage() {

        String response =
                BuildServer.getResponseBody(
                        "/greet", "name=Christina");

        assertTrue(response.contains("Hello, Christina!"));
        assertTrue(response.contains("Welcome to BuildProcessDemo."));
    }

    // Tests the default greeting when no name is provided.
    @Test
    void greetingPageUsesDefaultName() {

        String response =
                BuildServer.getResponseBody("/greet", null);

        assertTrue(response.contains("Hello, Guest!"));

        String emptyNameResponse =
                BuildServer.getResponseBody("/greet", "name=");

        assertTrue(emptyNameResponse.contains("Hello, Guest!"));
    }

    // Tests URL decoding for names containing spaces.
    @Test
    void greetingPageDecodesNames() {

        String response =
                BuildServer.getResponseBody(
                        "/greet", "name=Alex%20Rivera");

        assertTrue(response.contains("Hello, Alex Rivera!"));
    }

    // Tests that HTML characters are safely escaped.
    @Test
    void greetingPageEscapesHtmlCharacters() {

        String response =
                BuildServer.getResponseBody(
                        "/greet", "name=%3Cscript%3E");

        assertTrue(response.contains(
                "Hello, &lt;script&gt;!"));

        assertFalse(response.contains("<script>"));
    }
}
