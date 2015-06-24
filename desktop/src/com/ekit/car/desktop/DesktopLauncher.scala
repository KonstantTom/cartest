package com.ekit.car.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.ekit.car.Main

/**
 * @author Konstant
 */
object DesktopLauncher {
  def main(args: Array[String]) : Unit = {
    val config = new LwjglApplicationConfiguration
    config.title = "Car"
    new LwjglApplication(new Main, config)
  }
}