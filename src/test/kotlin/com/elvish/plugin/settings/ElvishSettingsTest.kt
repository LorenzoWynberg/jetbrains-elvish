package com.elvish.plugin.settings

import org.junit.Assert.*
import org.junit.Test

class ElvishSettingsTest {

    @Test
    fun `default state has elvish as default path`() {
        val state = ElvishSettings.State()
        assertEquals("elvish", state.elvishPath)
    }

    @Test
    fun `state elvishPath can be modified`() {
        val state = ElvishSettings.State()
        state.elvishPath = "/usr/local/bin/elvish"
        assertEquals("/usr/local/bin/elvish", state.elvishPath)
    }

    @Test
    fun `state can be created with custom path`() {
        val state = ElvishSettings.State(elvishPath = "/custom/path/elvish")
        assertEquals("/custom/path/elvish", state.elvishPath)
    }

    @Test
    fun `getState returns current state`() {
        val settings = ElvishSettings()
        val state = settings.getState()
        assertNotNull(state)
        assertEquals("elvish", state.elvishPath)
    }

    @Test
    fun `loadState updates internal state`() {
        val settings = ElvishSettings()
        val newState = ElvishSettings.State(elvishPath = "/new/path/elvish")
        settings.loadState(newState)
        assertEquals("/new/path/elvish", settings.getState().elvishPath)
    }

    @Test
    fun `elvishPath property getter returns state value`() {
        val settings = ElvishSettings()
        assertEquals("elvish", settings.elvishPath)
    }

    @Test
    fun `elvishPath property setter updates state value`() {
        val settings = ElvishSettings()
        settings.elvishPath = "/modified/elvish"
        assertEquals("/modified/elvish", settings.elvishPath)
        assertEquals("/modified/elvish", settings.getState().elvishPath)
    }

    @Test
    fun `loadState and getState round trip preserves data`() {
        val settings = ElvishSettings()
        val originalState = ElvishSettings.State(elvishPath = "/test/elvish")
        settings.loadState(originalState)
        val retrievedState = settings.getState()
        assertEquals(originalState.elvishPath, retrievedState.elvishPath)
    }

    @Test
    fun `state equality works correctly`() {
        val state1 = ElvishSettings.State(elvishPath = "/path/elvish")
        val state2 = ElvishSettings.State(elvishPath = "/path/elvish")
        val state3 = ElvishSettings.State(elvishPath = "/other/elvish")
        assertEquals(state1, state2)
        assertNotEquals(state1, state3)
    }

    @Test
    fun `state copy works correctly`() {
        val original = ElvishSettings.State(elvishPath = "/original/elvish")
        val copy = original.copy(elvishPath = "/copied/elvish")
        assertEquals("/original/elvish", original.elvishPath)
        assertEquals("/copied/elvish", copy.elvishPath)
    }
}
