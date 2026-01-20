package com.elvish.plugin.textmate

import org.jetbrains.plugins.textmate.api.TextMateBundleProvider
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider.PluginBundle
import java.nio.file.Path

class ElvishTextMateBundleProvider : TextMateBundleProvider {
    override fun getBundles(): List<PluginBundle> {
        val bundleUrl = javaClass.getResource("/textmate") ?: return emptyList()
        return listOf(PluginBundle("Elvish", Path.of(bundleUrl.toURI())))
    }
}
