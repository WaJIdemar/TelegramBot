package org.example.tgbot;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    public static final Integer countOfTerms = 19;
    public static Map<String, String> dictionary = new HashMap<String, String>();
    public static String[] dictionaryForRandom = new String[countOfTerms];

    public static void AddWordsInDictionary()    {
        AddNewTerm("Дайсы - игральные кости", 0);
        AddNewTerm("d - обозначения для игральных костей. Например, d6 - шестигранный куб, а d20 - " +
                "двадцатигранный", 1);
        AddNewTerm("Хб - изменения, внесённые ведущим в правила игры",2);
        AddNewTerm("Хоумрул - изменения, внесённые ведущим в правила игры",3);
        AddNewTerm("Симуляционизм - вера в игровые обстоятельства, атмосфера и цельность и логичность " +
                "мира",4);
        AddNewTerm("Чарник - лист, в котором заполняется информация о персонаже",5);
        AddNewTerm("Проверка - способ определить, удалось ди действие, заявленное игроком",6);
        AddNewTerm("Геймизм - желание победить или проиграть в ролевой игре, ожидание честного вызова",7);
        AddNewTerm("Нарратив - приоритет повествования над цельностью мира. Готовность пойти на уступки" +
                " ради истории",8);
        AddNewTerm("Ваншот - игра, сюжет которой рассчитан на одну встречу",9);
        AddNewTerm("Сериал - игра, сюжет которой рассчитан на несколько встреч",10);
        AddNewTerm("Компейн - игра, сюжет которой рассчитан на большое количество встреч",11);
        AddNewTerm("Система - набор правил игры",12);
        AddNewTerm("Сеттинг - место, время и условия действия, в которых происходит сюжет игры",13);
        AddNewTerm("Рольнулись - выпали случайным образом при броске игральные костей",14);
        AddNewTerm("Боёвка - часть правил игры/игровая ситуация, описывающая драки и прочие битвы",15);
        AddNewTerm("Экшн сцена - эпизод игры, наполненный активными действиями, например битва",16);
        AddNewTerm("Рулбук - книга правил игры", 17);
        AddNewTerm("Лор - информация о мире игры", 18);
    }

    private static void AddNewTerm(String term, Integer numberOfTerm)
    {
        dictionaryForRandom[numberOfTerm] = term;
        var termAndMeaning = dictionaryForRandom[numberOfTerm].split(" - ",2);
        dictionary.put(termAndMeaning[0], termAndMeaning[1]);
    }
}
