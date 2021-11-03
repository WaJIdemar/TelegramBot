package org.example.tgbot;

import java.util.*;

public class TermsDictionary {
    private final Map<String, String> termsDictionary = new HashMap<>();
    private final List<String> terms = new ArrayList<>();
    private final Random random = new Random();
    private final LevenshteinCalculator levenshteinCalculator = new LevenshteinCalculator();

    public TermsDictionary() {
        addWordsInDictionary();
    }

    private void addWordsInDictionary() {
        addNewTerm("дайсы", "игральные кости");
        addNewTerm("d", "обозначения для игральных костей. Например, d6 - шестигранный куб, а d20 - двадцатигранный");
        addNewTerm("хб", "изменения, внесённые ведущим в правила игры");
        addNewTerm("хоумрул", "изменения, внесённые ведущим в правила игры");
        addNewTerm("симуляционизм", "вера в игровые обстоятельства, атмосфера и цельность и логичность мира");
        addNewTerm("чарник", "лист, в котором заполняется информация о персонаже");
        addNewTerm("проверка", "способ определить, удалось ди действие, заявленное игроком");
        addNewTerm("геймизм", "желание победить или проиграть в ролевой игре, ожидание честного вызова");
        addNewTerm("нарратив", "приоритет повествования над цельностью мира. Готовность пойти на уступки" +
                " ради истории");
        addNewTerm("ваншот", "игра, сюжет которой рассчитан на одну встречу");
        addNewTerm("сериал", "игра, сюжет которой рассчитан на несколько встреч");
        addNewTerm("компейн", "игра, сюжет которой рассчитан на большое количество встреч");
        addNewTerm("система", "набор правил игры");
        addNewTerm("сеттинг", "место, время и условия действия, в которых происходит сюжет игры");
        addNewTerm("рольнулись", "выпали случайным образом при броске игральные костей");
        addNewTerm("боёвка", "часть правил игры/игровая ситуация, описывающая драки и прочие битвы");
        addNewTerm("экшн сцена", "эпизод игры, наполненный активными действиями, например битва");
        addNewTerm("рулбук", "книга правил игры");
        addNewTerm("лор", "информация о мире игры");
    }

    private void addNewTerm(String term, String definition) {
        terms.add(term + " - " + definition);
        termsDictionary.put(term, definition);
    }

    public String getCertainDefinition(String term) {
        return termsDictionary.get(term);
    }

    public Boolean containsTermOnDictionary(String term) {
        return termsDictionary.containsKey(term);
    }

    public String searchSimilarTermOnDictionary(String word) {
        double minVal = word.length();
        var minWord = "";
        for (Map.Entry<String, String> pair : termsDictionary.entrySet()) {
            var levenshtein = levenshteinCalculator.levenshteinDistance(pair.getKey(), word);
            if (levenshtein < minVal) {
                minVal = levenshtein;
                minWord = pair.getKey();
            }
        }
        if (Objects.equals(minWord, "") || minVal > 2) {
            return "";
        } else {
            return minWord;
        }
    }

    public String getRandomTerm() {
        return terms.get(random.nextInt(terms.size()));
    }
}