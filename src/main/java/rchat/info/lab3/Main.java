package rchat.info.lab3;

import rchat.info.libs.Coder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
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

    public static Experiment experiment2(List<Byte> origin) {
        try {
            List<Byte> RLE = Coder.getEncodedRLE(origin);
            System.out.println("RLE only: " + RLE.size() + " bytes, compression factor: " + 1.0 * origin.size() / RLE.size());
            return new Experiment(RLE.size(), "RLE only");
        } catch (OutOfMemoryError e) {
            System.out.println("RLE only: out of mem!");
        }

        return new Experiment(-1, "RLE only");
    }

    public static Experiment experiment3(List<Byte> origin) {
        try {
            List<Byte> SF = Coder.getEncodedSchennonFano(origin);
            System.out.println("SF only: " + SF.size() + " bytes, compression factor: " + 1.0 * origin.size() / SF.size());
            return new Experiment(SF.size(), "SF only");
        } catch (OutOfMemoryError e) {
            System.out.println("SF only: out of mem!");
        }

        return new Experiment(-1, "SF only");
    }

    public static Experiment experiment4(List<Byte> origin) {
        try {
            List<Byte> RLE = Coder.getEncodedRLE(origin);
            List<Byte> RLE2 = Coder.getEncodedRLE(RLE);
            System.out.println("RLE2: " + RLE2.size() + " bytes, compression factor: " + 1.0 * origin.size() / RLE2.size());
            return new Experiment(RLE2.size(), "RLE2");
        } catch (OutOfMemoryError e) {
            System.out.println("RLE2: out of mem!");
        }

        return new Experiment(-1, "RLE2");
    }

    public static Experiment experiment5(List<Byte> origin) {
        try {
            List<Byte> RLE = Coder.getEncodedRLE(origin);
            List<Byte> SF = Coder.getEncodedSchennonFano(RLE);
            System.out.println("RLE>SF: " + SF.size() + " bytes, compression factor: " + 1.0 * origin.size() / SF.size());
            return new Experiment(SF.size(), "RLE>SF");
        } catch (OutOfMemoryError e) {
            System.out.println("RLE>SF: out of mem!");
        }

        return new Experiment(-1, "RLE>SF");
    }

    public static Experiment experiment6(List<Byte> origin) {
        try {
            List<Byte> SF = Coder.getEncodedSchennonFano(origin);
            List<Byte> RLE = Coder.getEncodedRLE(SF);
            System.out.println("SF>RLE: " + RLE.size() + " bytes, compression factor: " + 1.0 * origin.size() / RLE.size());
            return new Experiment(RLE.size(), "SF>RLE");
        } catch (OutOfMemoryError e) {
            System.out.println("SF>RLE: out of mem!");
        }

        return new Experiment(-1, "SF>RLE");
    }

    public static Experiment experiment7(List<Byte> origin) {
        try {
            List<Byte> RLE1 = Coder.getEncodedRLE(origin);
            List<Byte> SF = Coder.getEncodedSchennonFano(RLE1);
            RLE1.clear();
            List<Byte> RLE2 = Coder.getEncodedRLE(SF);
            System.out.println("RLE>SF>RLE: " + RLE2.size() + " bytes, compression factor: " + 1.0 * origin.size() / RLE2.size());
            return new Experiment(RLE2.size(), "RLE>SF>RLE");
        } catch (OutOfMemoryError e) {
            System.out.println("RLE>SF>RLE: out of mem!");
        }

        return new Experiment(-1, "RLE>SF>RLE");
    }

    public static Experiment experiment8(List<Byte> origin) {
        try {
            List<Byte> SF = Coder.getEncodedRLE(origin);
            List<Byte> SF2 = Coder.getEncodedRLE(SF);
            System.out.println("SF2: " + SF2.size() + " bytes, compression factor: " + 1.0 * origin.size() / SF2.size());
            return new Experiment(SF2.size(), "SF2");
        } catch (OutOfMemoryError e) {
            System.out.println("SF2: out of mem!");
        }

        return new Experiment(-1, "SF2");
    }

    public static Experiment experiment9(List<Byte> origin) {
        try {
            List<Byte> HUF = Coder.getEncodedHuffman(origin);
            System.out.println("HUF only: " + HUF.size() + " bytes, compression factor: " + 1.0 * origin.size() / HUF.size());
            return new Experiment(HUF.size(), "HUF only");
        } catch (OutOfMemoryError e) {
            System.out.println("HUF only: out of mem!");
        }

        return new Experiment(-1, "HUF only");
    }

    public static Experiment experiment10(List<Byte> origin) {
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

    public static Experiment experiment11(List<Byte> origin) {
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

    public static Experiment experiment12(List<Byte> origin) {
        try {
            List<Byte> RLE1 = Coder.getEncodedRLE(origin);
            List<Byte> HUF = Coder.getEncodedSchennonFano(RLE1);
            RLE1.clear();
            List<Byte> RLE2 = Coder.getEncodedRLE(HUF);
            System.out.println("RLE>HUF>RLE: " + RLE2.size() + " bytes, compression factor: " + 1.0 * origin.size() / RLE2.size());
            return new Experiment(RLE2.size(), "RLE>HUF>RLE");
        } catch (OutOfMemoryError e) {
            System.out.println("RLE>HUF>RLE: out of mem!");
        }

        return new Experiment(-1, "RLE>HUF>RLE");
    }

    public static Experiment experiment13(List<Byte> origin) {
        try {
            List<Byte> HUF = Coder.getEncodedRLE(origin);
            List<Byte> HUF2 = Coder.getEncodedRLE(HUF);
            System.out.println("HUF2: " + HUF2.size() + " bytes, compression factor: " + 1.0 * origin.size() / HUF2.size());
            return new Experiment(HUF2.size(), "HUF2");
        } catch (OutOfMemoryError e) {
            System.out.println("HUF2: out of mem!");
        }

        return new Experiment(-1, "HUF2");
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
            experiments.add(experiment2(Arrays.asList(bytesConverted)));
            experiments.add(experiment3(Arrays.asList(bytesConverted)));
            experiments.add(experiment4(Arrays.asList(bytesConverted)));
            experiments.add(experiment5(Arrays.asList(bytesConverted)));
            experiments.add(experiment6(Arrays.asList(bytesConverted)));
            experiments.add(experiment7(Arrays.asList(bytesConverted)));
            experiments.add(experiment8(Arrays.asList(bytesConverted)));
            experiments.add(experiment9(Arrays.asList(bytesConverted)));
            experiments.add(experiment10(Arrays.asList(bytesConverted)));
            experiments.add(experiment11(Arrays.asList(bytesConverted)));
            experiments.add(experiment12(Arrays.asList(bytesConverted)));
            experiments.add(experiment13(Arrays.asList(bytesConverted)));

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
        for (File file : new File("src/assets/lab3").listFiles()) {
            makeExperiment(Paths.get(file.toURI()));
        }
    }
}