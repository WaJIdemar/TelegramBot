# TelegramBot
Это репозиторий с практической работой по ООП 2021-2022, студентов университета УрФУ института ИЕНиМ направления КБ второго курса: Завьялова Владимира, Кошелева Валерия.
# Deploy
Для развёртывания этого бота на сервере (например, Heroku) нужно:
1. Скопировать этот репозиторий себе
2. Подключить репозиторий к серверу
3. Добавить сестемные переменыые: 
  TELEGRAM_BOT_NAME - имя телеграм бота 
  TELEGRAM_BOT_TOKEN - токен телеграм бота 
  TELEGRAM_MODERATOR_GROUP_ID - ID группы модераторов или одного модератора 
  TELEGRAM_ADMIN_GROUP_ID - ID группы админов или одного дамина, куда будут отправляться информация об ошибках
  ID_VK_GROUP - ID группы ВК откуда будут браться посты и пересылаться в канал в telegram
  APP_VK_SECRET_KEY - секретный ключь приложения ВК 
  APP_VK_ID - ID приложения ВК 
  APP_VK_ACCESS_TOKEN - Токен приложения ВК
4. Сделать деплой
5. Включить бота
