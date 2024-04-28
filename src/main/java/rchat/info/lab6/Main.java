package rchat.info.lab6;

import dnl.utils.text.table.TextTable;
import org.apache.commons.lang.StringUtils;
import rchat.info.libs.Coder;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Введите сообщение: ");
        String finput;
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        finput = r.readLine();

        byte[] bytesOrigin = Files.readAllBytes(Path.of(finput));

        Byte[] bytesConverted = new Byte[bytesOrigin.length];
        for (int i = 0; i < bytesOrigin.length; i++) {
            bytesConverted[i] = bytesOrigin[i];
        }

        List<Coder.HilbertMurielTableElement> table = Coder.getHilbertMurielTableElement(List.of(bytesConverted));

        System.out.println("Таблица: ");
        TextTable tableText = new TextTable(new TableModel() {
            @Override
            public int getRowCount() {
                return table.size();
            }

            @Override
            public int getColumnCount() {
                return 6;
            }

            @Override
            public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return "Код символа";
                    case 1:
                        return "Вероятность";
                    case 2:
                        return "d";
                    case 3:
                        return "Дельта";
                    case 4:
                        return "Количество бит";
                    case 5:
                        return "Код";
                    default:
                        return "";
                }
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Integer.toString(table.get(rowIndex).symbol);
                    case 1:
                        return String.format("%.3f", table.get(rowIndex).p);
                    case 2:
                        return String.format("%.3f", table.get(rowIndex).d);
                    case 3:
                        return String.format("%.3f", table.get(rowIndex).delta);
                    case 4:
                        return Integer.toString(table.get(rowIndex).logp);
                    case 5:
                        String ans = "";
                        for (int i = 0; i < table.get(rowIndex).logp; i++) {
                            ans += (((table.get(rowIndex).code >> (table.get(rowIndex).logp - i - 1)) & 1) == 1 ? "1" : "0");
                        }
                        return ans;
                    default:
                        return "";
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                throw new UnsupportedOperationException("");
            }

            @Override
            public void addTableModelListener(TableModelListener l) {
            }

            @Override
            public void removeTableModelListener(TableModelListener l) {
            }
        });
        tableText.printTable();
        System.out.println();
        System.out.println("Закодированное сообщение: ");
        int sum = 0;
        String code = new ArrayList<>(List.of(bytesConverted)).stream().map(el -> {
            for (Coder.HilbertMurielTableElement element : table) {
                if (el != element.symbol) continue;
                String elementCode = "";
                for (int i = 0; i < element.logp; i++) {
                    elementCode += ((((element.code >> (element.logp - i - 1)) & 1) == 1 ? "1" : "0"));
                }

                return elementCode;
            }

            return "";
        }).collect(Collectors.joining());

        sum = bytesOrigin.length;

        System.out.println(code);
        System.out.println();

        int codedLength = code.length();
        int uncodedLength = bytesOrigin.length * 8;
        System.out.println("Коэффициент сжатия: " + 1.0 * uncodedLength / codedLength);
        System.out.println();
        double midLen = 0;
        for (Coder.HilbertMurielTableElement element : table) {
            String elementCode = "";
            for (int i = 0; i < element.logp; i++) {
                elementCode += ((((element.code >> (element.logp - i - 1)) & 1) == 1 ? "1" : "0"));
            }

            midLen += elementCode.length() * (1.0 * element.amount / sum);
        }
        System.out.println("Средняя длина: " + midLen);
        System.out.println();

        double delta = 0;
        for (Coder.HilbertMurielTableElement element : table) {
            String elementCode = "";
            for (int i = 0; i < element.logp; i++) {
                elementCode += ((((element.code >> (element.logp - i - 1)) & 1) == 1 ? "1" : "0"));
            }

            delta += (1.0 * element.amount / sum) * (elementCode.length() - midLen) * (elementCode.length() - midLen);
        }
        System.out.println("Дисперсия: " + delta);
    }
}