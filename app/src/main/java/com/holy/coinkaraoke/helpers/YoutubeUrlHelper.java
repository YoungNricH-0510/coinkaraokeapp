package com.holy.coinkaraoke.helpers;


public class YoutubeUrlHelper {

    public static String getYoutubeVideoId(String youtubeUrl) {

        String[] splitYoutubeUrlWeb = youtubeUrl.split("v=");
        if (splitYoutubeUrlWeb.length == 2 && splitYoutubeUrlWeb[1].length() == 11) {
            return splitYoutubeUrlWeb[1];
        }

        String[] splitYoutubeMobile = youtubeUrl.split(".be/");
        if (splitYoutubeMobile.length == 2 && splitYoutubeMobile[1].length() == 11) {
            return splitYoutubeMobile[1];
        }

        return null;
    }

}
