package kitty.kaf.excel;

import java.util.ArrayList;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

public class ExcelSheet {
	ArrayList<ArrayList<ExcelCell>> rows = new ArrayList<ArrayList<ExcelCell>>();

	public ArrayList<ExcelCell> add() {
		ArrayList<ExcelCell> r = new ArrayList<ExcelCell>();
		rows.add(r);
		return r;
	}

	public ArrayList<ArrayList<ExcelCell>> getRows() {
		return rows;
	}

	public void write(WritableSheet sheet, int widths[]) throws WriteException {
		sheet.getSettings().setDefaultColumnWidth(13);
		for (int i = 0; i < widths.length; i++)
			sheet.setColumnView(i, widths[i]);
		for (ArrayList<ExcelCell> r : rows) {
			for (ExcelCell f : r) {
				WritableCellFormat format;
				if (f.getFormat() != null)
					format = (WritableCellFormat) f.getFormat();
				else {
					format = new WritableCellFormat();
					format.setFont(new WritableFont(WritableFont.createFont(f
							.getFontName()), f.getFontSize()));
					switch (f.getAlignment()) {
					case ExcelCell.LEFT:
						format.setAlignment(Alignment.LEFT);
						break;
					case ExcelCell.RIGHT:
						format.setAlignment(Alignment.RIGHT);
						break;
					default:
						format.setAlignment(Alignment.CENTRE);
					}
					if (f.getBorder() > 0)
						format.setBorder(Border.ALL,
								BorderLineStyle.getStyle(f.getBorder()));
				}
				Label cell = new Label(f.getCol(), f.getRow(),
						f.getValue() == null ? "" : f.getValue().toString());
				cell.setCellFormat(format);
				if (f.getMergeCols() > 0 || f.getMergeRows() > 0)
					sheet.mergeCells(f.getCol(), f.getRow(),
							f.getCol() + f.getMergeCols(),
							f.getRow() + f.getMergeRows());
				sheet.addCell(cell);
			}
		}
	}
}
