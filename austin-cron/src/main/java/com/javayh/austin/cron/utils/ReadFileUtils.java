package com.javayh.austin.cron.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRowHandler;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import com.google.common.base.Throwables;
import com.javayh.austin.cron.csv.CountFileRowHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 读取人群文件 工具类
 * 
 * @author yh
 */
@Slf4j
public class ReadFileUtils {

    /**
     * csv文件 存储 接收者 的列名
     */
    public static final String RECEIVER_KEY = "userId";

    private ReadFileUtils() {
    }

    /**
     * 读取csv文件，每读取一行都会调用 csvRowHandler 对应的方法
     *
     * @param path
     * @param csvRowHandler
     */
    public static void getCsvRow(String path, CsvRowHandler csvRowHandler) {
        // 把首行当做是标题，获取reader
        try (CsvReader reader = CsvUtil.getReader(
                new InputStreamReader(Files.newInputStream(Paths.get(path)), CharsetUtil.CHARSET_UTF_8),
                new CsvReadConfig().setContainsHeader(true))) {
            reader.read(csvRowHandler);
        } catch (Exception e) {
            log.error("ReadFileUtils#getCsvRow fail!{}", Throwables.getStackTraceAsString(e));
        }
    }
    
    /**
     * 读取csv文件，获取文件里的行数
     *
     * @param path
     * @param countFileRowHandler
     */
    public static long countCsvRow(String path, CountFileRowHandler countFileRowHandler) {

        // 把首行当做是标题，获取reader
        try (CsvReader reader = CsvUtil.getReader(
                new InputStreamReader(new FileInputStream(path), CharsetUtil.CHARSET_UTF_8),
                new CsvReadConfig().setContainsHeader(true))) {

            reader.read(countFileRowHandler);
        } catch (Exception e) {
            log.error("ReadFileUtils#getCsvRow fail!{}", Throwables.getStackTraceAsString(e));
        }
        return countFileRowHandler.getRowSize();
    }
    
    /**
     * 从文件的每一行数据获取到params信息
     * [{key:value},{key:value}]
     *
     * @param fieldMap
     * @return
     */
    public static Map<String, String> getParamFromLine(Map<String, String> fieldMap) {
        HashMap<String, String> params = MapUtil.newHashMap();
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            if (!ReadFileUtils.RECEIVER_KEY.equals(entry.getKey())) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        return params;
    }
    
}
