package by.it.group410901.bandarzheuskaya.lesson10;

import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyArrayDeque<E> implements Deque<E> {
    private E[] elements;
    private int head;
    private int tail;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public MyArrayDeque() {
        elements = (E[]) new Object[DEFAULT_CAPACITY];
        head = 0;
        tail = 0;
        size = 0;
    }

    // Обязательные к реализации методы

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length; //вычисляем индекс учитывая кольцо
            sb.append(elements[index]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    @Override
    public void addFirst(E element) {
        ensureCapacity();
        head = (head - 1 + elements.length) % elements.length; //сдвигаем голову назад по кольцу
        elements[head] = element;
        size++;
    }

    @Override
    public void addLast(E element) {
        ensureCapacity();
        elements[tail] = element;
        tail = (tail + 1) % elements.length; //сдвигаем хвост вперед по кольцу
        size++;
    }

    //получение первого элемента без удаления
    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (size == 0) throw new NoSuchElementException("Deque is empty");
        return elements[head];
    }

    //получение последнего элемента без удаления
    @Override
    public E getLast() {
        if (size == 0) throw new NoSuchElementException("Deque is empty");
        return elements[(tail - 1 + elements.length) % elements.length];
    }

    //извлечение первого элемента
    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (size == 0) return null;
        E element = elements[head];
        elements[head] = null; //очищаем ссылку
        head = (head + 1) % elements.length; //сдвигаем голову вперед по кольцу
        size--;
        return element;
    }

    @Override
    public E pollLast() {
        if (size == 0) return null;
        tail = (tail - 1 + elements.length) % elements.length; //сдвигаем хвост назад по кольцу
        E element = elements[tail];
        elements[tail] = null;
        size--;
        return element;
    }

    //Итераторы
    // Итератор для обхода элементов от первого к последнему
    @Override
    public Iterator<E> iterator() {
        return new ArrayDequeIterator();
    }

    // Итератор для обхода элементов от последнего к первому
    @Override
    public Iterator<E> descendingIterator() {
        return new ArrayDequeDescendingIterator();
    }

    // Внутренний класс для прямого итератора
    private class ArrayDequeIterator implements Iterator<E> {
        private int currentIndex = 0;     // Текущая позиция относительно head
        private int returnedCount = 0;    // Количество возвращенных элементов

        @Override
        public boolean hasNext() {
            return returnedCount < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // Вычисляем фактический индекс в массиве
            int index = (head + currentIndex) % elements.length;
            E element = elements[index];
            currentIndex++;
            returnedCount++;
            return element;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // Внутренний класс для обратного итератора
    private class ArrayDequeDescendingIterator implements Iterator<E> {
        private int currentIndex = size - 1; // Текущая позиция относительно head (с конца)
        private int returnedCount = 0;       // Количество возвращенных элементов

        @Override
        public boolean hasNext() {
            return returnedCount < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // Вычисляем фактический индекс в массиве
            int index = (head + currentIndex) % elements.length;
            E element = elements[index];
            currentIndex--;
            returnedCount++;
            return element;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // Доп. методы для Deque
    // Предложение добавить элемент в начало (аналог addFirst, но возвращает boolean)
    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    // Предложение добавить элемент в конец (аналог addLast, но возвращает boolean)
    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    // Удаление первого элемента (аналог pollFirst, но бросает исключение если дек пуст)
    @Override
    public E removeFirst() {
        if (size == 0) throw new NoSuchElementException("Deque is empty");
        return pollFirst();
    }

    // Удаление последнего элемента (аналог pollLast, но бросает исключение если дек пуст)
    @Override
    public E removeLast() {
        if (size == 0) throw new NoSuchElementException("Deque is empty");
        return pollLast();
    }

    // Просмотр первого элемента без удаления (возвращает null если дек пуст)
    @Override
    public E peekFirst() {
        return (size == 0) ? null : getFirst();
    }

    // Просмотр последнего элемента без удаления (возвращает null если дек пуст)
    @Override
    public E peekLast() {
        return (size == 0) ? null : getLast();
    }

    // Удаление первого вхождения указанного элемента
    @Override
    public boolean removeFirstOccurrence(Object o) {
        // Проходим по всем элементам от начала к концу
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            // Проверяем совпадение (включая случай null)
            if ((elements[index] == null && o == null) ||
                    (elements[index] != null && elements[index].equals(o))) {
                removeAtIndex((head + i) % elements.length);
                return true;
            }
        }
        return false;
    }

    // Удаление последнего вхождения указанного элемента
    @Override
    public boolean removeLastOccurrence(Object o) {
        // Проходим по всем элементам от конца к началу
        for (int i = size - 1; i >= 0; i--) {
            int index = (head + i) % elements.length;
            // Проверяем совпадение (включая случай null)
            if ((elements[index] == null && o == null) ||
                    (elements[index] != null && elements[index].equals(o))) {
                removeAtIndex((head + i) % elements.length);
                return true;
            }
        }
        return false;
    }

    // Предложение добавить элемент в конец (для Queue интерфейса)
    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    // Удаление первого элемента (для Queue интерфейса)
    @Override
    public E remove() {
        return removeFirst();
    }

    // Просмотр первого элемента без удаления (для Queue интерфейса)
    @Override
    public E peek() {
        return peekFirst();
    }

    // Проверка пустоты дека
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Проверка наличия элемента в деке
    @Override
    public boolean contains(Object o) {
        // Линейный поиск по всем элементам
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            // Проверяем совпадение (включая случай null)
            if ((elements[index] == null && o == null) ||
                    (elements[index] != null && elements[index].equals(o))) {
                return true;
            }
        }
        return false;
    }

    //Вспомогательные методы
    // Удаление элемента по конкретному индексу в массиве
    private void removeAtIndex(int index) {
        if (index == head) {
            // Если удаляем первый элемент - используем pollFirst
            pollFirst();
        } else if (index == (tail - 1 + elements.length) % elements.length) {
            // Если удаляем последний элемент - используем pollLast
            pollLast();
        } else {
            // Удаление из середины - сложный случай
            // Сдвигаем все элементы после удаляемого влево
            int current = index;
            while (current != (tail - 1 + elements.length) % elements.length) {
                int next = (current + 1) % elements.length;
                elements[current] = elements[next];
                current = next;
            }
            // Обновляем хвост и очищаем последнюю позицию
            tail = (tail - 1 + elements.length) % elements.length;
            elements[tail] = null;
            size--;
        }
    }

    // Увеличение емкости массива при необходимости
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == elements.length) {
            // Создаем новый массив в 2 раза больше + 1
            E[] newElements = (E[]) new Object[elements.length * 2 + 1];

            // Копируем элементы от head до конца массива
            int firstPart = Math.min(size, elements.length - head);
            System.arraycopy(elements, head, newElements, 0, firstPart);

            // Копируем оставшиеся элементы из начала массива
            if (firstPart < size) {
                System.arraycopy(elements, 0, newElements, firstPart, size - firstPart);
            }

            // Заменяем старый массив новым
            elements = newElements;
            head = 0;
            tail = size;
        }
    }

    //Неподдерживаемые методы

    // Удаление элемента (используем удаление первого вхождения)
    @Override
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    // Добавление элемента в начало (для Stack интерфейса)
    @Override
    public void push(E e) {
        addFirst(e);
    }

    // Извлечение элемента из начала (для Stack интерфейса)
    @Override
    public E pop() {
        return removeFirst();
    }

    // Остальные методы Collection - не реализуем для упрощения

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    // Очистка дека - обнуляем все элементы и сбрасываем индексы
    @Override
    public void clear() {
        for (int i = 0; i < elements.length; i++) {
            elements[i] = null;
        }
        head = 0;
        tail = 0;
        size = 0;
    }
}