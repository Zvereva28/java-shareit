package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    @Autowired
    public ItemClient(RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory("http://server:9090" + "/items"))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public <T> ResponseEntity<Object> createItem(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public <T> ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(long itemId, long userId) {
        return get("/" + itemId, userId, null);
    }

    public ResponseEntity<Object> getAllUserItems(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchItems(long userId, String searchText, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", searchText,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public <T> ResponseEntity<Object> postComment(long userId, long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, null, commentDto);
    }
}
