package com.elvish.plugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(
    name = "ElvishSettings",
    storages = [Storage("elvish.xml")]
)
@Service(Service.Level.PROJECT)
class ElvishSettings : PersistentStateComponent<ElvishSettings.State> {

    data class State(
        var elvishPath: String = "elvish"
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var elvishPath: String
        get() = state.elvishPath
        set(value) {
            state.elvishPath = value
        }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): ElvishSettings {
            return project.getService(ElvishSettings::class.java)
        }
    }
}
