// Christina Gadson
// 9/16/2026

package com.christina.builddemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}