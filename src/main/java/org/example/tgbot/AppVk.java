package org.example.tgbot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.objects.callback.longpoll.responses.GetLongPollEventsResponse;
import com.vk.api.sdk.objects.wall.Wallpost;

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

    public AppVk(int appVkId, String secretKey, String serviceKey, int vkGroupId, String accessToken, VkApiClient vk,
                 ChatClient chatClient, Long groupIdSendPostTo, Long adminGroupId) {
        this.chatClient = chatClient;
        this.idVkGroup = vkGroupId;
        this.vk = vk;
        this.groupActor = new GroupActor(vkGroupId, accessToken);
        this.serviceActor = new ServiceActor(appVkId, secretKey, serviceKey);
        this.groupIdSendPostTo = groupIdSendPostTo;
        this.adminGroupId = adminGroupId;
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
//                        List<String> photosUrl = new ArrayList<>();
                        Wallpost wallpost = g.fromJson(jsonObject.get("object"), Wallpost.class);
//                        if (!Objects.equals(wallpost.getAttachments(), null)) {
//                            for (WallpostAttachment wallpostAttachment : wallpost.getAttachments()) {
//                                if (!Objects.equals(wallpostAttachment.getPhoto(), null)) {
//                                    Photo photo = wallpostAttachment.getPhoto();
//                                    String url = null;
//                                    boolean typeZ = false;
//                                    for (PhotoSizes photoSize : photo.getSizes()) {
//                                        if (Objects.equals(photoSize.getType(), PhotoSizesType.Z)) {
//                                            url = photoSize.getUrl().toString();
//                                            typeZ = true;
//                                        }
//                                        else if (Objects.equals(photoSize.getType(), PhotoSizesType.R) && !typeZ)
//                                            url = photoSize.getUrl().toString();
//                                    }
//                                    photosUrl.add(url);
//                                }
//                            }
//                        }
                        chatClient.sendMessage(groupIdSendPostTo, wallpost.getText() + "\n\n" + "https://vk.com/wall-" + idVkGroup + "_" + wallpost.getId(),
                                new OpenInVkKeyboard("https://vk.com/wall-" + idVkGroup + "_" + wallpost.getId()));
                    }
                }
                ts = eventsResponse.getTs();
            }
        } catch (Exception e) {
            chatClient.sendMessage(adminGroupId, "Ошибка appVk'a:" + "\n" + e);
        }
    }
}
