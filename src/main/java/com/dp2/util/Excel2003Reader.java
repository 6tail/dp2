package com.dp2.util;

import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * .xls格式文件读取，解决读取大文件引起内存疯涨溢出的问题
 *
 * @author 6tail
 *
 */
public class Excel2003Reader implements HSSFListener{
  /** YM格式 */
  private static final Set<String> YM_FORMATS = new HashSet<String>(){
    private static final long serialVersionUID = 1L;
    {
      add("mmmmm");
      add("mmm\\-yy");
      add("yyyy\"年\"m\"月\"");
    }
  };
  /** hms格式 */
  private static final Set<String> HMS_FORMATS = new HashSet<String>(){
    private static final long serialVersionUID = 1L;
    {
      add("h:mm");
      add("h\"时\"mm\"分\"");
    }
  };
  /** YMD格式 */
  private static final Set<String> YMD_FORMATS = new HashSet<String>(){
    private static final long serialVersionUID = 1L;
    {
      add("m/d/yy");
      add("m/d");
      add("mm/dd/yy");
      add("yy/m/d");
      add("yyyy/m/d");
      add("yyyy/mm/dd");
      add("d\\-mmm");
      add("\\ mmmm\\ dd\\,\\ yyyy");
      add("yyyy\"年\"m\"月\"d\"日\"");
      add("m\"月\"d\"日\"");
      add("aaaa");
      add("aaa");
    }
  };
  private final HSSFDataFormatter formatter = new HSSFDataFormatter();

  /** 每次读取的行数 */
  public static int queueSize = 5000;

  private File file;
  private boolean end;
  private boolean stop;
  private int sheetIndex = -1;
  private int eofCount = 0;
  private SSTRecord sstRecord;
  private FormatTrackingHSSFListener formatListener;
  private List<String> rowData = new ArrayList<String>();
  private Queue<List<String>> rowQueue = new LinkedBlockingQueue<List<String>>(queueSize);

  public Excel2003Reader(File file){
    this.file = file;
  }

  public void load() throws IOException {
    end = false;
    stop = false;
    sheetIndex = -1;
    MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
    formatListener = new FormatTrackingHSSFListener(listener);
    HSSFEventFactory factory = new HSSFEventFactory();
    HSSFRequest request = new HSSFRequest();
    request.addListenerForAllRecords(formatListener);
    POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
    factory.processWorkbookEvents(request, fs);
  }

  public List<String> nextLine(){
    if(stop){
      return null;
    }
    List<String> row = rowQueue.poll();
    while(null==row){
      if(stop||end){
        break;
      }
      try{
        Thread.sleep(2);
      }catch(InterruptedException ignore){}
      row = rowQueue.poll();
    }
    return row;
  }

  public void processRecord(Record record) {
    if(stop||end){
      return;
    }
    switch (record.getSid()){
      case BoundSheetRecord.sid:
        break;
      case BOFRecord.sid:
        BOFRecord bof = (BOFRecord)record;
        if(bof.getType() == BOFRecord.TYPE_WORKSHEET) {
          sheetIndex++;
          if(sheetIndex>0){
            end = true;
          }
        }
        break;
      case EOFRecord.sid:
        eofCount++;
        if(eofCount>1) {
          end = true;
        }
        break;
      case SSTRecord.sid:
        sstRecord = (SSTRecord) record;
        break;
      case BlankRecord.sid:
        //BlankRecord blank = (BlankRecord) record;
        rowData.add("");
        break;
      case BoolErrRecord.sid:
        //BoolErrRecord boolErr = (BoolErrRecord) record;
        rowData.add("");
        break;
      case FormulaRecord.sid:
        FormulaRecord formula = (FormulaRecord) record;
        if(!Double.isNaN(formula.getValue())){
          rowData.add(formatListener.formatNumberDateCell(formula));
        }
        break;
      case StringRecord.sid:
        StringRecord string = (StringRecord)record;
        rowData.add(string.getString());
        break;
      case LabelRecord.sid:
        LabelRecord label = (LabelRecord) record;
        rowData.add(label.getValue());
        break;
      case LabelSSTRecord.sid:
        LabelSSTRecord labelSST = (LabelSSTRecord) record;
        rowData.add(null==sstRecord?"":sstRecord.getString(labelSST.getSSTIndex()).toString());
        break;
      case NoteRecord.sid:
        //NoteRecord note = (NoteRecord) record;
        rowData.add("");
        break;
      case NumberRecord.sid:
        NumberRecord number = (NumberRecord) record;
        String formatString = formatListener.getFormatString(number);
        if(null==formatString){
          rowData.add(formatListener.formatNumberDateCell(number));
        }else {
          String convertFormatString = null;
          for (String format : YMD_FORMATS) {
            if (formatString.contains(format)) {
              convertFormatString = "yyyy-MM-dd";
              break;
            }
          }
          if(null==convertFormatString){
            for (String format : YM_FORMATS) {
              if (formatString.contains(format)) {
                convertFormatString = "yyyy-MM";
                break;
              }
            }
          }
          if(null==convertFormatString){
            for (String format : HMS_FORMATS) {
              if (formatString.contains(format)) {
                convertFormatString = "hh:mm:ss";
                break;
              }
            }
          }
          if(null!=convertFormatString) {
            int formatIndex = formatListener.getFormatIndex(number);
            rowData.add(formatter.formatRawCellContents(number.getValue(), formatIndex, convertFormatString));
          }else{
            rowData.add(formatListener.formatNumberDateCell(number));
          }
        }
        break;
      case RKRecord.sid:
        //RKRecord rk = (RKRecord) record;
        rowData.add("");
        break;
      default:
    }
    if(record instanceof MissingCellDummyRecord) {
      //MissingCellDummyRecord missingCellDummy = (MissingCellDummyRecord)record;
      rowData.add("");
    }
    if(record instanceof LastCellOfRowDummyRecord) {
      List<String> row = new ArrayList<String>(rowData.size());
      row.addAll(rowData);
      rowQueue.offer(row);
      rowData.clear();
    }
  }

  public void stop(){
    stop = true;
  }
}
