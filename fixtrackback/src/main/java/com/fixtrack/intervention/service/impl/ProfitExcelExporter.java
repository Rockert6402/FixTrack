package com.fixtrack.intervention.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.fixtrack.intervention.dto.report.OrderProfit;
import com.fixtrack.intervention.dto.report.ProfitReport;
import com.fixtrack.intervention.dto.report.ProfitTotals;
import com.fixtrack.intervention.dto.report.TechnicianProfit;

// Informe de ganancias en Excel: hoja de resumen (totales y por técnico) y hoja con el detalle por orden
@Component
public class ProfitExcelExporter {

	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public byte[] export(ProfitReport report) {
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Styles styles = new Styles(workbook);
			writeSummary(workbook.createSheet("Resumen"), report, styles);
			writeOrders(workbook.createSheet("Órdenes"), report.orders(), styles);
			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException("No se pudo generar el archivo de Excel", e);
		}
	}

	private void writeSummary(Sheet sheet, ProfitReport report, Styles styles) {
		int rowIndex = 0;
		text(sheet.createRow(rowIndex++), 0, "Informe de ganancias", styles.title);
		text(sheet.createRow(rowIndex++), 0,
				"Órdenes entregadas del " + DATE.format(report.from()) + " al " + DATE.format(report.to()), null);
		rowIndex++;

		ProfitTotals totals = report.totals();
		rowIndex = pair(sheet, rowIndex, "Órdenes entregadas", totals.orders(), styles);
		rowIndex = pair(sheet, rowIndex, "Garantías", totals.warranties(), styles);
		rowIndex = money(sheet, rowIndex, "Total cobrado", totals.revenue(), styles);
		rowIndex = money(sheet, rowIndex, "Costo de repuestos", totals.partsCost(), styles);
		rowIndex = money(sheet, rowIndex, "Costo de garantías (incluido)", totals.warrantyCost(), styles);
		rowIndex = money(sheet, rowIndex, "Ganancia", totals.profit(), styles);
		rowIndex++;

		Row header = sheet.createRow(rowIndex++);
		String[] headings = { "Técnico", "Órdenes", "Garantías", "Total cobrado", "Costo de repuestos",
				"Costo de garantías", "Ganancia" };
		for (int i = 0; i < headings.length; i++) {
			text(header, i, headings[i], styles.header);
		}
		for (TechnicianProfit technician : report.byTechnician()) {
			Row row = sheet.createRow(rowIndex++);
			text(row, 0, technician.technicianName(), null);
			number(row, 1, technician.orders());
			number(row, 2, technician.warranties());
			amount(row, 3, technician.revenue(), styles);
			amount(row, 4, technician.partsCost(), styles);
			amount(row, 5, technician.warrantyCost(), styles);
			amount(row, 6, technician.profit(), styles);
		}
		widths(sheet, 30, 12, 12, 18, 20, 20, 18);
	}

	private void writeOrders(Sheet sheet, List<OrderProfit> orders, Styles styles) {
		Row header = sheet.createRow(0);
		String[] headings = { "Orden", "Entregada", "Cliente", "Técnico", "Garantía", "Total cobrado",
				"Costo de repuestos", "Ganancia" };
		for (int i = 0; i < headings.length; i++) {
			text(header, i, headings[i], styles.header);
		}
		int rowIndex = 1;
		for (OrderProfit order : orders) {
			Row row = sheet.createRow(rowIndex++);
			text(row, 0, order.code(), null);
			Cell delivered = row.createCell(1);
			delivered.setCellValue(order.deliveredAt());
			delivered.setCellStyle(styles.dateTime);
			text(row, 2, order.customerName(), null);
			text(row, 3, order.technicianName(), null);
			text(row, 4, order.warranty() ? "Sí" : "No", null);
			amount(row, 5, order.revenue(), styles);
			amount(row, 6, order.partsCost(), styles);
			amount(row, 7, order.profit(), styles);
		}
		sheet.createFreezePane(0, 1);
		widths(sheet, 16, 18, 30, 26, 10, 18, 20, 18);
	}

	private int pair(Sheet sheet, int rowIndex, String label, long value, Styles styles) {
		Row row = sheet.createRow(rowIndex);
		text(row, 0, label, styles.header);
		number(row, 1, value);
		return rowIndex + 1;
	}

	private int money(Sheet sheet, int rowIndex, String label, BigDecimal value, Styles styles) {
		Row row = sheet.createRow(rowIndex);
		text(row, 0, label, styles.header);
		amount(row, 1, value, styles);
		return rowIndex + 1;
	}

	private void text(Row row, int column, String value, CellStyle style) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		if (style != null) {
			cell.setCellStyle(style);
		}
	}

	private void number(Row row, int column, long value) {
		row.createCell(column).setCellValue(value);
	}

	private void amount(Row row, int column, BigDecimal value, Styles styles) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value.doubleValue());
		cell.setCellStyle(styles.money);
	}

	private void widths(Sheet sheet, int... characters) {
		for (int i = 0; i < characters.length; i++) {
			sheet.setColumnWidth(i, characters[i] * 256);
		}
	}

	// Estilos compartidos: título, encabezados en negrita, pesos colombianos y fecha con hora
	private static final class Styles {

		private final CellStyle title;
		private final CellStyle header;
		private final CellStyle money;
		private final CellStyle dateTime;

		private Styles(Workbook workbook) {
			DataFormat format = workbook.createDataFormat();
			Font bold = workbook.createFont();
			bold.setBold(true);
			Font big = workbook.createFont();
			big.setBold(true);
			big.setFontHeightInPoints((short) 14);
			title = workbook.createCellStyle();
			title.setFont(big);
			header = workbook.createCellStyle();
			header.setFont(bold);
			money = workbook.createCellStyle();
			money.setDataFormat(format.getFormat("\"$\" #,##0;[Red]-\"$\" #,##0"));
			dateTime = workbook.createCellStyle();
			dateTime.setDataFormat(format.getFormat("dd/mm/yyyy hh:mm"));
		}
	}
}
