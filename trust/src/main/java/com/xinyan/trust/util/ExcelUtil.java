package com.xinyan.trust.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;

public class ExcelUtil {
    private HSSFWorkbook wb;


    /**
     * 创建一个单元格
     */
    public void setCell() {
        //获得一个工作薄
        wb = new HSSFWorkbook();
        //第一个sheet
        Sheet sheet = wb.createSheet("第一个sheet");
        for (int i = 0; i < 10; i++) {

            //创建第一行
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            //获取第一个单元格(第一列)
            Cell cell = row.createCell(5);
            //给第一个单元格赋值
            cell.setCellValue("第一个单元格的值 \n 我是下一行");

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setWrapText(true);
            cell.setCellStyle(cellStyle);

            row.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());

            sheet.autoSizeColumn(2);

        }
    }

    public void downloadExcel() {

        try {
            File file = new File("D://test.xls");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            wb.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExcelUtil excelutil = new ExcelUtil();
        excelutil.setCell();
        excelutil.downloadExcel();

    }
}
