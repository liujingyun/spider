package com.xinyan.trust.util;

import com.xinyan.trust.entity.BaseBean;
import com.xinyan.trust.entity.ExcelBean;
import com.xinyan.trust.entity.ZiJinBean;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

public class ExcelUtil {
    private HSSFWorkbook wb;


    public static void setSheet(HSSFSheet sheet, ExcelBean excelBean){
        List<ZiJinBean> message = excelBean.getMessage();
        for(ZiJinBean bean : message){
            //创建第一行
            Row row = sheet.createRow(sheet.getLastRowNum()+1);
            Cell cell = row.createCell(1);
            cell.setCellValue(bean.getUrl());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(bean.getTitle());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(bean.getImageData());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(bean.getData());
        }
        Row row = sheet.createRow(sheet.getLastRowNum()+1);
        Cell cell = row.createCell(1);
        cell.setCellValue("创建时间：");
        Cell cell2 = row.createCell(2);
        cell2.setCellValue(new Date());

    }
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

}
