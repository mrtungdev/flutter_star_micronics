enum StarMicronicsDiscoveryType { TCP, BLUETOOTH }

/// A:	Font-A (12 x 24 dots) / Specify 7 x 9 font (half dots)
/// B:	Font-B (9 x 24 dots) / Specify 5 x 9 font (2P-1)
enum StarFontStyleType { A, B }

enum StarEncoding {
  USASCII,
  Windows1252,
  ShiftJIS,
  Windows1251,
  GB2312,
  Big5,
  UTF8
}

/// CP437: CodePage 437 (USA, Std. Europe)
/// CP737: Codepage 737 (Greek)
/// CP772: Codepage 772 (Lithuanian)
/// CP774: Codepage 774 (Lithuanian)
/// CP851: Codepage 851 (Greek)
/// CP852: Codepage 852 (Latin-2)
/// CP855: Codepage 855 (Cyrillic Bulgarian)
/// CP857: Codepage 857 (Turkey)
/// CP858: Codepage 858 (Multilingual)
/// CP860: Codepage 860 (Portuguese)
/// CP861: Codepage 861 (Icelandic)
/// CP862: Codepage 862 (Israel (Hebrew))
/// CP863: Codepage 863 (Canadian French)
/// CP864: Codepage 864 (Arabic)
/// CP865: Codepage 865 (Nordic)
/// CP866: Codepage 866 (Cyrillic Russian)
/// CP869: Codepage 869 (Greek)
/// CP874: Codepage 874 (Thai)
/// CP928: Codepage 928 (Greek)
/// CP932: Katakana
/// CP998: Normal
/// CP999: Codepage 1252 (Windows Latin-1)
/// CP1001: Codepage 1001 (Arabic)
/// CP1250: Codepage 1250 (Windows Latin-2)
/// CP1251: Codepage 1251 (Windows Cyrillic)
/// CP1252: Codepage 1252 (Windows Latin-1)
/// CP2001: Codepage 2001 (Lithuanian-KBL)
/// CP3001: Codepage 3001 (Estonian-1)
/// CP3002: Codepage 3002 (Estonian-2)
/// CP3011: Codepage 3011 (Latvian-1)
/// CP3012: Codepage 3012 (Latvian-2)
/// CP3021: Codepage 3021 (Bulgarian)
/// CP3041: Codepage 3041 (Maltese)
/// CP3840: Codepage 3840 (IBM-Russian)
/// CP3841: Codepage 3841 (Gost)
/// CP3843: Codepage 3843 (Polish)
/// CP3844: Codepage 3844 (CS2)
/// CP3845: Codepage 3845 (Hungarian)
/// CP3846: Codepage 3846 (Turkish)
/// CP3847: Codepage 3847 (Brazil-ABNT)
/// CP3848: Codepage 3848 (Brazil-ABICOMP)
/// UTF8: UTF-8
/// Blank: User Setting Blank Code Page
enum StarCodePageType {
  CP437,
  CP737,
  CP772,
  CP774,
  CP851,
  CP852,
  CP855,
  CP857,
  CP858,
  CP860,
  CP861,
  CP862,
  CP863,
  CP864,
  CP865,
  CP866,
  CP869,
  CP874,
  CP928,
  CP932,
  CP998,
  CP999,
  CP1001,
  CP1250,
  CP1251,
  CP1252,
  CP2001,
  CP3001,
  CP3002,
  CP3011,
  CP3012,
  CP3021,
  CP3041,
  CP3840,
  CP3841,
  CP3843,
  CP3844,
  CP3845,
  CP3846,
  CP3847,
  CP3848,
  UTF8,
  Blank
}

enum StarInternationalType {
  USA,
  France,
  Germany,
  UK,
  Denmark,
  Sweden,
  Italy,
  Spain,
  Japan,
  Norway,
  Denmark2,
  Spain2,
  LatinAmerica,
  Korea,
  Ireland,
  Legal
}

enum StarAlignmentPosition { Left, Center, Right }

/// Paper cut constants
///
/// FullCut: Full Cut
/// PartialCut: Partial Cut
/// FullCutWithFeed: Full Cut with Feed
/// PartialCutWithFeed: Partial Cut with Feed
enum StarCutPaperAction {
  FullCut,
  PartialCut,
  FullCutWithFeed,
  PartialCutWithFeed
}

/// Cash drawer channel constants
///
/// No1: Channel1
/// No2: Channel2
enum StarPeripheralChannel { No1, No2 }

/// Sound/Buzzer channel constants
/// No1: Channel1
/// No2: Channel2
enum StarSoundChannel { No1, No2 }

enum StarBarcodeSymbology {
  UPCE,
  UPCA,
  JAN8,
  JAN13,
  Code39,
  ITF,
  Code128,
  Code93,
  NW7
}

enum StarBarcodeWidth {
  Mode1,
  Mode2,
  Mode3,
  Mode4,
  Mode5,
  Mode6,
  Mode7,
  Mode8,
  Mode9,
  ExtMode1,
  ExtMode2,
  ExtMode3,
  ExtMode4,
  ExtMode5,
  ExtMode6,
  ExtMode7,
  ExtMode8,
  ExtMode9
}

enum StarQrCodeLevel { L, M, Q, H }

enum StarBitmapConverterRotation { Normal, Right90, Left90, Rotate180 }

enum StarBlackMarkType { Invalid, Valid, ValidWithDetection }

enum StarPrintableAreaType { Standard, Type1, Type2, Type3, Type4 }

// const Map<String, String> StartCodePageType = {
//   "CP437": "CodePage 437 (USA, Std. Europe)",
//   "CP737": "Codepage 737 (Greek)",
//   "CP772": "Codepage 772 (Lithuanian)",
//   "CP774": "Codepage 774 (Lithuanian)",
//   "CP851": "Codepage 851 (Greek)",
//   "CP852": "Codepage 852 (Latin-2)",
//   "CP855": "Codepage 855 (Cyrillic Bulgarian)",
//   "CP857": "Codepage 857 (Turkey)",
//   "CP858": "Codepage 858 (Multilingual)",
//   "CP860": "Codepage 860 (Portuguese)",
//   "CP861": "Codepage 861 (Icelandic)",
//   "CP862": "Codepage 862 (Israel (Hebrew))",
//   "CP863": "Codepage 863 (Canadian French)",
//   "CP864": "Codepage 864 (Arabic)",
//   "CP865": "Codepage 865 (Nordic)",
//   "CP866": "Codepage 866 (Cyrillic Russian)",
//   "CP869": "Codepage 869 (Greek)",
//   "CP874": "Codepage 874 (Thai)",
//   "CP928": "Codepage 928 (Greek)",
//   "CP932": "Katakana",
//   "CP998": "Normal",
//   "CP999": "Codepage 1252 (Windows Latin-1)",
//   "CP1001": "Codepage 1001 (Arabic)",
//   "CP1250": "Codepage 1250 (Windows Latin-2)",
//   "CP1251": "Codepage 1251 (Windows Cyrillic)",
//   "CP1252": "Codepage 1252 (Windows Latin-1)",
//   "CP2001": "Codepage 2001 (Lithuanian-KBL)",
//   "CP3001": "Codepage 3001 (Estonian-1)",
//   "CP3002": "Codepage 3002 (Estonian-2)",
//   "CP3011": "Codepage 3011 (Latvian-1)",
//   "CP3012": "Codepage 3012 (Latvian-2)",
//   "CP3021": "Codepage 3021 (Bulgarian)",
//   "CP3041": "Codepage 3041 (Maltese)",
//   "CP3840": "Codepage 3840 (IBM-Russian)",
//   "CP3841": "Codepage 3841 (Gost)",
//   "CP3843": "Codepage 3843 (Polish)",
//   "CP3844": "Codepage 3844 (CS2)",
//   "CP3845": "Codepage 3845 (Hungarian)",
//   "CP3846": "Codepage 3846 (Turkish)",
//   "CP3847": "Codepage 3847 (Brazil-ABNT)",
//   "CP3848": "Codepage 3848 (Brazil-ABICOMP)",
//   "UTF8": "UTF-8",
//   "Blank": "User Setting Blank Code Page",
// };
