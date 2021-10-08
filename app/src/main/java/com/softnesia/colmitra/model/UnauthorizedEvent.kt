package com.softnesia.colmitra.model

class UnauthorizedEvent private constructor() {
    companion object {
        private val INSTANCE: UnauthorizedEvent = UnauthorizedEvent()

        fun instance(): UnauthorizedEvent {
            return INSTANCE
        }
    }
}