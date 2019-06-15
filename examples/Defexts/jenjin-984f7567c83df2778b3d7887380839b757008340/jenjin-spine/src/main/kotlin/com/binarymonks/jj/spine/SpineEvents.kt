package com.binarymonks.jj.core.spine

import com.binarymonks.jj.spine.components.SpineComponent
import com.esotericsoftware.spine.Event


typealias SpineEventHandler = (spineComponent: SpineComponent, event: Event) -> Unit