package net.prismclient.aether.ui.component.type.input.slider

import net.prismclient.aether.ui.shape.UIColoredShape
import net.prismclient.aether.ui.shape.type.UIRectangle
import net.prismclient.aether.ui.style.UIStyleSheet
import net.prismclient.aether.ui.util.extensions.px
import net.prismclient.aether.ui.util.extensions.radius
import net.prismclient.aether.ui.util.extensions.rel

class UISliderSheet : UIStyleSheet() {
    var sliderControl: UIColoredShape = UIRectangle().also {
        it.color = -1
        it.width = px(40)
        it.height = rel(1)
        it.radius = radius(2.5f)
    }
}