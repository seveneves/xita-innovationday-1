package com.example

import java.util.UUID

import spray.json.DefaultJsonProtocol

case class Android(os: String, ui: String)
case class Battery(standbyTime: String, talkTime: String, batteryType: String)
case class Camera(features: List[String], primary: String)
case class Connectivity(bluetooth: String, cell: String, gps: Boolean, infrared: Boolean, wifi: String)
case class Display(screenResolution: String, screenSize: String, touchScreen: Boolean)
case class Hardware(accelerometer: Boolean, audioJack: String, cpu: String, fmRadio: Boolean, physicalKeyboard: Boolean, usb: String)
case class SizeAndWeight(dimensions: List[String], weight: String)
case class Storage(flash: String, ram: String)
case class Device(additionalFeatures: String, android: Android, availability: List[String], battery: Battery, camera: Camera, connectivity: Connectivity, description: String, display: Display, hardware: Hardware, id: String, images: List[String], name: String, sizeAndWeight: SizeAndWeight, storage: Storage)

//product domain
object Android extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(Android.apply)
}

object Battery extends DefaultJsonProtocol {
  implicit val format = jsonFormat3(Battery.apply)
}

object Connectivity extends DefaultJsonProtocol {
  implicit val format = jsonFormat5(Connectivity.apply)
}

object Display extends DefaultJsonProtocol {
  implicit val format = jsonFormat3(Display.apply)
}
object Hardware extends DefaultJsonProtocol {
  implicit val format = jsonFormat6(Hardware.apply)
}
object Camera extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(Camera.apply)
}
object SizeAndWeight extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(SizeAndWeight.apply)
}
object Storage extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(Storage.apply)
}

object Device extends DefaultJsonProtocol {
  implicit val format = jsonFormat14(Device.apply)
}

