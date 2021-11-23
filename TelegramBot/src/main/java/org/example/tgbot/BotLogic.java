package org.example.tgbot;

import java.util.*;

public class BotLogic {
    private final Map<Long, User> users = new HashMap<>();
    private final TermsDictionary termsDictionary = new TermsDictionary();
    private final StandardResponsesToUser standardResponsesToUser = new StandardResponsesToUser();
    private final StandardUserRequest standardUserRequest = new StandardUserRequest();
    private final ChatClient chatClient;

    public BotLogic(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public void respondUser(Long id, String message) {
        if(Objects.equals(id, chatClient.getModeratorGroupId()))
            return;
        message = message.toLowerCase(Locale.ROOT);
        if (!users.containsKey(id)) {
            users.put(id, new User(id));
            chatClient.sendMessage(id, standardResponsesToUser.startMessage, new DefaultKeyboard());
            return;
        }
        var request = parsUserMessage(message);
        switch (users.get(id).getDialogState()) {
            case WAIT_TERM -> aggregateWaitTermUser(id, message, request); // Ждём пока пользователь введёт конкретный термин
            case WAIT_WORD_INPUT -> aggregateWaitWordInput(id, message, request); // Ждём пока пользователь введёт слово, чтобы выдать определение или попросить написать определение
            case WAIT_DEFINITION -> aggregateWaitDefinition(id, message, request); // Ждём опеределение от пользователя
            case WAIT_CONFIRMATION_DEFINITION_INPUT -> aggregateWaitConfirmationDefinitionInput(id, request);// Ждём подтверждения, что пользователь хочет написать определение
            case WAIT_RANDOM_OR_CERTAIN_TERM -> aggregateWaitRandomOrCertainTerm(id, request); // Ждём какое определение хочет пользователь конкретно или конкретное
            case DEFAULT -> aggregateDefaultState(id, request); // Стандартное состояние

        }
    }

    private void aggregateWaitTermUser(long id, String message, Requests request) {
        if (request == Requests.CANCEL) {
            users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            chatClient.sendMessage(id, standardResponsesToUser.cancel, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
        } else {
            if (termsDictionary.containsTermOnDictionary(message)) {
                TermDefinition termDefinition = termsDictionary.getCertainDefinition(message);
                String text = termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                        + " - " + termDefinition.definition;
                chatClient.sendMessage(id, text, new RandomOrCertainTermKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            } else {
                users.get(id).changeUserTerm(message);
                ArrayList<String> similarTerm = termsDictionary.searchSimilarTermOnDictionary(message);
                users.get(id).changeUserSimilarWordsTermsDictionary(similarTerm);

                if (Objects.equals(similarTerm, new ArrayList<String>())) {
                    chatClient.sendMessage(id, standardResponsesToUser.termNotFound.formatted(message), new YesOrNoKeyboard());
                    users.get(id).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);
                } else {
                    users.get(id).changeUserSimilarWordsTermsDictionary(similarTerm);
                    chatClient.sendMessage(id, standardResponsesToUser.userAgree.formatted(String.join(", ", similarTerm)),
                            new CancelKeyboard());
                    users.get(id).changeDialogState(DialogState.WAIT_WORD_INPUT);
                }
            }
        }
    }

    private void aggregateWaitWordInput(long id, String message, Requests request) {
        if (request == Requests.CANCEL) {
            chatClient.sendMessage(id, standardResponsesToUser.writeDefinition.formatted(users.get(id).getUserTerm()), new YesOrNoKeyboard());
            users.get(id).changeDialogState(DialogState.WAIT_CONFIRMATION_DEFINITION_INPUT);

        } else if (users.get(id).getUserSimilarWordsTermsDictionary().contains(message)) {
            TermDefinition termDefinition = termsDictionary.getCertainDefinition(message);
            String text = termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                    + " - " + termDefinition.definition;
            chatClient.sendMessage(id, text, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
            users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
        } else {
            chatClient.sendMessage(id, standardResponsesToUser.invalidInputWaitWord, new CancelKeyboard());
        }
    }

    private void aggregateWaitDefinition(long id, String message, Requests request) {
        if (request == Requests.CANCEL)
            chatClient.sendMessage(id, standardResponsesToUser.cancel, new RandomOrCertainTermKeyboard());
        else {
            chatClient.sendMessage(id, standardResponsesToUser.definitionSentForConsideration, new RandomOrCertainTermKeyboard());
            chatClient.sendMessageModeratorGroup(new TermDefinition(users.get(id).getUserTerm(), message));
        }
        chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
        users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
    }


    private void aggregateWaitConfirmationDefinitionInput(long id, Requests request) {
        if (request == Requests.YES) {
            chatClient.sendMessage(id, standardResponsesToUser.waitDefinition.formatted(users.get(id).getUserTerm()), new CancelKeyboard());
            users.get(id).changeDialogState(DialogState.WAIT_DEFINITION);
        } else if (request == Requests.NO) {
            chatClient.sendMessage(id, standardResponsesToUser.cancel, new RandomOrCertainTermKeyboard());
            chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
            users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
        } else {
            chatClient.sendMessage(id, standardResponsesToUser.invalidInputYesOrNo, new YesOrNoKeyboard());
        }
    }

    private void aggregateWaitRandomOrCertainTerm(long id, Requests request) {
        switch (request) {
            case RANDOM_TERM -> {
                TermDefinition termDefinition = termsDictionary.getRandomTerm();
                String text = termDefinition.term.substring(0, 1).toUpperCase() + termDefinition.term.substring(1)
                        + " - " + termDefinition.definition;
                chatClient.sendMessage(id, text, new RandomOrCertainTermKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
            case CERTAIN_TERM -> {
                chatClient.sendMessage(id, standardResponsesToUser.waitTerm, new CancelKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_TERM);
            }
            case BACK -> {
                chatClient.sendMessage(id, standardResponsesToUser.cancel, new DefaultKeyboard());
                users.get(id).changeDialogState(DialogState.DEFAULT);
            }
            default -> {
                chatClient.sendMessage(id, standardResponsesToUser.invalidInputRandomOrCertain,
                        new RandomOrCertainTermKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
        }
    }

    private void aggregateDefaultState(long id, Requests request) {
        switch (request) {
            case HELP -> chatClient.sendMessage(id, standardResponsesToUser.helpMessage, new DefaultKeyboard());
            case GREETING -> chatClient.sendMessage(id, standardResponsesToUser.gettingMessage, new DefaultKeyboard());
            case OUT_TERM -> {
                chatClient.sendMessage(id, standardResponsesToUser.outTerm, new RandomOrCertainTermKeyboard());
                users.get(id).changeDialogState(DialogState.WAIT_RANDOM_OR_CERTAIN_TERM);
            }
            default -> {
                chatClient.sendMessage(id, standardResponsesToUser.unknownCommand, new DefaultKeyboard());
                users.get(id).changeDialogState(DialogState.DEFAULT);
            }
        }
    }

    private Requests parsUserMessage(String message) {
        if (Objects.equals(message, standardUserRequest.help))
            return Requests.HELP;
        if (Objects.equals(message, standardUserRequest.getting))
            return Requests.GREETING;
        if (Objects.equals(message, standardUserRequest.outTerm))
            return Requests.OUT_TERM;
        if (Objects.equals(message, standardUserRequest.outRandomTerm)
                || Objects.equals(message, standardUserRequest.outRandomTerm2))
            return Requests.RANDOM_TERM;
        if (Objects.equals(message, standardUserRequest.outCertainTerm)
                || Objects.equals(message, standardUserRequest.outCertainTerm2))
            return Requests.CERTAIN_TERM;
        if (Objects.equals(message, standardUserRequest.cancel))
            return Requests.CANCEL;
        if (Objects.equals(message, standardUserRequest.yes))
            return Requests.YES;
        if (Objects.equals(message, standardUserRequest.no))
            return Requests.NO;
        if (Objects.equals(message, standardUserRequest.back))
            return Requests.BACK;
        return Requests.UNKNOWN_COMMAND;
    }
}
