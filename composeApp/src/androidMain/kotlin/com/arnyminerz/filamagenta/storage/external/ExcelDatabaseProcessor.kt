package com.arnyminerz.filamagenta.storage.external

import io.github.aakira.napier.Napier
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelDatabaseProcessor : ExternalDatabaseProcessor {
    private fun Cell.getNumberLenient(): Double? {
        return try {
            numericCellValue
        } catch (_: IllegalStateException) {
            stringCellValue.takeIf { it.isNotBlank() }?.toDoubleOrNull()
        }?.takeIf { it > 0.0 }
    }

    private fun Cell.isNumeric(): Boolean {
        return (cellType == CellType.NUMERIC && numericCellValue > 0.0) ||
                (cellType == CellType.STRING && stringCellValue.toIntOrNull() != null) ||
                (cellType == CellType.FORMULA && getNumberLenient() != null)
    }

    override fun process(data: ByteArray): ExternalDatabaseResult {
        val orders = mutableListOf<ExternalOrder>()
        val warnings = mutableListOf<ExternalDatabaseWarning>()

        data.inputStream().use { stream ->
            val opcPackage = OPCPackage.open(stream)
            val workbook = XSSFWorkbook(opcPackage)
            val festersSheet = workbook.getSheet("FESTERS")

            var isNextRowTotals = false
            var totals: Int? = null

            // First obtain totals
            // Find the row with "TOTALES GENERALES"
            for (row in festersSheet) {
                for (cell in row) {
                    if (cell.cellType == CellType.STRING && cell.stringCellValue == "TOTALES GENERALES") {
                        isNextRowTotals = true
                    } else if (isNextRowTotals) {
                        Napier.i { "Totals: ${cell.numericCellValue.toInt()}" }
                        totals = cell.numericCellValue.toInt()
                        isNextRowTotals = false
                    }
                }
            }

            var isMemberRow = false

            var tempName: String? = null
            var tempOrder: String? = null
            var tempPhone: String? = null

            // Now get all the data
            for (sheet in workbook) {
                for (row in sheet) {
                    for ((cellIndex, cell) in row.withIndex()) {
                        val value = when (cell.cellType) {
                            CellType.NUMERIC -> cell.numericCellValue.toInt().toString()
                            CellType.STRING -> cell.stringCellValue
                            CellType.FORMULA -> {
                                try {
                                    cell.numericCellValue.toInt().toString()
                                } catch (_: IllegalStateException) {
                                    cell.stringCellValue
                                }
                            }

                            CellType.BOOLEAN -> cell.booleanCellValue.toString()
                            CellType.ERROR -> cell.errorCellValue.toString()
                            CellType.BLANK -> null
                            CellType._NONE -> null
                            null -> null
                        }
                        if (cellIndex == 0 && cell.isNumeric()) {
                            isMemberRow = true
                        } else if (isMemberRow) {
                            when (cellIndex) {
                                1 -> tempName = value
                                3 -> tempOrder = value?.takeIf { it.isNotBlank() }
                                5 -> tempPhone = value
                            }
                        }
                    }

                    if (tempName != null && tempOrder != null && tempPhone != null) {
                        orders.add(
                            ExternalOrder(tempName, tempOrder, tempPhone)
                        )
                    }

                    tempName = null
                    tempOrder = null
                    tempPhone = null
                    isMemberRow = false
                }
            }
            Napier.i { "Got ${orders.size} orders." }

            for (order in orders) {
                Napier.d {
                    "- ${order.order} :: ${order.name} (${order.phone})"
                }
            }

            if (orders.size != totals) {
                Napier.w { "There are ${orders.size} orders, but totals specify $totals" }
                warnings.add(ExternalDatabaseWarning.InconsistentCount)
            }
        }

        if (orders.isEmpty()) {
            Napier.w { "Processing did not find any orders." }
            warnings.add(ExternalDatabaseWarning.Empty)
        }

        return ExternalDatabaseResult(orders, warnings)
    }
}
