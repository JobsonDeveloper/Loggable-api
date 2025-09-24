package br.com.jobson.controller;

import br.com.jobson.controller.dto.tweet.CreateTweetDto;
import br.com.jobson.controller.dto.tweet.EditTweetDto;
import br.com.jobson.controller.dto.tweet.FeedDto;
import br.com.jobson.controller.dto.tweet.FeedItemDto;
import br.com.jobson.domain.Role;
import br.com.jobson.domain.Tweet;
import br.com.jobson.repository.ITweetRepository;
import br.com.jobson.repository.IUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@Tag(name = "Tweet")
public class TweetController {

    private final ITweetRepository iTweetRepository;
    private final IUserRepository iUserRepository;

    public TweetController(ITweetRepository iTweetRepository, IUserRepository iUserRepository) {
        this.iTweetRepository = iTweetRepository;
        this.iUserRepository = iUserRepository;
    }

    // O "JwtAuthenticationToken" tráz o token já validado, possibilitando assim saber qual é o usuário que fez a requisição
    @PostMapping("/tweet/createNewTweet")
    @Operation(
            summary = "Register a tweet",
            description = "Register a new Tweet.",
            tags = {"Tweet"},
            parameters = {
                    @Parameter(
                            name = "content",
                            description = "Content of the new tweet.",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDto newDto, JwtAuthenticationToken token) {
        // Pega o UUID do usuário que fez a requisição
        var user = iUserRepository.findById(UUID.fromString(token.getName()));

        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(newDto.content());
        iTweetRepository.save(tweet);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/tweet/editTweet/{id}")
    @Operation(
            summary = "Edit a tweet",
            description = "Edit the content of the Tweet.",
            tags = {"Tweet"},
            parameters = {
                    @Parameter(
                            name = "content",
                            description = "New edited tweet content",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<Void> editTweet(
            @PathVariable("id") Long tweetId,
            @RequestBody EditTweetDto editedTweet,
            JwtAuthenticationToken token
    ) {
        // Verifica se o tweet existe
        var tweet = iTweetRepository.findById(tweetId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var realUser = tweet.getUser().getUserId().equals(UUID.fromString(token.getName())); // Verifica se o Tweet pertence ao usuário
        var user = iUserRepository.findById(UUID.fromString(token.getName())); // Coleta o usuário

        if (realUser) {
            var tweetInfo = new Tweet();
            tweetInfo.setUser(user.get());
            tweetInfo.setContent(editedTweet.content());
            tweetInfo.setTweetId(tweet.getTweetId());
            tweetInfo.setCreatedAt(tweet.getCreatedAt());

            iTweetRepository.save(tweetInfo);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tweet/deleteTweet/{id}")
    @Operation(
            summary = "Delete a tweet",
            description = "Deletes a registered tweet",
            tags = {"Tweet"}
    )
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId, JwtAuthenticationToken token) {
        // Verifica se o tweet realmente existe
        var tweet = iTweetRepository.findById(tweetId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // Verifica se o tweet pertence ao usuário que está tentando deletar
        var realUser = tweet.getUser().getUserId().equals(UUID.fromString(token.getName()));

        var users = iUserRepository.findById(UUID.fromString(token.getName()));

        //Verifica se o usuário é um ADMIN
        var isAdmin = users.get().getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (realUser || isAdmin) {
            iTweetRepository.deleteById(tweetId);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/tweet/getTweets")
    @Operation(
            summary = "List of the tweets",
            description = "List all registered tweets",
            tags = {"Tweet"}
    )
    public ResponseEntity<FeedDto> listTweets(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        var tweets = iTweetRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"))
                .map(tweet ->
                        new FeedItemDto(
                                tweet.getTweetId(),
                                tweet.getContent(),
                                tweet.getUser().getFirstName(),
                                tweet.getUser().getLastName()
                        )
                );

        return ResponseEntity.ok(new FeedDto(
                tweets.getContent(),
                page,
                pageSize,
                tweets.getTotalPages(),
                tweets.getTotalElements())
        );
    }
}
