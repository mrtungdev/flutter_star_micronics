package com.tlt.flutter_star_micronics

import android.R.attr.port
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore.Images.Media.getBitmap
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Base64
import android.util.Log
import androidx.annotation.NonNull
import com.google.gson.Gson
import com.starmicronics.stario.PortInfo
import com.starmicronics.stario.StarIOPort
import com.starmicronics.stario.StarIOPortException
import com.starmicronics.stario.StarPrinterStatus
import com.starmicronics.starioextension.ICommandBuilder
import com.starmicronics.starioextension.StarIoExt
import com.starmicronics.starioextension.StarIoExt.Emulation
import com.starmicronics.starioextension.StarIoExtManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import java.io.IOException
import java.nio.charset.Charset


interface JSONConvertable {
    fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T : JSONConvertable> String.toObject(): T = Gson().fromJson(this, T::class.java)

data class StarMicronicsResult(
        var type: String? = null,
        var success: Boolean? = null,
        var message: String? = null,
        var content: Any? = null
) : JSONConvertable

class StarPrinterInfo(
        var address: String? = null,
        var portName: String? = null,
        var model: String? = null
) : JSONConvertable

/** FlutterStarMicronicsPlugin */
class FlutterStarMicronicsPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private var starIoExtManager: StarIoExtManager? = null

    companion object {
        protected lateinit var applicationContext: Context

        @JvmStatic
        fun registerWith(registrar: PluginRegistry.Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutter_star_micronics_sdk")
            channel.setMethodCallHandler(FlutterStarMicronicsPlugin())
            FlutterStarMicronicsPlugin.setupPlugin(registrar.messenger(), registrar.context())
        }

        @JvmStatic
        fun setupPlugin(messenger: BinaryMessenger, context: Context) {
            try {
                applicationContext = context.applicationContext
                val channel = MethodChannel(messenger, "flutter_star_micronics_sdk")
                channel.setMethodCallHandler(FlutterStarMicronicsPlugin())
            } catch (e: Exception) {
                Log.e("StarMicronicsSDK", "Registration failed", e)
            }
        }
    }

    private lateinit var channel: MethodChannel
    private var logTag: String = "StarMicronicsSDK"
//  private var commandsSupport: Map<String, Boolean> = mapOf("appendEncoding" to true, "append" to true, "appendRaw" to true, "appendFontStyle" to true, "appendCodePage" to true, "appendInternational" to true, "appendLineFeed" to true, "appendUnitFeed" to true, "appendCharacterSpace" to true, "appendLineSpace" to true, "appendEmphasis" to true, "appendInvert" to true, "appendMultiple" to true, "appendUnderLine" to true, "appendAbsolutePosition" to true, "appendAlignment" to true, "appendHorizontalTabPosition" to true, "appendCutPaper" to true, "appendPeripheral" to true, "appendSound" to true, "appendBarcode" to true, "appendBitmap" to true, "appendBitmapWithAlignment" to true, "appendBitmapWithAbsolutePosition" to true, "appendPrintableArea" to true)


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_star_micronics")
        channel.setMethodCallHandler(FlutterStarMicronicsPlugin())
        setupPlugin(flutterPluginBinding.flutterEngine.dartExecutor, flutterPluginBinding.applicationContext)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull rawResult: Result) {
        val result = MethodResultWrapper(rawResult)
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

    private fun onDiscoveryTCP(@NonNull call: MethodCall, @NonNull result: Result) {
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
            resp.message = "Successfully!"
            resp.content = printers
            result.success(resp.toJSON())
        } catch (e: StarIOPortException) {
            e.printStackTrace()
            resp.success = false
            resp.message = "Unconnected"
            result.success(resp.toJSON())
        }
    }

    private fun onPrint(@NonNull call: MethodCall, @NonNull result: Result) {
        val address: String = call.argument<String>("address") as String
        val portName: String = call.argument<String>("portName") as String
        val emulation: String = call.argument<String>("emulation") as String
        val commands: ArrayList<Map<String, Any>> = call.argument<ArrayList<Map<String, Any>>>("commands") as ArrayList<Map<String, Any>>
        var emulationObj: Emulation = getEmulation(emulation)
        var resp = StarMicronicsResult()
        try {
            val builder: ICommandBuilder = StarIoExt.createCommandBuilder(getEmulation(emulation))
            builder.beginDocument()
            commands.forEach {
                onGenerateCommand(builder, call, it)
            }
            builder.endDocument()
            onSendCommand(portName, getPortSettingsOption(emulation), builder.commands, applicationContext, result)
        } catch (e: StarIOPortException) {
            e.printStackTrace()
            resp.success = false
            resp.message = "Print error"
            result.success(resp.toJSON())
        } finally {
            try {
                var port = StarIOPort.getPort(portName, getPortSettingsOption(emulation), 10000, applicationContext)
                // Port close
                StarIOPort.releasePort(port)
            } catch (e: StarIOPortException) {
            }
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
            var resp = StarMicronicsResult(success = false)
            var status: StarPrinterStatus = port.beginCheckedBlock()
            if (status.offline) {
                resp.message = "A printer is offline"
                result.success(resp.toJSON())
                return
//        throw StarIOPortException("A printer is offline")
            }
            port.writePort(commands, 0, commands.size)
            port.setEndCheckedBlockTimeoutMillis(30000) // Change the timeout time of endCheckedBlock method.
            status = port.endCheckedBlock()
            if (status.coverOpen) {
//        result.error("STARIO_PORT_EXCEPTION", "Cover open", null)
                resp.message = "Cover open"
                result.success(resp.toJSON())
                return
            } else if (status.receiptPaperEmpty) {
//        result.error("STARIO_PORT_EXCEPTION", "Empty paper", null)
                resp.message = "Empty paper"
                result.success(resp.toJSON())
                return
            } else if (status.offline) {
                resp.message = "Printer offline"
                result.success(resp.toJSON())
                result.error("STARIO_PORT_EXCEPTION", "Printer offline", null)
                return
            }
//      result.success("Success")
            resp.success = true
            resp.message = "Successfully"
            result.success(resp.toJSON())
        } catch (e: Exception) {
            result.error("STARIO_PORT_EXCEPTION", e.message, null)
        }
    }

    private fun onGenerateCommand(builder: ICommandBuilder, call: MethodCall, command: Map<String, Any>) {
        var encoding: Charset = Charset.forName("UTF-8")
        Log.d(logTag, "onGenerateCommand: $command")
        var commandId: String = command["id"] as String
        if (!commandId.isNullOrEmpty()) {
            var commandValue = command["value"]

            when (commandId) {
                "appendEncoding" -> {
                    var charsetName: String = "UTF-8"
                    if (!(commandValue as String).isNullOrEmpty()) {
                        charsetName = commandValue
                    }
                    encoding = try {
                        Charset.forName(charsetName)
                    } catch (e: IllegalArgumentException) {
                        Charset.forName("UTF-8")
                    }

                }
                "append" -> {
                    builder.append(commandValue.toString().toByteArray(encoding))
                }
                "appendRaw" -> {
                    builder.appendRaw(commandValue.toString().toByteArray(encoding))
                }
                "appendFontStyle" -> {
                    var fontStyle: ICommandBuilder.FontStyleType = ICommandBuilder.FontStyleType.A
                    if (!(commandValue as String).isNullOrEmpty()) {
                        if (commandValue == "B") {
                            fontStyle = ICommandBuilder.FontStyleType.B
                        }
                    }
                    builder.appendFontStyle(fontStyle)
                }
                "appendCodePage" -> {
                    var codePage = getCodepage((commandValue as String))
                    Log.d(logTag, "appendCodePage: codePage $codePage")
                    builder.appendCodePage(codePage)
                }
                "appendInternational" -> {
                    builder.appendInternational(getInternational((commandValue as String)))
                }
                "appendLineFeed" -> {
                    var bytesArray = command["bytesArray"]
                    var line = command["line"]
                    Log.d(logTag, "appendLineFeed: bytesArray $bytesArray - line $line")
                    if (bytesArray != null && line != null) {
                        builder.appendLineFeed(bytesArray as ByteArray?, line as Int)
                    } else {
                        when {
                            bytesArray != null -> {
                                builder.appendLineFeed(bytesArray as ByteArray?)
                            }
                            line != null -> {
                                builder.appendLineFeed(line as Int)
                            }
                            else -> {
                                builder.appendLineFeed()
                            }
                        }
                    }
                }
                "appendUnitFeed" -> {
                    var bytesArray = command["bytesArray"]
                    var line = command["line"]
                    Log.d(logTag, "appendUnitFeed: bytesArray $bytesArray - line $line")
                    if (bytesArray != null && line != null) {
                        builder.appendUnitFeed(bytesArray as ByteArray?, line as Int)
                    } else {
                        when {
                            bytesArray != null -> {
                                builder.appendLineFeed(bytesArray as ByteArray?)
                            }
                            line != null -> {
                                builder.appendUnitFeed(line as Int)
                            }
                        }
                    }
                }
                "appendCharacterSpace" -> {
                    builder.appendCharacterSpace(commandValue as Int)
                }
                "appendLineSpace" -> {
                    builder.appendLineSpace(commandValue as Int)
                }
                "appendEmphasis" -> {
                    builder.appendEmphasis(commandValue as Boolean)
                }
                "appendInvert" -> {
                    builder.appendInvert(commandValue as Boolean)
                }
                "appendMultiple" -> {
                    var width = command["width"] as Int
                    var height = command["height"] as Int
                    if (commandValue != null) {
                        builder.appendMultiple(commandValue.toString().toByteArray(encoding), width, height)
                    } else {
                        builder.appendMultiple(width, height)
                    }
                }
                "appendUnderLine" -> {
                    builder.appendUnderLine(commandValue as Boolean)
                }
                "appendAbsolutePosition" -> {
                    builder.appendAbsolutePosition(commandValue as Int)
                }
                "appendAlignment" -> {
                    builder.appendAlignment(getAlignment((commandValue as String)))
                }
                "appendHorizontalTabPosition" -> {
                    builder.appendHorizontalTabPosition(commandValue as IntArray?)
                }
                "appendCutPaper" -> {
                    builder.appendCutPaper(getCutPaper((commandValue as String)))
                }
                "appendPeripheral" -> {
                    var time = command["time"] as Int
                    if (time != null) {
                        builder.appendPeripheral(getPeripheralChannel(commandValue as String), time)
                    } else {
                        builder.appendPeripheral(getPeripheralChannel(commandValue as String))
                    }
                }
                "appendSound" -> {
                    val soundChl = getSoundChannel(commandValue as String)
                    val repeat = command["repeat"] as Int?
                    val driveTime = command["driveTime"] as Int?
                    val delayTime = command["delayTime"] as Int?
                    if (repeat != null && driveTime != null && delayTime != null) {
                        builder.appendSound(soundChl, repeat, driveTime, delayTime)
                    } else if (repeat != null) {
                        builder.appendSound(soundChl, repeat)
                    } else {
                        builder.appendSound(soundChl)
                    }
                }
                "appendBarcode" -> {
                    val codeSym = getBarcodeSymbology(command["symbology"] as String)
                    val width = getBarcodeWidth(command["width"] as String)
                    val height = command["height"] as Int
                    val hri = command["hri"] as Boolean
                    builder.appendBarcode(commandValue.toString().toByteArray(encoding), codeSym, width, height, hri)
                }
                "appendBitmapImg" -> {

                    try {

                        val width = command["width"] as? Int
                        Log.d(logTag, "appendBitmap: width $width")
                        val diffusion = command["diffusion"] as? Boolean
                        Log.d(logTag, "appendBitmap: diffusion $diffusion")
                        val bothScale = command["bothScale"] as? Boolean
                        Log.d(logTag, "appendBitmap: bothScale $bothScale")
                        val position = command["position"] as? Int
                        Log.d(logTag, "appendBitmap: position $position")
                        val aligmentPos = getAlignment(command["aligmentPosition"] as? String)
                        Log.d(logTag, "appendBitmap: aligmentPos $aligmentPos")
                        val rotate = getBitmapConverterRotation(command["rotation"] as? String)
                        Log.d(logTag, "appendBitmap: rotate $rotate")

//                        val imageUri: Uri = Uri.parse(commandValue.toString())
//                        val bitmap: Bitmap = getCapturedImage(imageUri)

                        val decodedString: ByteArray = (commandValue as ByteArray) ;//Base64.decode(bitmapString, Base64.DEFAULT)
                        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                        val bitmap: Bitmap? = decodedByte// convertBase64toBitmap(commandValue as String)
                        Log.d(logTag, "appendBitmap: bitmap $bitmap")
                        if (width != null && bothScale != null && diffusion != null) {

                            when {
                                position != null -> {
                                    builder.appendBitmapWithAbsolutePosition(bitmap, diffusion, width, bothScale, rotate, position)
                                }
                                aligmentPos != null -> {
                                    builder.appendBitmapWithAlignment(bitmap, diffusion, width, bothScale, rotate, aligmentPos)
                                }
                                else -> {
                                    builder.appendBitmap(bitmap, diffusion, width, bothScale, rotate)
                                }
                            }

                        } else {

                            when {
                                position != null -> {
                                    builder.appendBitmapWithAbsolutePosition(bitmap, diffusion!!, rotate, position)
                                }
                                aligmentPos != null -> {
                                    builder.appendBitmapWithAlignment(bitmap, diffusion!!, rotate, aligmentPos)
                                }
                                else -> {
                                    builder.appendBitmap(bitmap, diffusion!!, rotate)
                                }
                            }

                        }
                    } catch (e: IOException) {

                    }

                }
                "appendBitmap" -> {

                    try {

                        val width = command["width"] as? Int
                        Log.d(logTag, "appendBitmap: width $width")
                        var diffusion = command["diffusion"] as? Boolean
                        Log.d(logTag, "appendBitmap: diffusion $diffusion")
                        var bothScale = command["bothScale"] as? Boolean
                        Log.d(logTag, "appendBitmap: bothScale $bothScale")
                        var position = command["position"] as? Int
                        Log.d(logTag, "appendBitmap: position $position")
                        var aligmentPos = getAlignment(command["aligmentPosition"] as? String)
                        Log.d(logTag, "appendBitmap: aligmentPos $aligmentPos")
                        var rotate = getBitmapConverterRotation(command["rotation"] as? String)
                        Log.d(logTag, "appendBitmap: rotate $rotate")

//                        val imageUri: Uri = Uri.parse(commandValue.toString())
//                        val bitmap: Bitmap = getCapturedImage(imageUri)
                        val bitmap: Bitmap? = convertBase64toBitmap(commandValue as String)
                        Log.d(logTag, "appendBitmap: bitmap $bitmap")
                        if (width != null && bothScale != null && diffusion != null) {

                            when {
                                position != null -> {
                                    builder.appendBitmapWithAbsolutePosition(bitmap, diffusion, width, bothScale, rotate, position)
                                }
                                aligmentPos != null -> {
                                    builder.appendBitmapWithAlignment(bitmap, diffusion, width, bothScale, rotate, aligmentPos)
                                }
                                else -> {
                                    builder.appendBitmap(bitmap, diffusion, width, bothScale, rotate)
                                }
                            }

                        } else {

                            when {
                                position != null -> {
                                    builder.appendBitmapWithAbsolutePosition(bitmap, diffusion!!, rotate, position)
                                }
                                aligmentPos != null -> {
                                    builder.appendBitmapWithAlignment(bitmap, diffusion!!, rotate, aligmentPos)
                                }
                                else -> {
                                    builder.appendBitmap(bitmap, diffusion!!, rotate)
                                }
                            }

                        }
                    } catch (e: IOException) {

                    }

                }
                "appendPrintableArea" -> {
                    var codePrintArea = getPrintableArea((commandValue as String))
                    Log.d(logTag, "appendPrintableArea: codePage $codePrintArea")
                    builder.appendPrintableArea(codePrintArea)
                }

            }
        }


    }

    private fun createBitmapFromText(printText: String, textSize: Int, printWidth: Int, typeface: Typeface?): Bitmap? {
        val paint = Paint()
        val bitmap: Bitmap
        paint.textSize = textSize.toFloat()
        paint.typeface = typeface
        paint.getTextBounds(printText, 0, printText.length, Rect())
        val textPaint = TextPaint(paint)
        val staticLayout = StaticLayout(printText, textPaint, printWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0F, false)

        // Create bitmap
        bitmap = Bitmap.createBitmap(staticLayout.width, staticLayout.height, Bitmap.Config.ARGB_8888)

        // Create canvas
        val canvas: Canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.translate(0f, 0f)
        staticLayout.draw(canvas)
        return bitmap
    }

    private fun getCapturedImage(selectedPhotoUri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(applicationContext.contentResolver, selectedPhotoUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            getBitmap(applicationContext.contentResolver, selectedPhotoUri)
        }

    }

    private fun getEmulation(emulation: String?): Emulation {
        return when (emulation) {
            "StarPRNT" -> Emulation.StarPRNT
            "StarPRNTL" -> Emulation.StarPRNTL
            "StarLine" -> Emulation.StarLine
            "StarGraphic" -> Emulation.StarGraphic
            "EscPos" -> Emulation.EscPos
            "EscPosMobile" -> Emulation.EscPosMobile
            "StarDotImpact" -> Emulation.StarDotImpact
            else -> Emulation.StarLine
        }
    }

    private fun convertBase64toBitmap(base64Str: String): Bitmap? {
        val decodedBytes: ByteArray = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }


    private fun convertByteArrayToBitmap(src: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(src, 0, src.size)
    }

    private fun getBitmapConverterRotation(code: String?): ICommandBuilder.BitmapConverterRotation {
        if (code == null) return ICommandBuilder.BitmapConverterRotation.Normal
        return try {
            ICommandBuilder.BitmapConverterRotation.valueOf(code)
        } catch (e: IllegalArgumentException) {
            ICommandBuilder.BitmapConverterRotation.Normal
        }
    }

    private fun getBarcodeWidth(code: String): ICommandBuilder.BarcodeWidth {
        return try {
            ICommandBuilder.BarcodeWidth.valueOf(code)
        } catch (e: IllegalArgumentException) {
            ICommandBuilder.BarcodeWidth.Mode1
        }
    }

    private fun getPrintableArea(code: String): ICommandBuilder.PrintableAreaType {
        return try {
            ICommandBuilder.PrintableAreaType.valueOf(code)
        } catch (e: IllegalArgumentException) {
            ICommandBuilder.PrintableAreaType.Standard
        }
    }

    private fun getBarcodeSymbology(code: String): ICommandBuilder.BarcodeSymbology {
        return try {
            ICommandBuilder.BarcodeSymbology.valueOf(code)
        } catch (e: IllegalArgumentException) {
            ICommandBuilder.BarcodeSymbology.Code128
        }
    }

    private fun getPeripheralChannel(code: String): ICommandBuilder.PeripheralChannel {
        return try {
            ICommandBuilder.PeripheralChannel.valueOf(code)
        } catch (e: IllegalArgumentException) {
            ICommandBuilder.PeripheralChannel.No1
        }
    }

    private fun getSoundChannel(code: String): ICommandBuilder.SoundChannel {
        return try {
            ICommandBuilder.SoundChannel.valueOf(code)
        } catch (e: IllegalArgumentException) {
            ICommandBuilder.SoundChannel.No1
        }
    }

    private fun getCutPaper(code: String): ICommandBuilder.CutPaperAction {
        return try {
            ICommandBuilder.CutPaperAction.valueOf(code)
        } catch (e: IllegalArgumentException) {
            ICommandBuilder.CutPaperAction.PartialCut
        }
    }

    private fun getCodepage(codepage: String): ICommandBuilder.CodePageType {
        return try {
            ICommandBuilder.CodePageType.valueOf(codepage)
        } catch (e: IllegalArgumentException) {
            ICommandBuilder.CodePageType.UTF8
        }
    }

    private fun getAlignment(code: String?): ICommandBuilder.AlignmentPosition {
        if (code == null)
            return ICommandBuilder.AlignmentPosition.Left
        return try {
            ICommandBuilder.AlignmentPosition.valueOf(code)
        } catch (e: IllegalArgumentException) {
            ICommandBuilder.AlignmentPosition.Left
        }
    }

    private fun getInternational(code: String): ICommandBuilder.InternationalType {
        return try {
            ICommandBuilder.InternationalType.valueOf(code)
        } catch (e: IllegalArgumentException) {
            ICommandBuilder.InternationalType.Legal
        }
    }

    private fun getPortSettingsOption(emulation: String): String {
        when (emulation) {
            "EscPosMobile" -> return "mini"
            "EscPos" -> return "escpos"
            "StarPRNT", "StarPRNTL" -> return "Portable"
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
