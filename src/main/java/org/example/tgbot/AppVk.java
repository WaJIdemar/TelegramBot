package org.example.tgbot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.objects.callback.longpoll.responses.GetLongPollEventsResponse;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk.api.sdk.objects.photos.PhotoSizesType;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppVk implements Runnable {
    private final ChatClient chatClient;
    private final int idVkGroup;
    private final VkApiClient vk;
    private final GroupActor groupActor;
    private final ServiceActor serviceActor;
    private final Gson g;
    private final Long groupIdSendPostTo;
    private final Long adminGroupId;
    private AppVkData appVkData;
    private AppVkTs appVkTs;

    public AppVk(int appVkId, String secretKey, String serviceKey, int vkGroupId, String accessToken, VkApiClient vk, AppVkData appVkData,
                 ChatClient chatClient, Long groupIdSendPostTo, Long adminGroupId) {
        this.chatClient = chatClient;
        this.idVkGroup = vkGroupId;
        this.vk = vk;
        this.groupActor = new GroupActor(vkGroupId, accessToken);
        this.serviceActor = new ServiceActor(appVkId, secretKey, serviceKey);
        this.groupIdSendPostTo = groupIdSendPostTo;
        this.adminGroupId = adminGroupId;
        this.appVkData = appVkData;
        g = new Gson();
    }

    @Override
    public void run() {
        boolean start = false;
        String server = null;
        String key = null;
        try {
            server = vk.groups().getLongPollServer(groupActor, idVkGroup).execute().getServer();
            key = vk.groups().getLongPollServer(groupActor, idVkGroup).execute().getKey();
            appVkTs = appVkData.getAppVkTs();
            start = true;
        } catch (Exception e) {
            chatClient.sendMessage(adminGroupId, "Ошибка при запуске appVk'a:" + "\n" + e);
        }
        while (start) {
            try {
                GetLongPollEventsResponse eventsResponse = vk.longPoll().getEvents(server, key, appVkTs.getTs()).execute();
                List<JsonObject> updates = eventsResponse.getUpdates();
                for (JsonObject jsonObject : updates) {
                    String title = jsonObject.get("type").getAsString();
                    if (Objects.equals(title, "wall_post_new")) {
                        Wallpost wallpost = g.fromJson(jsonObject.get("object"), Wallpost.class);
                        List<String> photosUrl = new ArrayList<>();
                        if (!Objects.equals(wallpost.getAttachments(), null)) {
                            for (WallpostAttachment wallpostAttachment : wallpost.getAttachments()) {
                                if (!Objects.equals(wallpostAttachment.getPhoto(), null)) {
                                    Photo photo = wallpostAttachment.getPhoto();
                                    String url = null;
                                    for (PhotoSizes photoSize : photo.getSizes()) {
                                        if (Objects.equals(photoSize.getType(), PhotoSizesType.Z)
                                                || Objects.equals(photoSize.getType(), PhotoSizesType.Y)
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
                String nextTs = eventsResponse.getTs();
                if (!Objects.equals(appVkTs.getTs(), nextTs)) {
                    appVkTs.changeTs(eventsResponse.getTs());
                    appVkData.changeAppVkTs(appVkTs);
                }
            } catch (Exception e) {
                chatClient.sendMessage(adminGroupId, "Ошибка при получении поста, appVk'a:" + "\n" + e);
                Integer nextTs = Integer.parseInt(appVkTs.getTs()) + 1;
                appVkTs.changeTs(Integer.toString(nextTs));
                appVkData.changeAppVkTs(appVkTs);
            }

        }

    }
}
