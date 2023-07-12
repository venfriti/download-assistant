package com.example.button


sealed class ButtonState {
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}
