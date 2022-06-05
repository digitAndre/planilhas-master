package br.com.utils.planilhas;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LeitorPlanilha<T> {
    public interface RowConverter<T> {
        T convert(Object[] row);
    }

    public static class Builder<T> {
        private boolean possuiCabecalho;
        private RowConverter<T> converter;
        private String aba;

        private char delimitador;

        public Builder() {
        }

        public Builder<T> converter(RowConverter<T> converter) {
            this.converter = converter;
            return this;
        }

        public Builder<T> comCabecalho() {
            this.possuiCabecalho = true;
            return this;
        }

        public Builder<T> aba(String name) {
            this.aba = name;
            return this;
        }

        public Builder<T> delimitadorCsv(char delimiter) {
            this.delimitador = delimiter;
            return this;
        }

        public LeitorPlanilha<T> build() {
            return new LeitorPlanilha<T>(this);
        }

    }

    private final Builder<T> info;

    public static <T> Builder<T> builder(Class<T> cls) {
        return new Builder<T>();
    }

    private LeitorPlanilha(Builder<T> info) {
        this.info = info;
    }

    public List<T> ler(String fileName) throws Exception {
        try (FileInputStream is = new FileInputStream(fileName)) {
            return ler(is);
        }
    }

    public List<T> ler(InputStream is) throws Exception {
        try (BufferedInputStream buf = new BufferedInputStream(is)) {
            if (isExcel(buf))
                return lerExcel(buf);
            return lerCsv(buf, info.delimitador, 0);
        }
    }

    private List<T> lerExcel(InputStream is) throws Exception {
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheet(info.aba);
        return extrairAba(sheet);
    }

    private List<T> lerCsv(InputStream in, char delimitador, int qtdTentativas) throws Exception {
        List<T> objList = new ArrayList<>();
        InputStreamReader isr = new InputStreamReader(in);
        try {
            CSVReader cvsr = new CSVReader(isr, delimitador);
            List<String[]> allRows = cvsr.readAll();
            int start = info.possuiCabecalho ? 1 : 0;
            for (int i = start; i < allRows.size(); i++) {
                T obj = info.converter.convert(allRows.get(i));
                objList.add(obj);
            }
        } catch(ArrayIndexOutOfBoundsException e){
            if(qtdTentativas >= 1) {
                System.out.println("Não foi possível ler a planilha CSV com os delimitadores ; e ,");
                throw e;
            }

            if(delimitador == ';')
                delimitador = ',';
            else
                delimitador = ';';

            qtdTentativas = qtdTentativas + 1;
            lerCsv(in, delimitador, qtdTentativas);
        }
        return objList;
    }

    private List<T> extrairAba(Sheet sheet) {
        List<T> objList = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        if (rowIterator.hasNext() && info.possuiCabecalho)
            rowIterator.next();

        while (rowIterator.hasNext()) {
            T obj = extrairObjeto(rowIterator);
            objList.add(obj);
        }

        return objList;
    }

    private T extrairObjeto(Iterator<Row> rowIterator) {
        Row row = rowIterator.next();
        Iterator<Cell> cellIterator = row.cellIterator();
        Object[] rowVals = new Object[row.getLastCellNum()];
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            rowVals[cell.getColumnIndex()] = obterValorCelula(cell);
        }
        return info.converter.convert(rowVals);
    }

    private boolean isExcel(InputStream is) throws Exception {
        return FileMagic.valueOf(is) == FileMagic.OOXML /* .xlsx */
                || FileMagic.valueOf(is) == FileMagic.OLE2; /* .xls */
    }

    private Object obterValorCelula(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_ERROR:
                return cell.getErrorCellValue();
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            case Cell.CELL_TYPE_BLANK:
                return null;
        }
        return null;
    }
}
