package org.example.tgbot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
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

    public AppVk(int id, String secretKey, String serviceKey, int idVkGroup, String accessToken, VkApiClient vk, ChatClient chatClient) {
        this.chatClient = chatClient;
        this.idVkGroup = idVkGroup;
        this.vk = vk;
        this.groupActor = new GroupActor(idVkGroup, accessToken);
        this.serviceActor = new ServiceActor(id, secretKey, serviceKey);
        g = new Gson();
    }

    @Override
    public void run() {
        try {
            String ts = vk.groups().getLongPollServer(groupActor, idVkGroup).execute().getTs();
            String server = vk.groups().getLongPollServer(groupActor, idVkGroup).execute().getServer();
            String key = vk.groups().getLongPollServer(groupActor, idVkGroup).execute().getKey();
            while (true) {
                GetLongPollEventsResponse eventsResponse = vk.longPoll().getEvents(server, key, ts).execute();
                List<JsonObject> updates = eventsResponse.getUpdates();
                for (JsonObject jsonObject : updates) {
                    String title = jsonObject.get("type").getAsString();
                    if (Objects.equals(title, "wall_post_new")) {
                        List<String> photosUrl = new ArrayList<>();
                        Wallpost wallpost = g.fromJson(jsonObject.get("object"), Wallpost.class);
                        if (!Objects.equals(wallpost.getAttachments(), null)) {
                            for (WallpostAttachment wallpostAttachment : wallpost.getAttachments()) {
                                if (!Objects.equals(wallpostAttachment.getPhoto(), null)) {
                                    Photo photo = wallpostAttachment.getPhoto();
                                    String url = null;
                                    for (PhotoSizes photoSize : photo.getSizes()) {
                                        if (Objects.equals(photoSize.getType(), PhotoSizesType.Z)) {
                                            url = photoSize.getUrl().toString();
                                        }
                                    }
                                    photosUrl.add(url);
                                }
                            }
                        }
                        chatClient.sendPostToChannel(wallpost.getText(), photosUrl,
                                "https://vk.com/wall-" + idVkGroup + "_" + wallpost.getId(),
                                new UrlKeyboard("https://vk.com/wall-" + idVkGroup + "_" + wallpost.getId()));
                    }
                }
                ts = eventsResponse.getTs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
