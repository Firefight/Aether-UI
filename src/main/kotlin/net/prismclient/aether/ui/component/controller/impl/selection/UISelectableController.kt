package net.prismclient.aether.ui.component.controller.impl.selection

import net.prismclient.aether.ui.component.UIComponent
import net.prismclient.aether.ui.component.controller.UIController
import java.util.function.Consumer

/**
 * [UISelectableController] is a controller which accepts [T] and only allows
 * for one of [T] to be the [selectedComponent] at a time. A common use for this
 * type of controller is for a list of components, where only one component can be
 * selected at a time, like a radio .
 *
 * @author sen
 * @since 6/3/2022
 * @param T The type of component you want to control. Leave T as UIComponent<*> to allow any component to be selected.
 */
class UISelectableController<T : UIComponent<*>> : UIController<T>() {
    /**
     * The component that is actively selected
     */
    var selectedComponent: T? = null
        private set

    /**
     * The action to apply to all components when they are selected
     *
     * @see onSelection
     */
    var selectedComponentAction: Consumer<T>? = null
        private set

    /**
     * The action to apply to all components when they are deselected
     *
     * @see onDeselection
     */
    var deselectedComponentAction: Consumer<T>? = null
        private set

    /**
     * Selects the component at the given index in the list
     */
    fun selectComponent(index: Int) = selectComponent(components[index])

    /**
     * Selects the given component and notifies all the other component to deselect
     */
    fun selectComponent(component: UIComponent<*>) {
        try {
            selectedComponent = component as T
        } catch (_: Exception) {
            throw ClassCastException("Failed to cast $component to the current type of this controller. Make sure you're passing in the correct type of component.")
        }
        // Invoke the selected action on the selected component
        selectedComponentAction?.accept(component)

        // Invoke the deselect action on all the other components
        components.forEach {
            if (it != component) {
                deselectedComponentAction?.accept(it)
            }
            it.update()
        }
    }

    /**
     * The action that is preformed when a component is selected
     */
    fun onSelection(action: Consumer<T>) {
        selectedComponentAction = action
    }

    /**
     * The action that is performed when a component is deselected
     */
    fun onDeselection(action: Consumer<T>) {
        deselectedComponentAction = action
    }
}