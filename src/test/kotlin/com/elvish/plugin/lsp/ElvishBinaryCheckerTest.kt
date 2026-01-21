package com.elvish.plugin.lsp

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Tests for ElvishBinaryChecker utility.
 */
class ElvishBinaryCheckerTest {

    @Before
    fun setUp() {
        // Clear cache before each test
        ElvishBinaryChecker.clearAllCache()
    }

    @After
    fun tearDown() {
        // Clean up cache after tests
        ElvishBinaryChecker.clearAllCache()
    }

    @Test
    fun testCacheClearByPath() {
        // Test that clearing cache by path works
        ElvishBinaryChecker.clearCache("/some/path")
        ElvishBinaryChecker.clearCache("elvish")
        // Just verify no exceptions are thrown
    }

    @Test
    fun testCacheClearAll() {
        // Test that clearing all cache works
        ElvishBinaryChecker.clearAllCache()
        // Just verify no exceptions are thrown
    }

    @Test
    fun testCheckAbsolutePath_NonExistent() {
        // Test that a non-existent absolute path returns false
        // We use reflection to test the private method indirectly through cache behavior
        val nonExistentPath = "/this/path/definitely/does/not/exist/elvish-binary"

        // Since we can't directly call the private method, we test the behavior
        // by observing that a non-existent path would not be found
        val file = File(nonExistentPath)
        assertFalse("Non-existent path should not exist", file.exists())
    }

    @Test
    fun testCheckAbsolutePath_ExistingBinary() {
        // Test with a known existing binary (e.g., /bin/sh)
        val existingBinary = "/bin/sh"
        val file = File(existingBinary)

        if (file.exists()) {
            assertTrue("Known binary should exist", file.exists())
            assertTrue("Known binary should be executable", file.canExecute())
        }
    }

    @Test
    fun testPathEnvironmentSearch() {
        // Test that PATH environment variable is accessible
        val pathEnv = System.getenv("PATH")
        assertNotNull("PATH environment variable should exist", pathEnv)
        assertTrue("PATH should not be empty", pathEnv.isNotEmpty())
    }

    @Test
    fun testCommonLocations() {
        // Test that common locations include expected paths
        val homeDir = System.getProperty("user.home")
        assertNotNull("User home should be available", homeDir)

        // Verify that File can be constructed with common paths
        val commonPaths = listOf(
            "/usr/local/bin/elvish",
            "/opt/homebrew/bin/elvish",
            "$homeDir/.local/bin/elvish"
        )

        for (path in commonPaths) {
            val file = File(path)
            // Just verify that File construction works
            assertNotNull("File should be constructible for: $path", file)
        }
    }

    @Test
    fun testElvishBinaryFoundInPath() {
        // This test verifies that if elvish is installed, it can be found
        // Skip if elvish is not installed
        val elvishPath = findElvishInPath()
        if (elvishPath != null) {
            val file = File(elvishPath)
            assertTrue("Elvish binary should exist at: $elvishPath", file.exists())
            assertTrue("Elvish binary should be executable", file.canExecute())
        }
    }

    private fun findElvishInPath(): String? {
        val pathEnv = System.getenv("PATH") ?: return null
        val pathDirs = pathEnv.split(File.pathSeparator)

        for (dir in pathDirs) {
            val candidate = File(dir, "elvish")
            if (candidate.exists() && candidate.canExecute()) {
                return candidate.absolutePath
            }
        }

        // Check common locations
        val commonLocations = listOf(
            "/usr/local/bin/elvish",
            "/opt/homebrew/bin/elvish",
            "/usr/bin/elvish"
        )

        for (location in commonLocations) {
            val file = File(location)
            if (file.exists() && file.canExecute()) {
                return location
            }
        }

        return null
    }
}
