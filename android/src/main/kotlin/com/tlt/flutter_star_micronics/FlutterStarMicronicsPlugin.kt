package com.tlt.flutter_star_micronics

import android.content.Context
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
import com.starmicronics.stario.StarPrinterStatus
import com.starmicronics.starioextension.ICommandBuilder
import com.starmicronics.starioextension.StarIoExt
import com.starmicronics.starioextension.StarIoExt.Emulation
import com.starmicronics.starioextension.StarIoExtManager
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.PluginRegistry

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
  protected var starIoExtManager: StarIoExtManager? = null
  companion object {
    protected lateinit var applicationContext: Context

    @JvmStatic
    fun registerWith(registrar: PluginRegistry.Registrar) {
      val channel = MethodChannel(registrar.messenger(), "flutter_star_prnt")
      channel.setMethodCallHandler(FlutterStarMicronicsPlugin())
      FlutterStarMicronicsPlugin.setupPlugin(registrar.messenger(), registrar.context())
    }
    @JvmStatic
    fun setupPlugin(messenger: BinaryMessenger, context: Context) {
      try {
        applicationContext = context.getApplicationContext()
        val channel = MethodChannel(messenger, "StarMicronicsSDK")
        channel.setMethodCallHandler(FlutterStarMicronicsPlugin())
      } catch (e: Exception) {
        Log.e("StarMicronicsSDK", "Registration failed", e)
      }
    }
  }

  private lateinit var channel : MethodChannel
  private var logTag: String = "StarMicronicsSDK"
  private var commandsSupport: Map<String, Boolean> = mapOf("appendEncoding" to true, "append" to true, "appendRaw" to true, "appendFontStyle" to true, "appendCodePage" to true, "appendInternational" to true, "appendLineFeed" to true, "appendUnitFeed" to true, "appendCharacterSpace" to true, "appendLineSpace" to true, "appendEmphasis" to true, "appendInvert" to true, "appendMultiple" to true, "appendUnderLine" to true, "appendAbsolutePosition" to true, "appendAlignment" to true, "appendHorizontalTabPosition" to true, "appendCutPaper" to true, "appendPeripheral" to true, "appendSound" to true, "appendBarcode" to true, "appendBitmap" to true, "appendBitmapWithAlignment" to true, "appendBitmapWithAbsolutePosition" to true, "appendPrintableArea" to true)


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
    val address: String = call.argument<String>("address") as String
    val portName: String = call.argument<String>("portName") as String
    val emulation: String = call.argument<String>("emulation") as String
    val commands: ArrayList<Map<String, Any>> = call.argument<ArrayList<Map<String, Any>>>("commands") as ArrayList<Map<String, Any>>
    var emulationObj: Emulation = getEmulation(emulation);
    var resp = StarMicronicsResult()
    try {
      val builder: ICommandBuilder = StarIoExt.createCommandBuilder(getEmulation(emulation))
      builder.beginDocument()
      commands.forEach {
        onGenerateCommand(builder, call, it)
      }
      builder.endDocument()
//      onSendCommand(portName, getPortSettingsOption(emulation), builder.getCommands(), applicationContext, result)
    } catch (e: StarIOPortException) {
      e.printStackTrace()
      resp.success = false
      resp.message =  "Print error"
      result.success(resp.toJSON())
    }
  }

  private fun onSendCommand(portName: String, portSettings: String, commands: ByteArray, context: Context, @NonNull result: Result) {
    var port: StarIOPort? = null
    try {
      port = StarIOPort.getPort(portName, portSettings, 10000, applicationContext)
      try {
        Thread.sleep(100)
      } catch (e: InterruptedException) {
      }
      var status: StarPrinterStatus = port.beginCheckedBlock()
      if (status.offline) {
        throw StarIOPortException("A printer is offline")
      }
      port.writePort(commands, 0, commands.size)
      port.setEndCheckedBlockTimeoutMillis(30000); // Change the timeout time of endCheckedBlock method.
      status = port.endCheckedBlock()
      if (status.coverOpen) {
        result.error("STARIO_PORT_EXCEPTION", "Cover open", null)
        return
      } else if (status.receiptPaperEmpty) {
        result.error("STARIO_PORT_EXCEPTION", "Empty paper", null)
        return
      } else if (status.offline) {
        result.error("STARIO_PORT_EXCEPTION", "Printer offline", null)
        return
      }
      result.success("Success")
    } catch (e: Exception) {
      result.error("STARIO_PORT_EXCEPTION", e.message, null)
    }
  }

  private fun onGenerateCommand(builder: ICommandBuilder, call: MethodCall, command: Map<String, Any>){
    Log.d(logTag, "onGenerateCommand: $command")
  }


  private fun getEmulation(emulation: String?): Emulation {
    when (emulation) {
      "StarPRNT" -> return Emulation.StarPRNT
      "StarPRNTL" -> return Emulation.StarPRNTL
      "StarLine" -> return Emulation.StarLine
      "StarGraphic" -> return Emulation.StarGraphic
      "EscPos" -> return Emulation.EscPos
      "EscPosMobile" -> return Emulation.EscPosMobile
      "StarDotImpact" -> return Emulation.StarDotImpact
      else -> return Emulation.StarLine
    }
  }

  private fun getPortSettingsOption(emulation: String): String { // generate the portsettings depending on the emulation type
    when (emulation) {
      "EscPosMobile" -> return "mini"
      "EscPos" -> return "escpos"
      "StarPRNT", "StarPRNTL" -> return "Portable;l"
      else -> return emulation
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
