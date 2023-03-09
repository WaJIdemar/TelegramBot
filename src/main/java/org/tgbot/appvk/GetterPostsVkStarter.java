package org.tgbot.appvk;

public class GetterPostsVkStarter {

    public GetterPostsVkStarter(GetterPostsVk getterPostsVk){
        Thread getterPostsVkThread = new Thread(getterPostsVk);
        getterPostsVkThread.start();
    }
}
