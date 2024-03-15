package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;

@Service
public class UserClient extends BaseClient {

    @Autowired
    public UserClient(RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory("http://server:9090" + "/users"))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDTO clientDTO) {
        return post("", clientDTO);
    }

    public ResponseEntity<Object> deleteUser(long userId) {
        return delete("/" + userId);
    }

    public ResponseEntity<Object> updateUser(long userId, UserDTO clientDTO) {
        return patch("/" + userId, clientDTO);
    }

    public ResponseEntity<Object> getUser(long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

}
