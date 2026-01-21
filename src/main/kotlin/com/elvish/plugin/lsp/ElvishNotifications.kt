package com.elvish.plugin.lsp

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles notifications for the Elvish plugin.
 * Prevents duplicate notifications from being shown.
 */
object ElvishNotifications {
    private const val NOTIFICATION_GROUP_ID = "Elvish Plugin"
    private const val SETTINGS_CONFIGURABLE_ID = "com.elvish.plugin.settings"

    // Track which projects have been notified about missing binary
    // to avoid spamming notifications
    private val notifiedProjects = ConcurrentHashMap<String, Boolean>()

    /**
     * Show a notification that the Elvish binary was not found.
     * Only shows once per project until the cache is reset.
     *
     * @param project The project to show the notification for
     * @param elvishPath The path that was searched for
     */
    fun notifyElvishNotFound(project: Project, elvishPath: String) {
        val projectKey = project.locationHash

        // Don't spam notifications - only show once per project
        if (notifiedProjects.putIfAbsent(projectKey, true) != null) {
            return
        }

        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(
                "Elvish Not Found",
                "Could not find Elvish binary at '$elvishPath'. " +
                    "LSP features are disabled. " +
                    "Please install Elvish or configure the path in settings.",
                NotificationType.WARNING
            )

        notification.addAction(object : AnAction("Open Settings") {
            override fun actionPerformed(e: AnActionEvent) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, SETTINGS_CONFIGURABLE_ID)
                notification.expire()
            }
        })

        notification.addAction(object : AnAction("Install Elvish") {
            override fun actionPerformed(e: AnActionEvent) {
                com.intellij.ide.BrowserUtil.browse("https://elv.sh/get/")
                notification.expire()
            }
        })

        notification.notify(project)
    }

    /**
     * Reset the notification state for a project.
     * Call this when settings change to allow re-checking.
     */
    fun resetNotificationState(project: Project) {
        val projectKey = project.locationHash
        notifiedProjects.remove(projectKey)
    }

    /**
     * Reset all notification states.
     */
    fun resetAllNotificationStates() {
        notifiedProjects.clear()
    }
}
