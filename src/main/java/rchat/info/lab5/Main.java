package rchat.info.lab5;

import dnl.utils.text.table.TextTable;
import rchat.info.libs.Coder;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Введите сообщение: ");
        String finput;
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        finput = r.readLine();

        AbstractMap.SimpleEntry<Double, Double> ans = Coder.encodeArithmeticEncodingTable(Arrays.asList(finput.split("")));

        double left = 0.0;
        double right = 1.0;
        double middle;
        int it = 0;

        while (true) {
            it++;
            middle = (right + left) / 2;

            if (middle > ans.getValue()) {
                right = middle;
            } else if (middle < ans.getKey()) {
                left = middle;
            } else break;
        }

        System.out.println("Интервал: " + ans);
        System.out.println("Число, характеризующее интервал: " + middle);
        System.out.println("Количество бит: " + it);
        System.out.println("Код: ");
        while (middle != 0) {
            System.out.print((int)(middle * 2) == 1 ? "1" : "0");
            middle *= 2;
            if (middle >= 1)
                middle -= 1;
        }
    }
}