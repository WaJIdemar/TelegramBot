package org.example.tgbot;

public class LevenshteinCalculator {
    public double levenshteinDistance(String token1, String token2) {
        var opt = new int[token1.length() + 1][token2.length() + 1];
        for (var i = 0; i <= token1.length(); ++i)
            opt[i][0] = i;
        for (var i = 0; i <= token2.length(); ++i)
            opt[0][i] = i;
        for (var i = 1; i <= token1.length(); ++i)
            for (var j = 1; j <= token2.length(); ++j) {
                if (token1.charAt(i - 1) == token2.charAt(j - 1))
                    opt[i][j] = opt[i - 1][j - 1];
                else
                    opt[i][j] = 1 + Math.min(Math.min(opt[i - 1][j], opt[i - 1][j - 1]), opt[i][j - 1]);
            }
        return opt[token1.length()][token2.length()];
    }
}
