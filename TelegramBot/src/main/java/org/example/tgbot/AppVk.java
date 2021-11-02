package org.example.tgbot;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.objects.enums.WallFilter;
import com.vk.api.sdk.objects.wall.WallpostFull;

public class AppVk {
    private final VkApiClient vk;
    private final ServiceActor actor;
    private WallpostFull lastPost;
    private final Integer idVkGroup;

    public AppVk(Integer appId, String appSecretKey, String appAccessToken, Integer idVkGroup, TransportClient transportClient) {
        vk = new VkApiClient(transportClient);
        actor = new ServiceActor(appId, appSecretKey, appAccessToken);
        this.idVkGroup = idVkGroup;
        lastPost = getLastPostOnWall();
    }

    public WallpostFull getLastPostOnWall() {
        WallpostFull postOnWall = new WallpostFull();
        try {
            postOnWall = vk.wall()
                    .get(actor)
                    .ownerId(idVkGroup)
                    .count(1)
                    .filter(WallFilter.OWNER)
                    .execute()
                    .getItems().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postOnWall;
    }

    public boolean postedNewPost() {
        WallpostFull postOnWall = getLastPostOnWall();
        if (lastPost.equals(postOnWall) || postOnWall.equals(new WallpostFull()))
            return false;
        else {
            lastPost = postOnWall;
            return true;
        }
    }

    public WallpostFull getLastPost(){
        return lastPost;
    }
}
