package com.eunan.tracey.etimefinalyearproject;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WriteExcel {

    private static final String EXCEL_FILE_LOCATION = "C:\\temp\\MyFirstExcel.xls";


    public static void main(String[] args) {
        //1. Create an Excel file
        WritableWorkbook myFirstWbook = null;
        try {

            myFirstWbook = Workbook.createWorkbook(new File(EXCEL_FILE_LOCATION));

            // create an Excel sheet
            WritableSheet excelSheet = myFirstWbook.createSheet("InvoiceActivity", 0);

            // add something into the Excel sheet
            Label label = new Label(0, 0, "InvoiceActivity");
            excelSheet.addCell(label);

            Number number = new Number(0, 1, 1);
            excelSheet.addCell(number);

            label = new Label(1, 0, "Project");
            excelSheet.addCell(label);

            label = new Label(2, 0, "Days");
            excelSheet.addCell(label);

            label = new Label(3, 0, "Hours");
            excelSheet.addCell(label);

            label = new Label(4, 0, "Total");
            excelSheet.addCell(label);

            label = new Label(1, 1, "Belfast");
            excelSheet.addCell(label);

            label = new Label(2, 1, "5");
            excelSheet.addCell(label);

            label = new Label(3, 1, "40");
            excelSheet.addCell(label);

            label = new Label(4, 1, "200");
            excelSheet.addCell(label);

            myFirstWbook.write();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {

            if (myFirstWbook != null) {
                try {
                    myFirstWbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }


        }

    }
}