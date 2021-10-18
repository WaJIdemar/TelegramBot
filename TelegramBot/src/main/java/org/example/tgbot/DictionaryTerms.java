package org.example.tgbot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DictionaryTerms {
    private final Integer countOfTerms = 19;
    private final Map<String, String> dictionaryTerms = new HashMap<>();
    private final String[] dictionaryForRandom = new String[countOfTerms];
    private Integer numberOfTerm = 0;
    private final Random random = new Random();

    public DictionaryTerms() {
        addWordsInDictionary();
    }

    private void addWordsInDictionary() {
        addNewTerm("Дайсы", "игральные кости");
        addNewTerm("d", "обозначения для игральных костей. Например, d6 - шестигранный куб, а d20 - двадцатигранный");
        addNewTerm("Хб", "изменения, внесённые ведущим в правила игры");
        addNewTerm("Хоумрул", "изменения, внесённые ведущим в правила игры");
        addNewTerm("Симуляционизм", "вера в игровые обстоятельства, атмосфера и цельность и логичность мира");
        addNewTerm("Чарник", "лист, в котором заполняется информация о персонаже");
        addNewTerm("Проверка", "способ определить, удалось ди действие, заявленное игроком");
        addNewTerm("Геймизм", "желание победить или проиграть в ролевой игре, ожидание честного вызова");
        addNewTerm("Нарратив", "приоритет повествования над цельностью мира. Готовность пойти на уступки" +
                " ради истории");
        addNewTerm("Ваншот", "игра, сюжет которой рассчитан на одну встречу");
        addNewTerm("Сериал", "игра, сюжет которой рассчитан на несколько встреч");
        addNewTerm("Компейн", "игра, сюжет которой рассчитан на большое количество встреч");
        addNewTerm("Система", "набор правил игры");
        addNewTerm("Сеттинг", "место, время и условия действия, в которых происходит сюжет игры");
        addNewTerm("Рольнулись", "выпали случайным образом при броске игральные костей");
        addNewTerm("Боёвка", "часть правил игры/игровая ситуация, описывающая драки и прочие битвы");
        addNewTerm("Экшн сцена", "эпизод игры, наполненный активными действиями, например битва");
        addNewTerm("Рулбук", "книга правил игры");
        addNewTerm("Лор", "информация о мире игры");
    }

    private void addNewTerm(String term, String definition) {
        dictionaryForRandom[numberOfTerm] = term + " - " + definition;
        dictionaryTerms.put(term, definition);
        numberOfTerm++;
    }

    public String getRandomTerm() {
        return dictionaryForRandom[random.nextInt(countOfTerms)];
    }
}
