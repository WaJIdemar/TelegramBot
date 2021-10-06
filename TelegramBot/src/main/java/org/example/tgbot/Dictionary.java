package org.example.tgbot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Dictionary {
    private final Integer countOfTerms = 19;
    private final Map<String, String> dictionary = new HashMap<String, String>();
    private final String[] dictionaryForRandom = new String[countOfTerms];

    public Dictionary()
    {
        addWordsInDictionary();
    }

    private void addWordsInDictionary() {
        addNewTerm("Дайсы - игральные кости", 0);
        addNewTerm("d - обозначения для игральных костей. Например, d6 - шестигранный куб, а d20 - " +
                "двадцатигранный", 1);
        addNewTerm("Хб - изменения, внесённые ведущим в правила игры", 2);
        addNewTerm("Хоумрул - изменения, внесённые ведущим в правила игры", 3);
        addNewTerm("Симуляционизм - вера в игровые обстоятельства, атмосфера и цельность и логичность " +
                "мира", 4);
        addNewTerm("Чарник - лист, в котором заполняется информация о персонаже", 5);
        addNewTerm("Проверка - способ определить, удалось ди действие, заявленное игроком", 6);
        addNewTerm("Геймизм - желание победить или проиграть в ролевой игре, ожидание честного вызова", 7);
        addNewTerm("Нарратив - приоритет повествования над цельностью мира. Готовность пойти на уступки" +
                " ради истории", 8);
        addNewTerm("Ваншот - игра, сюжет которой рассчитан на одну встречу", 9);
        addNewTerm("Сериал - игра, сюжет которой рассчитан на несколько встреч", 10);
        addNewTerm("Компейн - игра, сюжет которой рассчитан на большое количество встреч", 11);
        addNewTerm("Система - набор правил игры", 12);
        addNewTerm("Сеттинг - место, время и условия действия, в которых происходит сюжет игры", 13);
        addNewTerm("Рольнулись - выпали случайным образом при броске игральные костей", 14);
        addNewTerm("Боёвка - часть правил игры/игровая ситуация, описывающая драки и прочие битвы", 15);
        addNewTerm("Экшн сцена - эпизод игры, наполненный активными действиями, например битва", 16);
        addNewTerm("Рулбук - книга правил игры", 17);
        addNewTerm("Лор - информация о мире игры", 18);
    }

    private void addNewTerm(String term, Integer numberOfTerm) {
        dictionaryForRandom[numberOfTerm] = term;
        var termAndMeaning = dictionaryForRandom[numberOfTerm].split(" - ", 2);
        dictionary.put(termAndMeaning[0], termAndMeaning[1]);
    }

    public String getTerm()
    {
        return dictionaryForRandom[new Random().nextInt(countOfTerms)];
    }
}
