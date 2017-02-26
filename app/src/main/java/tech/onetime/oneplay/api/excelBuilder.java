package tech.onetime.oneplay.api;

import android.content.Context;
import android.os.Environment;
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
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by JianFa on 2017/2/24
 */

public class excelBuilder {

    public static final String TAG = "excelBuilder";

    private static Workbook _wb = null;
    private static HashMap<String, Sheet> _sheetHashMap = null;

    private static String _currentSheetName = null;
    private static int _currentRowIndex = 1;
    private static int _currentCellIndex = 1;

    private static boolean _fileSaved = true;

    @Deprecated
    public static void createWorkbook() {
        if (_wb == null) _wb = new HSSFWorkbook();
        _fileSaved = false;
    }

    public static void createNewSheet(String sheetName) {

        if(_wb == null) _wb = new HSSFWorkbook();

        if (_sheetHashMap == null) _sheetHashMap = new HashMap<>();

        _sheetHashMap.put(sheetName, _wb.createSheet(sheetName));

        _currentSheetName = sheetName;

        _fileSaved = false;

    }

    public static void nextRow(){

        Sheet shTemp = _sheetHashMap.get(_currentSheetName);

        if (shTemp == null) {
            Log.e(TAG, "Sheet is not exist. You must create a new sheet first.");
            return;
        }

        _currentRowIndex++;

        shTemp.createRow(_currentRowIndex);

    }

    public static void setCellByRowInOrder(String content) {

        Sheet shTemp = _sheetHashMap.get(_currentSheetName);

        if (shTemp == null) {
            Log.e(TAG, "Sheet is not exist. You must create a new sheet first.");
            return;
        }

        Row rowTemp = shTemp.createRow(_currentRowIndex);

        Cell cellTemp = rowTemp.createCell(_currentCellIndex);

        cellTemp.setCellValue(content);

        _currentCellIndex++;

    }

    public static void setCellByRowInOrder(int content) {

        Sheet shTemp = _sheetHashMap.get(_currentSheetName);

        if (shTemp == null) {
            Log.e(TAG, "Sheet is not exist. You must create a new sheet first.");
            return;
        }

        Row rowTemp = shTemp.getRow(_currentRowIndex);

        if(rowTemp == null) rowTemp = shTemp.createRow(_currentRowIndex);

        Cell cellTemp = rowTemp.createCell(_currentCellIndex);

        cellTemp.setCellValue(content);

//        Log.d(TAG, "put value : " + Integer.toString(content));

        _currentCellIndex++;

    }

    public static int getCurrentRowIndex() {
        return _currentRowIndex;
    }

    public static int getCurrentCellIndex() {
        return _currentCellIndex;
    }

    public static boolean isFileSaved() {
        return _fileSaved;
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
            _fileSaved = true;
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


}
