package org.example.tgbot.appvk;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.LongPollServerKeyExpiredException;
import com.vk.api.sdk.exceptions.LongPollServerTsException;
import com.vk.api.sdk.objects.callback.longpoll.responses.GetLongPollEventsResponse;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk.api.sdk.objects.photos.PhotoSizesType;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import org.example.tgbot.ChatClient;
import org.example.tgbot.keyboards.OpenInVkKeyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetterPostsVk implements Runnable {
    private final ChatClient chatClient;
    private final int idVkGroup;
    private final VkApiClient vk;
    private final GroupActor groupActor;
    private final ServiceActor serviceActor;
    private final Gson g;
    private final Long groupIdSendPostTo;
    private final Long adminGroupId;
    private final GetterPostsVkDatabase getterPostsVkDatabase;
    private AppVkTs appVkTs;
    private boolean errorStatus;

    public GetterPostsVk(int appVkId, String secretKey, String serviceKey, int vkGroupId, String accessToken, VkApiClient vk, GetterPostsVkDatabase getterPostsVkDatabase,
                         ChatClient chatClient, Long groupIdSendPostTo, Long adminGroupId) {
        this.chatClient = chatClient;
        this.idVkGroup = vkGroupId;
        this.vk = vk;
        this.groupActor = new GroupActor(vkGroupId, accessToken);
        this.serviceActor = new ServiceActor(appVkId, secretKey, serviceKey);
        this.groupIdSendPostTo = groupIdSendPostTo;
        this.adminGroupId = adminGroupId;
        this.getterPostsVkDatabase = getterPostsVkDatabase;
        g = new Gson();
        errorStatus = false;
    }

    @Override
    public void run() {
        String server = null;
        String key = null;
        appVkTs = getterPostsVkDatabase.getAppVkTs();
        try {
            server = vk.groups().getLongPollServer(groupActor, idVkGroup).execute().getServer();
            key = vk.groups().getLongPollServer(groupActor, idVkGroup).execute().getKey();
        } catch (Exception e) {
            chatClient.sendMessage(adminGroupId, "Ошибка при получении ключа или сервера для appVk'a:" + "\n" + e.getMessage());
            return;
        }
        while (true) {
            GetLongPollEventsResponse eventsResponse = getLongPollEventsResponse(server, key);
            if (errorStatus)
                return;
            if (eventsResponse == null)
                continue;
            try {
                List<JsonObject> updates = eventsResponse.getUpdates();
                for (JsonObject jsonObject : updates) {
                    String title = jsonObject.get("type").getAsString();
                    if (Objects.equals(title, "wall_post_new")) {
                        acceptNewPostVk(jsonObject);
                    }
                }
                String nextTs = eventsResponse.getTs();
                if (!Objects.equals(appVkTs.getTs(), nextTs)) {
                    appVkTs.changeTs(nextTs); // заменить
                    getterPostsVkDatabase.changeAppVkTs(appVkTs);
                }
            } catch (Exception e) {
                chatClient.sendMessage(adminGroupId, "Ошибка при обработке поста в appVk'a:" + "\n" + e.getMessage());
                return;
            }
        }
    }

    private GetLongPollEventsResponse getLongPollEventsResponse(String server, String key){
        GetLongPollEventsResponse eventsResponse = null;
        try {
            eventsResponse = vk.longPoll().getEvents(server, key, appVkTs.getTs()).execute();
        } catch (LongPollServerKeyExpiredException e) {
            try {
                key = vk.groups().getLongPollServer(groupActor, idVkGroup).execute().getKey();
            } catch (Exception ex) {
                chatClient.sendMessage(adminGroupId, "Ошибка при получении ключа для appVk'a:" + "\n" + ex.getMessage());
                errorStatus = true;
            }
        } catch (LongPollServerTsException e) {
            try {
                appVkTs.changeTs(vk.groups().getLongPollServer(groupActor, idVkGroup).execute().getTs());
            } catch (Exception ex) {
                chatClient.sendMessage(adminGroupId, "Ошибка при получении нового номера события для appVk'a:" + "\n" + ex.getMessage());
                errorStatus = true;
            }
        } catch (Exception e) {
            chatClient.sendMessage(adminGroupId, "Ошибка при получении поста appVk'a:" + "\n" + e.getMessage());
            errorStatus = true;
        }
        return eventsResponse;
    }

    private void acceptNewPostVk(JsonObject jsonObject) {
        Wallpost wallpost = g.fromJson(jsonObject.get("object"), Wallpost.class);
        List<String> photosUrl = new ArrayList<>();
        if (!Objects.equals(wallpost.getAttachments(), null)) {
            for (WallpostAttachment wallpostAttachment : wallpost.getAttachments()) {
                if (!Objects.equals(wallpostAttachment.getPhoto(), null)) {
                    Photo photo = wallpostAttachment.getPhoto();
                    String url = null;
                    for (PhotoSizes photoSize : photo.getSizes()) {
                        if (Objects.equals(photoSize.getType(), PhotoSizesType.Z)
                                || Objects.equals(photoSize.getType(), PhotoSizesType.X)) {
                            url = photoSize.getUrl().toString();
                        }
                    }
                    photosUrl.add(url);
                }
            }
        }
        if (photosUrl.size() == 1) {
            chatClient.sendMessage(groupIdSendPostTo,
                    wallpost.getText() + "\n\n" + "https://vk.com/wall-" + idVkGroup + "_" + wallpost.getId(),
                    photosUrl.get(0),
                    new OpenInVkKeyboard("https://vk.com/wall-" + idVkGroup + "_" + wallpost.getId()));
        } else if (photosUrl.size() == 0)
            chatClient.sendMessage(groupIdSendPostTo,
                    wallpost.getText() + "\n\n" + "https://vk.com/wall-" + idVkGroup + "_" + wallpost.getId(),
                    new OpenInVkKeyboard("https://vk.com/wall-" + idVkGroup + "_" + wallpost.getId()));
        else
            chatClient.sendMessage(groupIdSendPostTo,
                    wallpost.getText() + "\n\n" + "https://vk.com/wall-" + idVkGroup + "_" + wallpost.getId(),
                    photosUrl,
                    new OpenInVkKeyboard("https://vk.com/wall-" + idVkGroup + "_" + wallpost.getId()));
    }
}
