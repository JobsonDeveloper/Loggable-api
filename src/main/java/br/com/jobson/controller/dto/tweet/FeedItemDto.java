package br.com.jobson.controller.dto.tweet;

public record FeedItemDto(
        Long tweetId,
        String content,
        String firstName,
        String lastName
) {

}
