package net.prismclient.aether.ui.screen

import net.prismclient.aether.ui.UICore

/**
 * [UIScreen] is the interface for all screens. To create a scree, implement this interface and
 * invoke [UICore.displayScreen] to display the screen. Define components within the [build] function.
 * It is automatically invoked on the creation of the screen.
 *
 * Components and frames are saved within the [UICore] class.
 *
 * @author sen
 * @since 5/29/2022
 */
interface UIScreen {
    /**
     * Invoked when the screen is initialized. All components should be defined here.
     */
    fun build()
}