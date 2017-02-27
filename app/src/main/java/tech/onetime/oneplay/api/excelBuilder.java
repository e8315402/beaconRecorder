package tech.onetime.oneplay.api;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.IntRange;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;


/**
 * Created by JianFa on 2017/2/24
 */

public class excelBuilder {

    public static final String TAG = "excelBuilder";

    private static Workbook _wb = null;

    private static String _currentSheetName = "1M";
    private static int _currentRowIndex = 1;
    private static int _currentCellIndex = 1;

    private static int[] distances = {1, 2, 3, 5, 8, 10, 20, 30, 40, 50};

    public static void initExcel() {

        if (_wb == null) {
            _wb = new HSSFWorkbook();

            _wb.createSheet("1M");
            setHeaderOfSheet("1M");

            _wb.createSheet("10M");
            setHeaderOfSheet("10M");

            _wb.createSheet("20M");
            setHeaderOfSheet("20M");

            _wb.createSheet("50M");
            setHeaderOfSheet("50M");
        }

    }

    public static void clearExcel() {

        _wb = null;

    }

    public static void setCurrentSheet(String sheetName) {

        if(_wb == null) initExcel();

        if( _wb.getSheet(sheetName) != null) {

            if(sheetName.compareTo(_currentSheetName) == 0) return;

            _currentSheetName = sheetName;

        } else Log.e(TAG, "setCurrentSheet __ Illegal sheet name, must be 1M, 10M, 20M or 50M.");

    }

    public static void setCellByRowInOrder(String content) {

        Sheet shTemp = _wb.getSheet(_currentSheetName);

        Row rowTemp = shTemp.getRow(_currentRowIndex);

        Cell cellTemp = rowTemp.createCell(_currentCellIndex);

        cellTemp.setCellValue(content);

//        Log.d(TAG, "put value : " + Integer.toString(content));

        _currentCellIndex++;

    }

    public static void setCellByRowInOrder(int content) {

        Sheet shTemp = _wb.getSheet(_currentSheetName);

        Row rowTemp = shTemp.getRow(_currentRowIndex);

        Cell cellTemp = rowTemp.createCell(_currentCellIndex);

        cellTemp.setCellValue(content);

//        Log.d(TAG, "put value : " + Integer.toString(content));

        _currentCellIndex++;

    }

    public static void setCurrentRowByDistance(int distance) {

        int rowIndex = 0;

        while(rowIndex < 10) {
            if(distances[rowIndex] == distance) break;
            rowIndex++;
        }

        if(rowIndex == 10) Log.e(TAG, "setCurrentRowByDistance __ distance is not exist.");

        _currentRowIndex = rowIndex + 1;

    }

    public static void setCurrentCell(int cellIndex){

        _currentCellIndex = cellIndex;

    }

    public static boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            _wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }

    @Deprecated
    public static void readExcelFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return;
        }

        try{
            // Creating Input Stream
            File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                while(cellIter.hasNext()){
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d(TAG, "Cell Value: " +  myCell.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static void setHeaderOfSheet(String sheetName) {

        Sheet shTemp = _wb.getSheet(sheetName);

        Row rowTemp = shTemp.createRow(0);
        for(int cellIndex = 1 ; cellIndex <= 100 ; cellIndex++ )
            rowTemp.createCell(cellIndex).setCellValue(cellIndex);

        for(int rowIndex = 0 ; rowIndex < 10 ; rowIndex++ )
            shTemp.createRow(rowIndex + 1).createCell(0).setCellValue(distances[rowIndex]);

    }

}
