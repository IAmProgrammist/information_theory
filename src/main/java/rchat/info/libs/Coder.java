package rchat.info.libs;

import java.util.*;
import java.util.stream.Collectors;

public class Coder {
    public static List<Byte> getEncodedRLE(List<Byte> origin) {
        List<Byte> result = new ArrayList<>();

        // Создаём дек для подсчёта элементов
        Deque<AbstractMap.SimpleEntry<Byte, Integer>> queue = new ArrayDeque<>();
        // Если массив пустой, то возвращаем пустой результат
        if (origin.isEmpty()) {
            return result;
        }

        // Добавляем в дек первый элемент
        queue.add(new AbstractMap.SimpleEntry<>(origin.get(0), 1));
        for (int i = 1; i < origin.size(); i++) {
            // Если байт элемента сверху равен текущему
            if (queue.peekLast().getKey().equals(origin.get(i))) {
                // То увеличиваем кол-во повторяющихся байтов
                AbstractMap.SimpleEntry<Byte, Integer> element = queue.pollLast();
                queue.add(new AbstractMap.SimpleEntry<>(element.getKey(), element.getValue() + 1));
            } else {
                // Иначе кладём в дек новый элемент
                queue.add(new AbstractMap.SimpleEntry<>(origin.get(i), 1));
            }
        }

        List<Byte> unrepeatingBuffer = new ArrayList<>();
        while (!queue.isEmpty()) {
            // Пока дек не пуст
            AbstractMap.SimpleEntry<Byte, Integer> element = queue.pollFirst();

            // Если встречен неповторяющийся элемент
            if (element.getValue() == 1) {
                // То добавляем его в буффер неповторяющихся элементов
                unrepeatingBuffer.add(element.getKey());
            } else {
                // Записываем неповторяющиеся элементы
                writeUnrepeatingBufferBytesRLE(unrepeatingBuffer, result);

                // В данном случае в последовательности
                // будут повторяющиеся элементы,
                // в старшем разряде информационного бита будет 1, а
                // следующие 7 бит будут содержать количество элементов + 2.

                // Прим: 1'000_1000 - следующие 8 + 2 байт будут неповторяющимися


                // Таким образом можно сохранить [2; 129] байт, поэтому
                // получаем срез байтов размером до 129 байт
                while (element.getValue() > 0) {
                    // Записываем в старший разряд инф. байта 1
                    byte info = (byte) (1 << 7);

                    // Записываем количество символов
                    info += (byte) (element.getValue() % 130 - 2);
                    result.add(info);
                    result.add(element.getKey());

                    element.setValue(element.getValue() - 129);
                }
            }
        }

        // Если остались неповторяющиеся элементы, записываем их
        writeUnrepeatingBufferBytesRLE(unrepeatingBuffer, result);

        return result;
    }

    private static void writeUnrepeatingBufferBytesRLE(List<Byte> unrepeatingBuffer, List<Byte> result) {
        // Записываем в массив байтов неповторяющиеся элементы
        while (!unrepeatingBuffer.isEmpty()) {
            // В данном случае в последовательности
            // будут неповторяющиеся элементы,
            // в старшем разряде информационного бита будет 0, а
            // следующие 7 бит будут содержать количество элементов + 1.

            // Прим: 0'000_1000 - следующие 8 + 1 байт будут неповторяющимися


            // Таким образом можно сохранить [1; 128] байт, поэтому
            // получаем срез байтов размером до 128 байт
            List<Byte> slice;
            try {
                slice = unrepeatingBuffer.subList(0, 128);
                unrepeatingBuffer = unrepeatingBuffer.subList(128, unrepeatingBuffer.size());
            } catch (IndexOutOfBoundsException e) {
                slice = new ArrayList<>(unrepeatingBuffer);
                unrepeatingBuffer.clear();
            }

            // Записываем информационный байт
            result.add((byte) (slice.size() - 1));

            // Записываем в результат неповторяющиеся байты
            result.addAll(slice);
        }
    }

    public static class TableElement {
        // Сам байт
        public byte symbol;
        // Количество повторений байта
        public int amount;
        // Код в виде массива булеанов
        public List<Boolean> code;

        public TableElement(byte symbol) {
            this.symbol = symbol;
            this.amount = 1;
            this.code = new ArrayList<>();
        }

        int getCode() {
            // Преобразование кода в вид int
            int ans = 0;
            for (boolean v : code) {
                ans <<= 1;
                ans += v ? 1 : 0;
            }

            return ans;
        }
    }

    private static List<Byte> getEncodedFromTable(List<Byte> input, List<TableElement> table) {
        // Создаём буффер байтов
        List<Byte> result = new ArrayList<>();
        Map<Byte, TableElement> codes = table.stream()
                .collect(Collectors.toMap(tableElement -> tableElement.symbol, tableElement -> tableElement));

        int bit = 0;
        // Создаём буффер для побитовой работы
        BitSet bitSet = new BitSet();

        // Для каждого элемента в последовательности
        for (Byte in : input) {
            // Поулчаем элемент в таблице Шеннона Фано
            TableElement elementSchennon = codes.get(in);

            // Записываем элемент в буффер
            for (int i = elementSchennon.code.size() - 1; i >= 0; i--) {
                bitSet.set(bit, ((elementSchennon.getCode() >> i) & 1) == 1);
                bit++;
            }
        }

        // Так как при таком кодировании может получиться последовательность, длина которой не
        // делится на 8, записываем в конец дополняющий байт. Он начинается с 1 и дополняет нулями байт до коцна.
        if (bit % 8 == 0) {
            // Если длина последовательности битов делится на 8, дозаписываем лишний байт
            // 10000000
            bitSet.set(bit, true);
            bitSet.set(bit + 1, bit + 8, false);
        } else {
            // Иначе - дозаписываем по правилу
            bitSet.set(bit++, true);
            while (bit % 8 != 0) {
                bitSet.set(bit++, false);
            }
        }

        // Копируем данные из битового буффера в байтовый
        byte tmp = 0;
        for (int i = 0; i < bit; i++) {
            if (i % 8 == 0 && i != 0) {
                result.add(tmp);
                tmp = 0;
            }

            tmp = (byte) (tmp * 2 + (bitSet.get(i) ? 1 : 0));
        }

        if (bit != 0)
            result.add(tmp);

        return result;
    }

    public static List<Byte> getEncodedSchennonFano(List<Byte> input) {
        List<TableElement> table = getSchennonFanoTable(input);

        return getEncodedFromTable(input, table);
    }

    public static List<Byte> getEncodedHuffman(List<Byte> input) {
        List<TableElement> table = getHuffmanTable(input);

        return getEncodedFromTable(input, table);
    }

    public static List<TableElement> getSchennonFanoTable(List<Byte> input) {
        List<TableElement> table = getSegmentisedTable(input);

        // Начинаем обработку таблицы
        getSchennonFanoTable(table, 0, table.size());

        return table;
    }

    private static List<TableElement> getSegmentisedTable(List<Byte> input) {
        // Подготовим таблицу для дальнейшего использования
        List<TableElement> table = new ArrayList<>();
        for (Byte symbol : input) {
            // Ищем уникальные байты. Если байт есть - увеличиваем его кол-во в таблице,
            // иначе - добавляем новый элемент.
            Optional<TableElement> result = table.stream().filter((el) -> el.symbol == symbol).findAny();
            if (result.isPresent()) {
                result.get().amount++;
            } else {
                table.add(new TableElement(symbol));
            }
        }

        // Сортируем таблицу по убыванию кол-ва появления символов
        table.sort(Comparator.comparingInt(o -> o.amount));
        Collections.reverse(table);
        return table;
    }

    private static void getSchennonFanoTable(List<TableElement> table, int beginIndex, int endIndex) {
        // Условия выхода из рекурсии
        if (endIndex - beginIndex <= 1) return;
        if (endIndex - beginIndex == 2) {
            table.get(beginIndex).code.add(true);
            table.get(beginIndex + 1).code.add(false);
            return;
        }

        // Получаем индекс, делящий элементы таблицы на две последовательности,
        // имеющие примерно одинаковые суммы
        int separateIndex = getSeparateIndex(table, beginIndex, endIndex);

        // Присвоение первой половине кода 1, второй - 0
        for (int i = beginIndex; i < endIndex; i++) {
            if (i < beginIndex + separateIndex) {
                table.get(i).code.add(true);
            } else {
                table.get(i).code.add(false);
            }
        }

        getSchennonFanoTable(table, beginIndex, separateIndex);
        getSchennonFanoTable(table, separateIndex, endIndex);
    }

    private static int getSeparateIndex(List<TableElement> table, int beginIndex, int endIndex) {
        // Посчитаем сумму всей таблицы
        int sum = 0;
        for (int i = beginIndex; i < endIndex; i++) {
            sum += table.get(i).amount;
        }

        // Сумма до элемента-разделителя
        int sumBefore = table.get(beginIndex).amount;
        // И после
        int sumAfter = sum - sumBefore;
        int separateIndex = beginIndex + 1;

        // Пока сумма до разделителя меньше суммы после разделителя, будем сдвигать разделитель
        while (separateIndex < endIndex - 1 && sumAfter - table.get(separateIndex).amount -
                (sumBefore + table.get(separateIndex).amount) > 0 ) {
            sumAfter -= table.get(separateIndex).amount;
            sumBefore += table.get(separateIndex).amount;
            separateIndex++;
        }

        // Будем выбирать, какая из позиций разделителя оптимальна.
        // Если разница сумм по модулю при ещё одном сдвиге
        // элемента меньше текущей, сдвигаем разделитель
        if (Math.abs(sumBefore - sumAfter) > Math.abs(sumAfter - table.get(separateIndex).amount -
                (sumBefore + table.get(separateIndex).amount))) {
            separateIndex++;
        }
        return separateIndex;
    }

    private static class HuffmanTableElement {
        TableElement self = null;
        HuffmanTableElement left = null;
        HuffmanTableElement right = null;
        int amount = 0;

        // Элемент дерева - лист
        public HuffmanTableElement(TableElement self) {
            this.self = self;
            this.amount = self.amount;
        }

        // Элемент - дерево
        public HuffmanTableElement(HuffmanTableElement left, HuffmanTableElement right) {
            this.right = right;
            this.left = left;

            this.amount = left.amount + right.amount;
        }

        List<TableElement> getTableElement() {
            if (self != null) {
                return List.of(new TableElement[]{self});
            }

            return getTableElement(List.of());
        }

        private List<TableElement> getTableElement(List<Boolean> prefix) {
            List<TableElement> result = new ArrayList<>();

            // Проход по дереву в глубину, формируется код
            if (self == null) {
                List<Boolean> newPrefix = new ArrayList<>(prefix);
                newPrefix.add(true);
                result.addAll(left.getTableElement(newPrefix));

                newPrefix = new ArrayList<>(prefix);
                newPrefix.add(false);
                result.addAll(right.getTableElement(newPrefix));
            } else {
                self.code = prefix;
                result.add(self);
            }

            return result;
        }
    }

    public static List<TableElement> getHuffmanTable(List<Byte> input) {
        Queue<HuffmanTableElement> queue = new PriorityQueue<>(
                Comparator.comparingInt(o -> o.amount));
        // Создаём приоритетную очередь
        queue.addAll(getSegmentisedTable(input).stream().map(HuffmanTableElement::new).collect(Collectors.toList()));

        while (queue.size() > 1) {
            // Получаем два элемента из таблицы с наименьшими кодами, формируем новый узел дерева,
            // снова сохраняем в очередь
            HuffmanTableElement left = queue.poll();
            HuffmanTableElement right = queue.poll();
            HuffmanTableElement newElement = new HuffmanTableElement(left, right);

            queue.add(newElement);
        }

        // Возвращаем итоговую таблицу
        return queue.poll().getTableElement();
    }

}
