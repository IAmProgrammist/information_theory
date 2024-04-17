package rchat.info.lab2;

import rchat.info.libs.Coder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Введите сообщение: ");
        String input;
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        input = r.readLine();
        System.out.println();

        byte[] bytesOrigin = input.getBytes();

        Byte[] bytesConverted = new Byte[bytesOrigin.length];
        for (int i = 0; i < bytesOrigin.length; i++) {
            bytesConverted[i] = bytesOrigin[i];
        }

        List<Coder.TableElement<Byte>> table = Coder.getSchennonFanoTable(List.of(bytesConverted));

        System.out.println("Таблица: ");
        for (Coder.TableElement<Byte> element : table) {
            System.out.print(element.symbol);
            System.out.print(" " + element.amount + " ");
            for (int i = 0; i < element.code.size(); i++) {
                System.out.print(element.code.get(i) ? "1" : "0");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Закодированное сообщение: ");
        String code = input;
        int sum = 0;
        for (Coder.TableElement<Byte> element : table) {
            String elementCode = element.code.stream().map((el) -> el ? "1" : "0").collect(Collectors.joining(""));
            code = code.replace("" + element.symbol, elementCode);
            sum += element.amount;
        }
        System.out.println(code);
        System.out.println();

        int codedLength = code.length();
        int uncodedLength = input.length() * 8;
        System.out.println("Коэффициент сжатия: " + 1.0 * uncodedLength / codedLength);
        System.out.println();
        double midLen = 0;
        for (Coder.TableElement<Byte> element : table) {
            String elementCode = element.code.stream().map((el) -> el ? "1" : "0").collect(Collectors.joining(""));
            midLen += elementCode.length() * (1.0 * element.amount / sum);
        }
        System.out.println("Средняя длина: " + midLen);
        System.out.println();

        double delta = 0;
        for (Coder.TableElement<Byte> element : table) {
            String elementCode = element.code.stream().map((el) -> el ? "1" : "0").collect(Collectors.joining(""));
            delta += (1.0 * element.amount / sum) * (elementCode.length() - midLen) * (elementCode.length() - midLen);
        }
        System.out.println("Дисперсия: " + delta);
    }
}