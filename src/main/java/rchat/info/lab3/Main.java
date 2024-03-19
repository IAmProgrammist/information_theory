package rchat.info.lab3;

import rchat.info.libs.Coder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final boolean ENABLE_LATEX_OUTPUT = false;

    private static class Experiment {
        int size;
        String name;

        public Experiment(int size, String name) {
            this.size = size;
            this.name = name;
        }
    }

    public static Experiment experiment1(List<Byte> origin) {
        System.out.println("Original len: " + origin.size());
        return new Experiment(origin.size(), "Original");
    }

    public static Experiment HUFonly(List<Byte> origin) {
        try {
            List<Byte> HUF = Coder.getEncodedHuffman(origin);
            System.out.println("HUF only: " + HUF.size() + " bytes, compression factor: " + 1.0 * origin.size() / HUF.size());
            return new Experiment(HUF.size(), "HUF only");
        } catch (OutOfMemoryError e) {
            System.out.println("HUF only: out of mem!");
        }

        return new Experiment(-1, "HUF only");
    }

    public static Experiment RLEHuf(List<Byte> origin) {
        try {
            List<Byte> RLE = Coder.getEncodedRLE(origin);
            List<Byte> HUF = Coder.getEncodedSchennonFano(RLE);
            System.out.println("RLE>HUF: " + HUF.size() + " bytes, compression factor: " + 1.0 * origin.size() / HUF.size());
            return new Experiment(HUF.size(), "RLE>HUF");
        } catch (OutOfMemoryError e) {
            System.out.println("RLE>HUF: out of mem!");
        }

        return new Experiment(-1, "RLE>HUF");
    }

    public static Experiment HufRLE(List<Byte> origin) {
        try {
            List<Byte> HUF = Coder.getEncodedSchennonFano(origin);
            List<Byte> RLE = Coder.getEncodedRLE(HUF);
            System.out.println("HUF>RLE: " + RLE.size() + " bytes, compression factor: " + 1.0 * origin.size() / RLE.size());
            return new Experiment(RLE.size(), "HUF>RLE");
        } catch (OutOfMemoryError e) {
            System.out.println("HUF>RLE: out of mem!");
        }

        return new Experiment(-1, "HUF>RLE");
    }

    public static void makeExperiment(Path path) {

        try {
            byte[] bytesOrigin = Files.readAllBytes(path);

            Byte[] bytesConverted = new Byte[bytesOrigin.length];
            for (int i = 0; i < bytesOrigin.length; i++) {
                bytesConverted[i] = bytesOrigin[i];
            }

            System.out.println("Making an experiment for file: " + path.getFileName());
            System.out.println();

            List<Experiment> experiments = new ArrayList<>();
            Experiment original = experiment1(Arrays.asList(bytesConverted));
            experiments.add(original);
            experiments.add(HUFonly(Arrays.asList(bytesConverted)));
            experiments.add(RLEHuf(Arrays.asList(bytesConverted)));
            experiments.add(HufRLE(Arrays.asList(bytesConverted)));

            if (ENABLE_LATEX_OUTPUT) {
                experiments.sort(Comparator.comparingInt(o -> o.size));

                double scale = 4.0;
                double maxSize = experiments.get(experiments.size() - 1).size;

                for (Experiment c : experiments) {
                    if (c.size == -1) {
                        System.out.println(c.name + "\\multicolumn{3}{c}{Не прошёл}\\\\");
                        continue;
                    }
                    System.out.println(c.name +
                            "&\\begin{tikzpicture}\\filldraw [" +
                            (c.size > original.size ? "red" : (c.size < original.size ? "green" : "gray")) +
                            "] (0, 0) rectangle (" + scale * (c.size / maxSize) + ", 0.3);\n" +
                            "    \\end{tikzpicture} & " + String.format("%.3f", 1.0 * original.size / c.size).replace(',', '.') + "&" + c.size + "\\\\");
                }
            }
        } catch (IOException e) {
            System.out.println("File " + path.getFileName() + " not found");
        }

        System.out.println("======================");
    }

    public static void main(String[] args) {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите путь к файлу: ");
        try {
            File inputFile = new File(r.readLine());

            byte[] bytes = Files.readAllBytes(inputFile.toPath());
            Byte[] bytesConverted = new Byte[bytes.length];
            for (int i = 0; i < bytes.length; i++)
                bytesConverted[i] = bytes[i];

            {
                List<Byte> HUF = Coder.getEncodedHuffman(List.of(bytesConverted));
                System.out.println("Кодирование по алгоритму Хаффмана");
                System.out.println("Размер: " + HUF.size() + " байт");
                System.out.println("Коэффициент сжатия: " + (1.0 * bytesConverted.length / HUF.size()));
                System.out.println();
                File HUFOutput = new File(inputFile.getParent() + "/" + inputFile.getName().split("\\.")[0] + ".huf");

                byte[] HUFConverted = new byte[HUF.size()];
                for (int i = 0; i < HUF.size(); i++) {
                    HUFConverted[i] = HUF.get(i);
                }

                Files.write(HUFOutput.toPath(), HUFConverted);
                System.out.println("Результат записан в файл " + HUFOutput.getAbsolutePath());
            }

            System.out.println("===================");

            {
                List<Byte> HUF = Coder.getEncodedHuffman(List.of(bytesConverted));
                List<Byte> HUFRLE = Coder.getEncodedRLE(HUF);
                System.out.println("Кодирование по алгоритмам Хаффмана и RLE");
                System.out.println("Размер: " + HUFRLE.size() + " байт");
                System.out.println("Коэффициент сжатия: " + (1.0 * bytesConverted.length / HUFRLE.size()));
                System.out.println();
                File HUFRLEOutput = new File(inputFile.getParent() + "/" + inputFile.getName().split("\\.")[0] + ".hufrle");

                byte[] HUFRLEConverted = new byte[HUFRLE.size()];
                for (int i = 0; i < HUFRLE.size(); i++) {
                    HUFRLEConverted[i] = HUFRLE.get(i);
                }

                Files.write(HUFRLEOutput.toPath(), HUFRLEConverted);
                System.out.println("Результат записан в файл " + HUFRLEOutput.getAbsolutePath());
            }

            System.out.println("===================");


            {
                List<Byte> RLE = Coder.getEncodedRLE(List.of(bytesConverted));
                List<Byte> HUFRLE = Coder.getEncodedHuffman(RLE);
                System.out.println("Кодирование по алгоритмам RLE и Хаффмана");
                System.out.println("Размер: " + HUFRLE.size() + " байт");
                System.out.println("Коэффициент сжатия: " + (1.0 * bytesConverted.length / HUFRLE.size()));
                System.out.println();
                File HUFRLEOutput = new File(inputFile.getParent() + "/" + inputFile.getName().split("\\.")[0] + ".rlehuf");

                byte[] HUFRLEConverted = new byte[HUFRLE.size()];
                for (int i = 0; i < HUFRLE.size(); i++) {
                    HUFRLEConverted[i] = HUFRLE.get(i);
                }

                Files.write(HUFRLEOutput.toPath(), HUFRLEConverted);
                System.out.println("Результат записан в файл " + HUFRLEOutput.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Файл не найден");
        }
    }
}