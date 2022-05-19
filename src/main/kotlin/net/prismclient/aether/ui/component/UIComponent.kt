package net.prismclient.aether.ui.component

import net.prismclient.aether.UICore
import net.prismclient.aether.ui.animation.UIAnimation
import net.prismclient.aether.ui.component.type.layout.UIFrame
import net.prismclient.aether.ui.component.type.layout.styles.UIFrameSheet
import net.prismclient.aether.ui.component.util.interfaces.UIFocusable
import net.prismclient.aether.ui.style.UIProvider
import net.prismclient.aether.ui.style.UIStyleSheet
import net.prismclient.aether.ui.unit.UIUnit
import net.prismclient.aether.ui.util.UIKey
import net.prismclient.aether.ui.util.extensions.calculateX
import net.prismclient.aether.ui.util.extensions.calculateY
import net.prismclient.aether.ui.util.extensions.renderer
import java.util.function.Consumer

abstract class UIComponent<T : UIStyleSheet>(style: String) {
    var style: T = UIProvider.getStyle(style, false) as T
    var parent: UIComponent<*>? = null
    var animation: UIAnimation<*>? = null

    /** Component plot properties **/
    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f
    var relX = 0f
    var relY = 0f
    var relWidth = 0f
    var relHeight = 0f

    /** Padding and Margin **/
    var paddingTop = 0f
    var paddingRight = 0f
    var paddingBottom = 0f
    var paddingLeft = 0f

    var marginTop = 0f
    var marginRight = 0f
    var marginBottom = 0f
    var marginLeft = 0f


    var wasInside = false

    /** Listeners **/
    protected var mousePressedListeners: MutableList<Consumer<UIComponent<*>>>? = null
    protected var mouseReleasedListeners: MutableList<Consumer<UIComponent<*>>>? = null
    protected var mouseEnteredListeners: MutableList<Consumer<UIComponent<*>>>? = null
    protected var mouseLeaveListeners: MutableList<Consumer<UIComponent<*>>>? = null

    /**
     * Invoked on creation, and screen resize
     */
    open fun update() {
        // Update the position and size
        updatePosition()
        updateSize()

        // Update the relative values
        updateBounds()

        // Update the style and update the bounds again if something
        // within the style modified the plot of the component. Update
        // the style again if necessary because the bounds might change
        // the positioning of the component
        updateStyle()
    }

    open fun updatePosition() {
        x = +style.x + getParentX() - getAnchorX()
        y = -style.y + getParentY() - getAnchorY()
    }

    open fun updateSize() {
        width = +style.width
        height = -style.height
    }

    /**
     * Updates the relative values based on the absolute values
     */
    open fun updateBounds() {
        // Update padding
        paddingTop = -style.padding?.paddingTop
        paddingRight = +style.padding?.paddingRight
        paddingBottom = -style.padding?.paddingBottom
        paddingLeft = +style.padding?.paddingLeft

        // Update margin
        marginTop = -style.margin?.marginTop
        marginRight = +style.margin?.marginRight
        marginBottom = -style.margin?.marginBottom
        marginLeft = +style.margin?.marginLeft

        relX = x - paddingLeft
        relY = y - paddingTop

        relWidth = width + paddingLeft + paddingRight
        relHeight = height + paddingTop + paddingBottom
    }


    open fun updateStyle() {
        // Update font if not null
        style.font?.update(this)
    }

    open fun updateAnimation() {
        animation?.update()
    }

    open fun render() {
        updateAnimation()
        style.background?.render(relX, relY, relWidth, relHeight)
        renderer {
            if (style.clipContent) {
                scissor(relX, relY, relWidth, relHeight) {
                    renderComponent()
                }
            } else renderComponent()
        }
    }

    abstract fun renderComponent()

    /** Input **/

    open fun mouseMoved(mouseX: Float, mouseY: Float) {
        if (isMouseInsideBoundingBox() && !wasInside) {
            wasInside = true
            mouseEntered()
        } else if (wasInside) {
            mouseLeft()
            wasInside = false
        }
    }

    open fun mouseClicked(mouseX: Float, mouseY: Float) {
        if (this is UIFocusable<*> && !isFocused() && isMouseInsideBoundingBox()) {
            UICore.instance.focus(this)
        }
        mousePressedListeners?.forEach { it.accept(this) }
    }

    open fun mouseReleased(mouseX: Float, mouseY: Float) {
        mouseReleasedListeners?.forEach { it.accept(this) }
    }

    open fun keyPressed(key: UIKey, character: Char) {

    }

    open fun mouseScrolled(mouseX: Float, mouseY: Float, scrollAmount: Float) {

    }

    protected open fun mouseEntered() {
        mouseEnteredListeners?.forEach { it.accept(this) }
    }

    protected open fun mouseLeft() {
        mouseLeaveListeners?.forEach { it.accept(this) }
    }

    /** Event **/

    open fun onMousePressed(event: Consumer<UIComponent<*>>) {
        if (mousePressedListeners == null)
            mousePressedListeners = mutableListOf()
        mousePressedListeners!!.add(event)
    }

    open fun onMouseReleased(event: Consumer<UIComponent<*>>) {
        if (mouseReleasedListeners == null)
            mouseReleasedListeners = mutableListOf()
        mouseReleasedListeners!!.add(event)
    }

    open fun onMouseEnter(event: Consumer<UIComponent<*>>) {
        if (mouseEnteredListeners == null)
            mouseEnteredListeners = mutableListOf()
        mouseEnteredListeners!!.add(event)
    }

    open fun onMouseLeave(event: Consumer<UIComponent<*>>) {
        if (mouseLeaveListeners == null)
            mouseLeaveListeners = mutableListOf()
        mouseLeaveListeners!!.add(event)
    }

    /** Position Calculation **/

    operator fun UIUnit?.unaryPlus() = this.getX()

    operator fun UIUnit?.unaryMinus() = this.getY()

    @JvmOverloads
    fun UIUnit?.getX(ignore: Boolean = false): Float = this.getX(getParentWidth(), ignore)

    @JvmOverloads
    fun UIUnit?.getX(width: Float, ignore: Boolean = false): Float =
            calculateUnitX(this, width, ignore)

    fun calculateUnitX(unit: UIUnit?, width: Float, ignore: Boolean): Float = if (unit == null) 0f else {
        calculateX(unit, this, width, ignore)
    }

    @JvmOverloads
    fun UIUnit?.getY(ignore: Boolean = false): Float = this.getY(getParentHeight(), ignore)

    @JvmOverloads
    fun UIUnit?.getY(height: Float, ignore: Boolean = false) =
            calculateUnitY(this, height, ignore)

    fun calculateUnitY(unit: UIUnit?, height: Float, ignore: Boolean): Float = if (unit == null) 0f else {
        calculateY(unit, this, height, ignore)
    }

    fun getParentX() = if (parent != null)
            if (parent is UIFrame && ((parent as UIFrame).style as UIFrameSheet).clipContent) 0f else parent!!.x else 0f

    fun getParentY() = if (parent != null)
            if (parent is UIFrame && ((parent as UIFrame).style as UIFrameSheet).clipContent) 0f else parent!!.y else 0f

    fun getParentWidth() =
            if (parent != null) parent!!.width else UICore.width

    fun getParentHeight() =
            if (parent != null) parent!!.height else UICore.height

    fun getAnchorX() =
            calculateUnitX(style.anchor.x, width, false)

    fun getAnchorY() =
            calculateUnitY(style.anchor.y, height, false)

    fun isMouseInside() =
            (getMouseX() > x) &&
            (getMouseY() > y) &&
            (getMouseX() < x + width) &&
            (getMouseY() < y + height)

    /**
     * Returns true if the mouse is inside the rel x, y, width and height
     */
    fun isMouseInsideBoundingBox() =
            (getMouseX() > relX) &&
            (getMouseY() > relY) &&
            (getMouseX() < relX + relWidth) &&
            (getMouseY() < relY + relHeight)

    fun isFocused(): Boolean = UICore.instance.focusedComponent == this

    fun getMouseX(): Float =
            UICore.mouseX - getParentXOffset()

    fun getMouseY(): Float =
            UICore.mouseY - getParentYOffset()

    protected fun getParentXOffset(): Float =
            if (parent != null && parent is UIFrame && (parent!!.style as UIFrameSheet).clipContent) parent!!.relX else 0f

    protected fun getParentYOffset(): Float =
            if (parent != null && parent is UIFrame && (parent!!.style as UIFrameSheet).clipContent) parent!!.relY else 0f

    protected fun isWithinBounds(x: Float, y: Float, width: Float, height: Float) =
            (x <= getMouseX() && y <= getMouseY() && x + width >= getMouseX() && y + height >= getMouseY())

    /** Other **/
    inline fun style(block: T.() -> Unit) =
        block.invoke(style)
}