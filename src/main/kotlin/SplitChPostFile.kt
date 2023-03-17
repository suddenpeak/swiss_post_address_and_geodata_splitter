import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import kotlin.system.exitProcess

const val outFolder = "./out"
const val plzFile = "$outFolder/plzFile.csv"
const val plz2File = "$outFolder/plz2File.csv"
const val comFile = "$outFolder/comFile.csv"
const val strFile = "$outFolder/strFile.csv"
const val str2File = "$outFolder/str2File.csv"
const val gebFile = "$outFolder/gebFile.csv"
const val geb2File = "$outFolder/geb2File.csv"
const val geoFile = "$outFolder/geoFile.csv"
const val botFile = "$outFolder/botFile.csv"
const val hhFile = "$outFolder/hhFile.csv"
const val gebComFile = "$outFolder/gebComFile.csv"

var plzCount = 0
var plz2Count = 0
var comCount = 0
var strCount = 0
var str2Count = 0
var gebCount = 0
var geb2Count = 0
var botCount = 0
var geoCount = 0
var hhCount = 0
var gebComCount = 0

fun main(args: Array<String>) {
    if (args.isEmpty() || (args.size == 1 && args[0] == "-h")) {
        println("Usage: SplitChPostFile file-name")
        println("file-name will be prefixed with /src/main/resources/")
        exitProcess(0)
    } else {
        try {
            val scriptFile = args[0]
            val scriptPath = "${System.getProperty("user.dir")}/src/main/resources/$scriptFile"
            println("SplitChPostFile executing file $scriptFile")
            println("Path = $scriptPath")

            cleanOutFolder(outFolder, ".csv")
            createFile(plzFile, "REC_ART;ONRP;BFSNR;PLZ_TYP;PLZ;PLZ_ZZ;GPLZ;ORT_BEZ_18;ORT_BEZ_27;KANTON;SPRACHCODE;SPRACHCODE_ABW;BRIEFZ_DURCH;GILT_AB_DAT;PLZ_BRIEFZUST;PLZ_COFF")
            createFile(plz2File, "REC_ART;ONRP;LAUFNUMMER;BEZ_TYP;SPRACHCODE;ORT_BEZ_18;ORT_BEZ_27")
            createFile(comFile, "REC_ART;BFSNR;GEMEINDENAME;KANTON;AGGLONR")
            createFile(strFile, "REC_ART;STR_ID;ONRP;STR_BEZ_K;STR_BEZ_L;STR_BEZ_2K;STR_BEZ_2L;STR_LOK_TYP;STR_BEZ_SPC;STR_BEZ_COFF;STR_GANZFACH;STR_FACH_ONRP")
            createFile(str2File, "REC_ART;STR_ID_ALT;STR_ID;STR_TYP;STR_BEZ_AK;STR_BEZ_AL;STR_BEZ_A2K;STR_BEZ_A2L;STR_LOK_TYP;STR_BEZ_SPC")
            createFile(gebFile, "REC_ART;HAUSKEY;STR_ID;HNR;HNR_A;HNR_COFF;GANZFACH;FACH_ONRP")
            createFile(geb2File, "REC_ART;HAUSKEY_ALT;HAUSKEY;GEB_BEZ_ALT;GEB_TYP")
            createFile(geoFile, "REC_ART;HAUSKEY;RECHTS_KOORD;HOCH_KOORD;HOEHE_KOORD;GBDE_STATUS;STR_GEOPOST_ID;ADR_GEOPOST_ID;LAGE_BEZ")
            createFile(botFile, "REC_ART;HAUSKEY;A_PLZ;BBZ_PLZ;BOTEN_BBZ;ETAPPEN_NR;LAUF_NR;NDEPOT")
            createFile(hhFile, "REC_ART;HAUSKEY;HH_TOTAL;HH_ZUSTELL;HH_STOP;HH_EFH;ANZ_NAT_PERS;ANZ_FIRMEN")
            createFile(gebComFile, "REC_ART;HAUSKEY;BFSNR;GILT_AB")

            val fr = File(scriptPath).bufferedReader(StandardCharsets.ISO_8859_1)
            fr.use {
                val reader = CSVReaderBuilder(fr)
                    .withCSVParser(CSVParserBuilder().withSeparator(';').build())
                    .build()

                reader.use { r ->
                    var line = r.readNext()
                    while (line != null) {
                        when (line[0]) {
                            "00" -> newHeaRecord(line)
                            "01" -> newPlz1Record(line)
                            "02" -> newPlz2Record(line)
                            "03" -> newComRecord(line)
                            "04" -> newStrRecord(line)
                            "05" -> newStraRecord(line)
                            "06" -> newGebRecord(line)
                            "07" -> newGebaRecord(line)
                            "08" -> newBotBRecord(line)
                            "10" -> newGeoRecord(line)
                            "11" -> newHhRecord(line)
                            "12" -> newGebComRecord(line)
                            else -> {
                                print("Unknown record id ${line[0]}.")
                            }
                        }
                        line = r.readNext()
                    }
                }
            }
        } catch (ex: Exception) {
            println(ex.message)
        }
    }

    println("Finished processing file:")
    println(" PLZ: $plzCount")
    println(" PLZ2: $plz2Count")
    println(" COM: $comCount")
    println(" STR: $strCount")
    println(" STR2: $str2Count")
    println(" GEB: $gebCount")
    println(" GEB2: $geb2Count")
    println(" BOT: $botCount")
    println(" GEO: $geoCount")
    println(" HH: $hhCount")
    println(" GEBCOM: $gebComCount")
}

// Enthält das Versionsdatum und einen eindeutigen Zufallscode.
fun newHeaRecord(line: Array<String>) {
    println("NEW_HEA ${line[1]} ${line[2]}")
}

// Enthält alle für die Adressierung gültigen Postleitzahlen der Schweiz und des Fürstentums Liechtenstein.
fun newPlz1Record(line: Array<String>) {
    plzCount++
    appendToFile(plzFile, line.joinToString(";"))
}

// Enthält alternative Ortsbezeichnungen zur jeweiligen Postleitzahl.
fun newPlz2Record(line: Array<String>) {
    plz2Count++
    appendToFile(plz2File, line.joinToString(";"))
}

// Enthält die politischen Gemeinden der Schweiz und des Fürsten-tums Liechtenstein. Diese Daten stammen aus der offiziellen Liste des Bundesamtes für Statistik (BFS).
fun newComRecord(line: Array<String>) {
    comCount++
    appendToFile(comFile, line.joinToString(";"))
}

// Enthält alle Strassenbezeichnungen aller Ortschaften der Schweiz und des Fürstentums Liechtenstein.
fun newStrRecord(line: Array<String>) {
    strCount++
    appendToFile(strFile, line.joinToString(";"))
}

// Logische alternative oder fremdsprachige Strassenbezeichnung zur offiziellen Strassenbezeichnung. Gebäudebezeichnungen ohne Strasse/Hausnummer, Flur- oder Weilerbezeichnungen werden wie Strassennamen behandelt.
fun newStraRecord(line: Array<String>) {
    str2Count++
    appendToFile(str2File, line.joinToString(";"))
}

// Enthält Hausnummer und Hauskey.
fun newGebRecord(line: Array<String>) {
    gebCount++
    appendToFile(gebFile, line.joinToString(";"))
}

// Enthält alternative Hausbezeichnung und alternativen Hauskey.
fun newGebaRecord(line: Array<String>) {
    geb2Count++
    appendToFile(geb2File, line.joinToString(";"))
}

// Enthält Boteninformationen auf Stufe Hausnummer (Zustellung).
fun newBotBRecord(line: Array<String>) {
    botCount++
    appendToFile(botFile, line.joinToString(";"))
}

// Enthält Informationen zur Georeferenzierung von postalisch bedienten Gebäuden.
fun newGeoRecord(line: Array<String>) {
    geoCount++
    appendToFile(geoFile, line.joinToString(";"))
}

// Enthält Informationen zu Haushaltungen an einer Adresse.
fun newHhRecord(line: Array<String>) {
    hhCount++
    appendToFile(hhFile, line.joinToString(";"))
}

// Verknüpft Gebäude- und Gemeindeinformationen.
fun newGebComRecord(line: Array<String>) {
    gebComCount++
    appendToFile(gebComFile, line.joinToString(";"))
}

fun cleanOutFolder(folder: String, ext: String) {
    val dir = File(folder)
    val list = dir.listFiles { _, filename ->
        filename.endsWith(ext)
    }

    list?.let {
        list.forEach {
            println("Delete file ${it.name}...")
            it.delete()
        }
    }
}

fun createFile(fileName: String, line: String?) {
    val file = File(fileName)
    if (file.createNewFile()) {
        println("File $fileName created")
        line?.let {
            appendToFile(fileName, it)
        }
    } else {
        println("Error creating $fileName ...")
    }
}

fun appendToFile(fileName: String, line: String) {
    val file = File(fileName)
    FileOutputStream(file, true).bufferedWriter(StandardCharsets.ISO_8859_1).use { writer ->
        writer.write(line)
        writer.newLine()
    }
}
