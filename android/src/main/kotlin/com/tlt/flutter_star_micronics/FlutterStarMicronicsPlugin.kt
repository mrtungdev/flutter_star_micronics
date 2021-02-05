package com.tlt.flutter_star_micronics

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import com.starmicronics.stario.PortInfo
import com.starmicronics.stario.StarIOPort
import com.starmicronics.stario.StarIOPortException
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import com.google.gson.Gson

interface JSONConvertable {
  fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T : JSONConvertable> String.toObject(): T = Gson().fromJson(this, T::class.java)

data class StarMicronicsResult(
        var type: String? = null,
        var success: Boolean? = null,
        var message: String? = null,
        var content: Any? = null
): JSONConvertable

class StarPrinterInfo(
        var address: String? = null,
        var portName: String? = null,
        var model: String? = null
): JSONConvertable

/** FlutterStarMicronicsPlugin */
class FlutterStarMicronicsPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var logTag: String = "StarMicronicsSDK"

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_star_micronics")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull rawResult: Result) {
    val result: MethodResultWrapper = MethodResultWrapper(rawResult)
    Thread(MethodRunner(call, result)).start()
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  inner class MethodRunner(call: MethodCall, result: Result) : Runnable {
    private val call: MethodCall = call
    private val result: Result = result

    override fun run() {
      when (call.method) {
        "discovery" -> {
          onDiscovery(call, result)
        }
        "print" -> {
          onPrint(call, result)
        }
        else -> result.notImplemented()
      }
    }
  }

  private fun onDiscovery(@NonNull call: MethodCall, @NonNull result: Result) {
//    val arrayDiscovery: MutableList<PortInfo> = mutableListOf<PortInfo>()
    val printType: String = call.argument<String>("type") as String
    Log.d(logTag, "onDiscovery type: $printType")
    when (printType) {
      "tcp" -> {
        onDiscoveryTCP(call, result)
      }
      else -> result.notImplemented()
    }
  }

  private fun onDiscoveryTCP(@NonNull call: MethodCall, @NonNull result: Result){
    var resp = StarMicronicsResult()
    try {
      var printers: MutableList<StarPrinterInfo> = ArrayList()
      val portList: List<PortInfo> = StarIOPort.searchPrinter("TCP:")
      for (port in portList) {
        Log.i(logTag, "Port Name: " + port.portName)
        Log.i(logTag, "MAC Address: " + port.macAddress)
        Log.i(logTag, "Model Name: " + port.modelName)
        var printer = StarPrinterInfo(port.macAddress, port.portName, port.modelName)
        printers.add(printer)
      }
      resp.success = true
      resp.message= "Successfully!"
      resp.content = printers;
      result.success(resp.toJSON())
    } catch (e: StarIOPortException) {
      e.printStackTrace()
      resp.success = false
      resp.message =  "Unconnected"
      result.success(resp.toJSON())
    }
  }


  private fun onPrint(@NonNull call: MethodCall, @NonNull result: Result){
    var resp = StarMicronicsResult()
    try {

    } catch (e: StarIOPortException) {
      e.printStackTrace()
      resp.success = false
      resp.message =  "Print error"
      result.success(resp.toJSON())
    }
  }

  class MethodResultWrapper(methodResult: Result) : Result {

    private val methodResult: Result = methodResult
    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun success(result: Any?) {
      handler.post { methodResult.success(result) }
    }

    override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
      handler.post { methodResult.error(errorCode, errorMessage, errorDetails) }
    }

    override fun notImplemented() {
      handler.post { methodResult.notImplemented() }
    }
  }
}
