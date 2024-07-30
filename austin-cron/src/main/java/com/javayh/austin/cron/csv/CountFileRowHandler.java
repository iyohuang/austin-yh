package com.javayh.austin.cron.csv;

import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvRowHandler;
import lombok.Data;

/**
 * @author yh
 */
@Data
public class CountFileRowHandler implements CsvRowHandler {
    private long rowSize;
    @Override
    public void handle(CsvRow csvRow) {
        rowSize++;
    }
    public long getRowSize(){
        return rowSize;
    }
}
